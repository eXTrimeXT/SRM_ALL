package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelUnPassDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolEventService;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.ReuirementCancleCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 招标计划 - 取消单据审批流回调定义
 * PS: 审批流编码  MQL_PR_SOU_REQ_CANCEL_INIT
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementCancelFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.XQSQQX.processGroupId}")
    private String processGroupId;
    @Value("${bpm.XQSQQX.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    private PrSouRequirementPoolEventService prSouRequirementPoolEventService;

    @Autowired
    private QlService qlService;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Resource
    private BaseExtClient baseExtClient;

    private static final String BUSINESS_TYPE = "MQL_PR_SOU_REQ_CANCEL_INIT";
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        List<ExtPrSouRequirementCancelLine> extPrSouRequirementCancelLineList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class).
                eq(ExtPrSouRequirementCancelLine::getRequirementCancelId, businessId), ExtPrSouRequirementCancelLine.class);
        String techUsername = null;
        String vendorUsername = null;
        List<String> projectNames = new ArrayList<>();
        for(ExtPrSouRequirementCancelLine line : extPrSouRequirementCancelLineList) {
            Long requirementHeadId = line.getRequirementHeadId();
            ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey("ExtPrSouRequirementHead", requirementHeadId, ExtPrSouRequirementHead.class);
            projectNames.add(extPrSouRequirementHead.getProjectName());
            List<ExtPrSouRequirementGroup> extPrSouRequirementGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class).
                    eq(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadId), ExtPrSouRequirementGroup.class);
            for (ExtPrSouRequirementGroup item : extPrSouRequirementGroupList) {
                // 工作成员职责类型
                String groupType = item.getGroupType();
                if ("SOU".equals(groupType)) {
                    // bpm招标负责人 -- srm招标负责人账号
                    techUsername = (item.getUsername());
                } else if ("VENDOR".equals(groupType)) {
                    // 供应商负责人
                    vendorUsername = (item.getUsername());
                }
            }
        }

        JSONObject processVars = new JSONObject();
        processVars.put("ZBFZR",techUsername);
        processVars.put("GYSFZR",vendorUsername);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName() + "-" + projectNames.get(0));
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) {
        log.info("getVariableFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }

        /**
         * 查询 招标计划 -   businessid  就是 requirementCancelId
         * 计划取消表        scc_npm_pr_require_cancel  根据 requirementCancelId 查询 ,
         * 计划取消附件      scc_npm_pr_require_cancel_file   根据 requirementCancelId 查询
         * 计划取消明细      scc_npm_pr_require_cancel_line  根据 requirementCancelId 查询
         * 然后根据 计划取消明细中的招标计划id查询相关信息
         */
        log.info("===================进入招标计划取消装数据方法开始"+businessId);
        ExtPrSouRequirementCancel extPrSouRequirementCancel = qlService.readByKey("ExtPrSouRequirementCancel",businessId, ExtPrSouRequirementCancel.class);
        List<ExtPrSouRequirementCancelLine> extPrSouRequirementCancelLineList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class).
                eq(ExtPrSouRequirementCancelLine::getRequirementCancelId, businessId), ExtPrSouRequirementCancelLine.class);
        List<ExtPrSouRequirementCancelAttach> extPrSouRequirementCancelAttachlist = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelAttach.class).
                eq(ExtPrSouRequirementCancelAttach::getRequirementCancelId, businessId), ExtPrSouRequirementCancelAttach.class);
        // 组装主表信息
        Map<String, Object> mainTableData = new HashMap<>(50);
        mainTableData.put("SQBM",extPrSouRequirementCancel.getDepartmentId());
        //申请时间
        String sqsj = null ;
        if(extPrSouRequirementCancel.getApplyDate() != null){
            sqsj = extPrSouRequirementCancel.getApplyDate().toString();
        }
        mainTableData.put("SQSJ",(sqsj));
        mainTableData.put("SQDH",extPrSouRequirementCancel.getRequirementCancelNo());
        mainTableData.put("SQR",extPrSouRequirementCancel.getCreatedFullName());
        mainTableData.put("XQQXSM",extPrSouRequirementCancel.getCancelReason());

        ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey("ExtPrSouRequirementHead",extPrSouRequirementCancelLineList.get(0).getRequirementHeadId(), ExtPrSouRequirementHead.class);
        // 计划取消明细
        List<Object> itemdata = new ArrayList<>();
        for(ExtPrSouRequirementCancelLine line : extPrSouRequirementCancelLineList){
            Long  requirementHeadId = line.getRequirementHeadId();
            List<ExtPrSouRequirementGroup> extPrSouRequirementGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class).
                    eq(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadId), ExtPrSouRequirementGroup.class);
            String techUsername = null ;
            String vendorUsername = null ;
            for(ExtPrSouRequirementGroup item:extPrSouRequirementGroupList){
                // 工作成员职责类型
                String  groupType   =  item.getGroupType();
                if("TECH".equals(groupType)){
                    // bpm技术负责人 -- srm技术负责人账号
                    techUsername = (item.getUsername());
                }else if("VENDOR".equals(groupType)){
                    // 供应商负责人
                    vendorUsername = (item.getUsername());
                }
            }
            Map<String, Object> map = new HashMap<>(50);
            //项目名称
            map.put("XMMC",extPrSouRequirementHead.getProjectName());
            // 概算金额（万元）
            map.put("GSJE",extPrSouRequirementHead.getTotalAmountByTenKilo());
            // 项目所在地
            map.put("XMSZD",extPrSouRequirementHead.getProjectAddress());
            // 项目概况及范围
            map.put("XMGKJFW",extPrSouRequirementHead.getProjectOverview());
            // 招标负责人
            map.put("ZBFZR",techUsername);
            // 供应商负责人
            map.put("GYSFZR",vendorUsername);
            //取消原因
            map.put("QXYY",extPrSouRequirementCancel.getCancelReason());
            map.put("__TABLE", "BO_EU_XQSQQXXQZB1");
            itemdata.add(map);
        }
        // 计划取消附件
        if (CollectionUtils.isNotEmpty(extPrSouRequirementCancelAttachlist)) {
            extPrSouRequirementCancelAttachlist.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                List<Map<String, Object>> file = new ArrayList<>();
                Map<String, Object> fileMap = new HashMap<>(50);
                Fileupload fileupload = new Fileupload();
                // 【这里需要确认下是否是fileUploadid】
                fileupload.setFileuploadId(e.getFileId());
                fileupload.setPageNum(1);
                fileupload.setPageSize(1);
                PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
                fileuploads.getList().forEach(m -> {
                    fileMap.put("FILE_PATH_BYMOBILE", "");
                    fileMap.put("FILE_NAME", m.getFileSourceName());
                    String mes = "fileSourceName="+m.getFileSourceName()+"&fileuploadId="+m.getFileuploadId();
                    fileMap.put("FILE_PATH", fileDownloadPath+mes);
                    file.add(fileMap);
                });
                map.put("FJ",file);
                // 文件上传人
                map.put("SCR",extPrSouRequirementCancel.getCreatedFullName());
                // 账号
                map.put("ZH",extPrSouRequirementCancel.getApplyBy());
                // 上传时间
                map.put("SCSJ",e.getUploadTime());
                map.put("__TABLE", "BO_EU_XQSQQXXQZB2");
                itemdata.add(map);
            });
        }


        Map<String,Object> itemFile = new HashMap<>(50);
        List<String> fList = new ArrayList<>();
        fList.add("FJ");
        itemFile.put("BO_EU_XQSQQXXQZB2", fList);

        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_XQSQQXXQZB1");
        itemtable.add("BO_EU_XQSQQXXQZB2");

        String processtitle = "招标需求申请取消";
        String maintable = "BO_EU_XQSQQX";

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();


        String createOrgId = null;
        SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new RuntimeException("查询不到hr组织id");
        }

        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processGroupId, appId,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入计划取消组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========招标需求申请取消JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }

    @Override
    public void submitFlow(Long businessId, String param) {
        log.info("submitFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                prSouRequirementPoolEventService.callbackAfterCancelApprovalSubmit(businessId);


                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(ExtPrSouRequirementCancel::getRequirementCancelId, businessId);
                r.put(ExtPrSouRequirementCancel::getStartBpmUsername, loginAppUser.getUsername());
                r.put(ExtPrSouRequirementCancel::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update("ExtPrSouRequirementCancel", Arrays.asList(r));
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            prSouRequirementPoolEventService.callbackAfterCancelApprovalSubmit(businessId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passFlow(Long businessId, String param) {
        log.info("passFlow: {}, {}", businessId, param);
        prSouRequirementPoolEventService.callbackAfterCancelApprovalPass(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) {
        log.info("rejectFlow: {}, {}", businessId, param);
        prSouRequirementPoolEventService.callbackAfterCancelApprovalUnPass(
                new ExtPrSouRequirementCancelUnPassDTO(businessId, PrSouRequirementCancelStatusEnum.REJECTED));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) {
        log.info("withdrawFlow: {}, {}", businessId, param);
        prSouRequirementPoolEventService.callbackAfterCancelApprovalUnPass(
                new ExtPrSouRequirementCancelUnPassDTO(businessId, PrSouRequirementCancelStatusEnum.WITHDRAW));
    }

    @Override
    public void destoryFlow(Long businessId, String param) {
        log.info("destoryFlow: {}, {}", businessId, param);
        prSouRequirementPoolEventService.callbackAfterCancelApprovalUnPass(
                new ExtPrSouRequirementCancelUnPassDTO(businessId, PrSouRequirementCancelStatusEnum.ABANDONED));
    }


    @Override
    public String getVariableFlow(Long businessId, String param) {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        return null;
    }

}
