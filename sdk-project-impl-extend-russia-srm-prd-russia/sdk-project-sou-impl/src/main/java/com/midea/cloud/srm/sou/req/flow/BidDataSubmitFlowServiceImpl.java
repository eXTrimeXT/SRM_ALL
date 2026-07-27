package com.midea.cloud.srm.sou.req.flow;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.BidDataSubmitEvaluator;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.BidDataSubmitStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 功能名称
 * @author 100014337
 */
@Slf4j
@Service
public class BidDataSubmitFlowServiceImpl implements IFlowBusinessCallbackService {
    @Autowired
    protected QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Resource
    private PjSouClient pjSouClient;

    @Value("${bpm.zbzldj.processGroupId}")
    private String processGroupId;
    @Value("${bpm.zbzldj.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private BaseExtClient baseExtClient;
    private static final String BUSINESS_TYPE = "bidDataSubmit";
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        //临时修改，提交就审批通过
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                this.updateStatus(businessId, param, BidDataSubmitStatusEnum.APPROVING.toString());

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUBMIT_BUYER)
                        .set(BidDataSubmit::getStartBpmUsername, loginAppUser.getUsername())
                        .set(BidDataSubmit::getStartBpmNickname, loginAppUser.getNickname())
                        .eq(BidDataSubmit::getDataSubmitId, businessId));
                pjSouClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            this.updateStatus(businessId, param, BidDataSubmitStatusEnum.APPROVING.toString());
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, BidDataSubmitStatusEnum.APPROVED.toString());
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, BidDataSubmitStatusEnum.REJECTED.toString());
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, BidDataSubmitStatusEnum.WITHDRAW.toString());
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    public static String getStatusZhName(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("DRAFT", "拟定");
        map.put("SUBMITTED", "已提交");
        map.put("APPROVING", "审批中");
        map.put("APPROVED", "已审批");
        map.put("REJECTED", "已驳回");
        map.put("WITHDRAW", "已撤回");
        return map.get(str);
    }

    public static String getSourceFromZhName(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("YEAR", "年度");
        map.put("MONTH", "月度");
        map.put("WITHOUT_PLAN", "计划外");
        map.put("SPECIAL_SOU", "特殊招标");
        return map.get(str);
    }

    public static String getBidFlowZhName(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("STANDARD", "标准招标");
        map.put("SIMPLE", "简易招标");
        map.put("COMPETE", "竞争性谈判");
        map.put("INQUIRY", "询比价招标");
        map.put("JINGJIA", "竞价");
        return map.get(str);
    }

    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        BidDataSubmit bidDataSubmit = qlService.readByKey(MqlType.SUBMIT_BUYER, businessId, BidDataSubmit.class);
        DictItem requireFromDictItem = baseExtClient.getDictItem("PR_SOU_REQUIREMENT_FROM",bidDataSubmit.getSourceFrom());
        JSONObject processVars = new JSONObject();
        processVars.put("XQLY",requireFromDictItem==null?null:requireFromDictItem.getDictItemName());
        processVars.put("YS",bidDataSubmit.getTotalBudget());
        processVars.put("GS",bidDataSubmit.getOrgName());

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+bidDataSubmit.getProjectName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------getDataPushFlow-----------businessId:{}---param:{}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjSouClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        BidDataSubmit bidDataSubmit = qlService.readByKey(MqlType.SUBMIT_BUYER, businessId, BidDataSubmit.class);
        Map<String, Object> bidDataSubmitMap = new HashMap<>(50);
        //申请单号
        bidDataSubmitMap.put("SQDH", bidDataSubmit.getRequirementHeadNum());
        //板块
        bidDataSubmitMap.put("BK", bidDataSubmit.getOrgBuName());
        //公司
        bidDataSubmitMap.put("GS", bidDataSubmit.getOrgName());
        //需求部门
        bidDataSubmitMap.put("XQBM", bidDataSubmit.getCeeaDepartmentName());
        //单据状态
        bidDataSubmitMap.put("DJZT", getStatusZhName(bidDataSubmit.getStatus()));
        //创建人
        bidDataSubmitMap.put("CJR", bidDataSubmit.getCreatedFullName());
        //创建日期
        bidDataSubmitMap.put("CJRQ", BpmResult.sdfDate(bidDataSubmit.getCreationDate()));
        //最后更新时间
        bidDataSubmitMap.put("ZHGXSJ", BpmResult.sdfDate(bidDataSubmit.getLastUpdateDate()));
        //需求人
        bidDataSubmitMap.put("XQR", bidDataSubmit.getReqUserName());
        //招标负责人
        bidDataSubmitMap.put("ZBFZR", bidDataSubmit.getSouPersonName());
        //资料递交单号
        bidDataSubmitMap.put("ZLDJDH", bidDataSubmit.getDataSubmitNo());
        //项目名称
        bidDataSubmitMap.put("XMMC", bidDataSubmit.getProjectName());
        //需求来源
        bidDataSubmitMap.put("XQLY", getSourceFromZhName(bidDataSubmit.getSourceFrom()));
        //预算（万元）
        bidDataSubmitMap.put("YS", bidDataSubmit.getTotalBudget());
        //品类
        bidDataSubmitMap.put("PL", bidDataSubmit.getCategoryName());
        //规模数量
        bidDataSubmitMap.put("GMSL", bidDataSubmit.getRequireQuantity());
        //投标意向金（万元）
        bidDataSubmitMap.put("TBYXJ", bidDataSubmit.getDepositAmount());
        //投资编号
        bidDataSubmitMap.put("TZBH", bidDataSubmit.getInvestNo());
        //招标流程
        bidDataSubmitMap.put("ZBLC", getBidFlowZhName(bidDataSubmit.getBidFlow()));
        //评标组长
        bidDataSubmitMap.put("PBZZ", bidDataSubmit.getBidEvalLeaderName());
        //技术负责人
        bidDataSubmitMap.put("JSFZR", bidDataSubmit.getTechPrincipal());
        //联系方式
        bidDataSubmitMap.put("LXFS", bidDataSubmit.getPhone());
        //工作年限
        bidDataSubmitMap.put("GZNX", bidDataSubmit.getWorkYears());
        if (bidDataSubmit.getCompeteFileId() != null) {
            //竞争性谈判签批附件
            bidDataSubmitMap.put("JZXTPQPFJ", BpmResult.getFileList(addressPath, bidDataSubmit.getCompeteFileName(), bidDataSubmit.getCompeteFileId()));
        } else {
            //竞争性谈判签批附件
            bidDataSubmitMap.put("JZXTPQPFJ", new ArrayList<>());
        }
        //评标副组长
        bidDataSubmitMap.put("PBFZZ", bidDataSubmit.getBidEvalDeputyLeaderName());
        //合同签订单位
        bidDataSubmitMap.put("HTQDDW", bidDataSubmit.getContractSignUnit());
        //未提报月度计划原因
        bidDataSubmitMap.put("WTBYDJHYY", bidDataSubmit.getNotMonthlyPlanReason());
        //备注
        bidDataSubmitMap.put("BZ", bidDataSubmit.getRemark());
        //评标总人数
        bidDataSubmitMap.put("PBZRS", bidDataSubmit.getBidEvaluatorNum());
        //要求高级专家人数
        bidDataSubmitMap.put("YQGJZJRS", bidDataSubmit.getAskSeniorExpertNum());
        //指定评标人的原因
        bidDataSubmitMap.put("ZDPBRDYY", bidDataSubmit.getAppointEvaluatorReason());
        //招标资料递交子表1
        List<Object> itemDataList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        ids.add(businessId);
        List<SceneFile> fileName = baseClient.listSceneFileBatch(ids);
        Map<String, String> fileMap = baseClient.getDictItmeMapByDictCode("BID_DATA_SUBMIT_FILE_TYPE");
        for (SceneFile e : fileName) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_ZBZLDJZB1");
            map.put("WJLX", fileMap.get(e.getAttachmentType()));
            map.put("FJMC", BpmResult.getFileList(addressPath, e.getFileName(), e.getFileuploadId()));
            map.put("FJMBZC", e.getRemark());
            itemDataList.add(map);
        }
        JSONObject dataPushFlowJsn = getJsonObject(businessId, bidDataSubmit, bidDataSubmitMap, itemDataList);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    /**
     * 组装
     * @param businessId 参数
     * @param bidDataSubmit 参数
     * @param bidDataSubmitMap 参数
     * @param itemDataList 参数
     * @return 返回
     */
    @NotNull
    private JSONObject getJsonObject(Long businessId, BidDataSubmit bidDataSubmit, Map<String, Object> bidDataSubmitMap, List<Object> itemDataList) {
        //是否指定评标人
        try {
            QlOpenQueryWrapper wrapperList = QlOpenWrappers.query("SubmitEvaluatorBuyer")
                    .eq(BidDataSubmitEvaluator::getDataSubmitId, businessId);
            List<BidDataSubmitEvaluator> recordsList = qlOpenClient.query(ContextPath.SOU, wrapperList, BidDataSubmitEvaluator.class);
            recordsList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(16);
                map.put("__TABLE", "BO_EU_SFZDPBR");
                map.put("GH", e.getCeeaEmpNo());
                map.put("XM", e.getEvaluatorName());
                map.put("SJH", e.getPhone());
                map.put("DZYJ", e.getEmail());
                map.put("GZNX", e.getWorkYears());
                map.put("ZJDJ", getDictName("SOU_BID_EXPERT_LEVEL", e.getExpertLevel()));
                map.put("JS", getDictName("SOU_BID_ROLE", e.getEvaluatorRole()));
                itemDataList.add(map);
            });
        } catch (Exception e) {
            log.info("指定评标人错误");
        }
        Map<String, Object> headFileMap = new HashMap<>(16);
        headFileMap.put("__TABLE", "BO_EU_ZBZLDJFJZB");
        headFileMap.put("BTMC", "竞争性谈判签批附件");
        headFileMap.put("FJMC", BpmResult.getFileList(addressPath, bidDataSubmit.getCompeteFileName(), bidDataSubmit.getCompeteFileId()));
        itemDataList.add(headFileMap);
        String processTitle = "资料递交-"+ bidDataSubmit.getProjectName();
        String mainTable = "BO_EU_ZBZLDJ";
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = null;
        SccPjUser sccPjUser = pjSouClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        List<String> tableList = new ArrayList<>();
        tableList.add("BO_EU_ZBZLDJZB1");
        tableList.add("BO_EU_SFZDPBR");

        ArrayList<String> mainFile = Lists.newArrayList("JZXTPQPFJ");

        Map<String, Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_ZBZLDJ", BpmResult.getFileField("JZXTPQPFJ"));
        itemFile.put("BO_EU_ZBZLDJZB1", BpmResult.getFileField("FJMC"));
        itemFile.put("BO_EU_ZBZLDJFJZB", BpmResult.getFileField("FJMC"));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, bidDataSubmitMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile, mainFile);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        return dataPushFlowJsn;
    }

    public String getDictName(String dictCode, String va) {
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode(dictCode);
        for (DictItemDTO e : gyqyList) {
            if (e.getDictItemCode().equals(va)) {
                return e.getDictItemName();
            }
        }
        return null;
    }


    private void updateStatus(Long businessId, String param, String status) {
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUBMIT_BUYER)
                .set(BidDataSubmit::getStatus, status)
                .set(BidDataSubmit::getPublishTime, new Date())
                .set(BidDataSubmitStatusEnum.APPROVED.toString().equals(status), BidDataSubmit::getApprovePassTime, new Date())
                .eq(BidDataSubmit::getDataSubmitId, businessId));

        if (status.equals(BidDataSubmitStatusEnum.APPROVED.toString())) {
            BidDataSubmit bidDataSubmit = qlService.readByKey(MqlType.SUBMIT_BUYER, businessId, BidDataSubmit.class);
            List<Record> purchaseRequirementHeads = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                    .eq("requirementHeadNum", bidDataSubmit.getRequirementHeadNum()), Record.class);
            Assert.isTrue(ObjectUtil.isNotEmpty(purchaseRequirementHeads), "申请单数据为空");
            //回写申请单状态
            QlOpenUpdateWrapper up = QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                    .set("hasSendSouProfile", Enable.Y.name())
                    .set("sendSouProfileStatus", BidDataSubmitStatusEnum.APPROVED)
                    .eq("requirementHeadId", purchaseRequirementHeads.get(0).getLong("requirementHeadId"));
            qlOpenClient.update(ContextPath.SUP_CE, up);
        } else if (BidDataSubmitStatusEnum.APPROVING.name().equals(status)) {
            Record dataSubmit = qlService.readByKey(MqlType.SUBMIT_BUYER, businessId, Record.class);
            //查询申请单头表数据
            List<Record> purchaseRequirementHeads = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                    .eq("requirementHeadNum", dataSubmit.get(BidDataSubmit::getRequirementHeadNum)), Record.class);
            Assert.isTrue(ObjectUtil.isNotEmpty(purchaseRequirementHeads), "申请单数据为空");

            //回写申请单状态
            QlOpenUpdateWrapper up = QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                    .set("hasSendSouProfile", Enable.Y.name())
                    .set("sendSouProfileStatus", BidDataSubmitStatusEnum.APPROVING)
                    .eq(RequirementHead::getRequirementHeadId, purchaseRequirementHeads.get(0).get(RequirementHead::getRequirementHeadId));
            qlOpenClient.update(ContextPath.SUP_CE, up);
        }
    }
}
