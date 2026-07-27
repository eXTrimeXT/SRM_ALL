package com.midea.cloud.srm.sou.meiql.ca.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.constant.DingTalkConstant;
import com.midea.cloud.common.dingtalks.DingTalkClient;
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
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaOrderDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSupplierDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.enums.DictCodeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtComparePriceDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtComparePriceRespDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtCompareVendorPriceDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.enums.ExtRequireFromEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import com.midea.cloud.srm.sou.meiql.bidprices.service.SouNpmBidPriceSerice;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class CaFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Autowired
    public BaseClient baseClient;

    @Autowired
    private IExtSouProjectService projectService;


    @Autowired
    public FileCenterClient fileCenterClient;

    @Value("${bpm.DBSPD.processGroupId}")
    private String processGroupId;
    @Value("${bpm.DBSPD.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private PjSouClient pjSouClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouItemService IExtSouItemService;

    @Autowired
    private IExtSouDemandService demandService;

    @Autowired
    private SouNpmBidPriceSerice souNpmBidPriceSerice;

    @Resource
    private BaseExtClient baseExtClient;
    private static final String BUSINESS_TYPE = "SOU_CA";
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IExtSouItemService iExtSouItemService;

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    private static final String LOW_PRICE = "LOW_PRICE";

    private static final String SPECIAL_VENDOR_ONE= "SPECIAL_VENDOR_ONE";

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(CaDTO::getCaId,businessId);
                r.put(CaDTO::getStatus, CaStatusEnum.APPROVING.getCode());
                r.put(CaDTO::getStartBpmUsername, loginAppUser.getUsername());
                r.put(CaDTO::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            Record r = new Record();
            r.put(CaDTO::getCaId,businessId);
            r.put(CaDTO::getStatus, CaStatusEnum.APPROVING.getCode());
            qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
        }
    }

    private void doPassFlow(Long businessId, String param) throws Exception {
        CaDTO ca = qlService.readByKey(TypeEnum.Ca.getCode(),businessId,CaDTO.class);
        //1.修改单据状态为已审批
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode());
        qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
        //招标结果
        List<CaSelectionResultDTO> caSelectionResultDTOList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId, businessId), CaSelectionResultDTO.class);
        ca.setCaSelectionResults(caSelectionResultDTOList);
        // 修改bpmp回写信息
        if (YesOrNo.NO.getValue().equals(ca.getIfWrite())) {
            List<Record> updateList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(caSelectionResultDTOList)) {
                for (CaSelectionResultDTO dto : caSelectionResultDTOList) {
                    Record record = new Record();
                    record.put(CaSelectionResultDTO::getSelectionResultId, dto.getSelectionResultId());
                    String key = dto.getVendorCode();

                    String returnString = param;
                    JSONObject json = JSONObject.parseObject(returnString);
                    if (StringUtils.isNotBlank(param) && json != null && json.get("createdBy") != null
                            &&StringUtils.isNotBlank(json.get("createdBy").toString())&&!StringUtils.equals(json.get("createdBy").toString(),"null")) {
                        JSONObject createdBy = JSONObject.parseObject( json.get("createdBy").toString());
                        if(createdBy!=null&&createdBy.containsKey("BO_EU_GYSXDJG")){
                            JSONArray jsonArray  = createdBy.getJSONArray("BO_EU_GYSXDJG");
                            for(int i  = 0 ; i < jsonArray.size(); i++){
                                JSONObject js =   JSONObject.parseObject(jsonArray.get(i).toString());
                                // 供应商编码
                                String gysbm = js.get("GYSBM").toString();
                                // 是否中标
                                String sfzb = js.get("SFZB").toString();
                                // 中标范围
                                String zbfw = MapUtils.getString(js, "ZBFW", "").replaceAll(SrmConstant.SIG_3, SrmConstant.SIG_1);
                                // 中/落标原因
                                String zlbyy = js.get("ZLBYY").toString();
                                if("是".equals(sfzb) || "Y".equals(sfzb)){
                                    sfzb = "Y";
                                }else if("否".equals(sfzb) || "N".equals(sfzb)){
                                    sfzb = "N";
                                }
                                if(key.equals(gysbm)){
                                    //TODO 是否中标
                                    record.put(CaSelectionResultDTO::getIsWin, sfzb);
                                    //TODO 中标范围
                                    record.put(CaSelectionResultDTO::getWinRange, zbfw);
                                    //TODO 中标原因
                                    record.put(CaSelectionResultDTO::getWinReason, zlbyy);
                                    updateList.add(record);
                                }
                            }
                        }
                    }

                }
                qlService.update(TypeEnum.CaSelectionResult.getCode(), updateList);
            }
        }
        //2.修改招标单据状态
        if (null != ca.getProjectId() && SouTypeEnum.bid.name().equals(ca.getSouType())) {
            extSouInitEventService.updateSouBidStatus(ca.getProjectId(),SouBiddingProStatusEnum.WIN_LOSS_NOTICE);
        }

        //更新定标时间
        if(!Objects.isNull(ca.getProjectId())) {
            planService.applyAtualPoint(ca.getProjectId(), new Date(), ExtSouPlan::getPicketageTime, false);
        }
        //生成招标价格库数据
        souNpmBidPriceSerice.generateBidPriceById(businessId);
        yj(businessId);

        //特殊招标唯一中标单位不一致预警
        specialReqRemind(ca.getProjectId(), ca.getCaId());
    }

    /**
     * 招标项目[${souNo}][${souName}]特殊招标申请中唯一单位为${niqueVendor}，实际定标单位为${actualVendor}
     * 维护钉钉账号(钉钉提醒目标账号)字典：CA_SPECIAL_REMINDERS
     * @param projectId
     * @param caId
     */
    private void specialReqRemind(Long projectId, Long caId) {
        List<ExtSouDemand> demandList = demandService.lambdaQuery().eq(ExtSouDemand::getProjectId, projectId).list();
        if(CollectionUtils.isEmpty(demandList)) {
            return;
        }
        //查询采购需求提报
        List<RecordDTO> requirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadNum, demandList.stream().map(d -> d.getApplicantNo()).distinct().collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(requirementHeadList)) {
            return;
        }
        //查询采购需求提报-扩展表
        List<RecordDTO> extRequirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                .in(RequirementHead::getRequirementHeadId, requirementHeadList.stream().map(d -> d.get(RequirementHead::getRequirementHeadId)).distinct().collect(Collectors.toList()))
                .eq(ExtPrSouRequirementHead::getRequireFrom, ExtRequireFromEnum.SPECIAL_SOU.getCode())
                .eq(ExtPrSouRequirementHead::getSpecialSouType, SPECIAL_VENDOR_ONE));
        if(CollectionUtils.isEmpty(extRequirementHeadList)) {
            return;
        }

        //查询唯一单位
        List<RecordDTO> extRequirementVendorList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_VENDOR)
                .in(RequirementHead::getRequirementHeadId, extRequirementHeadList.stream().map(d -> d.get(RequirementHead::getRequirementHeadId)).distinct().collect(Collectors.toList())));
        if(CollectionUtils.isEmpty(extRequirementVendorList)) {
            return;
        }

        //查询中标供应商
        List<CaSelectionResultDTO> winVendorList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId, caId).eq(CaSelectionResultDTO::getIsWin, YesOrNo.YES.getValue()), CaSelectionResultDTO.class);

        if(CollectionUtils.isEmpty(winVendorList)) {
            return;
        }

        List<String> uniqueVendorList = extRequirementVendorList.stream().map(v -> v.get(ExtPrSouRequirementVendor::getVendorName)).distinct().collect(Collectors.toList());

        List<String> outRangeVendorNameList = new ArrayList<>(15);
        //校验中标供应商是否在采购需求提报的推荐供应商清单里面
        winVendorList.stream().forEach(winVendor -> {
            if(!uniqueVendorList.contains(winVendor.getVendorName())) {
                outRangeVendorNameList.add(winVendor.getVendorName());
            }
        });

        if(CollectionUtils.isEmpty(outRangeVendorNameList)) {
            return;
        }

        //查询字典
        List<DictItem> remindersList = baseClient.listDictItemByDictCode(DictCodeEnum.CA_SPECIAL_REMINDERS.getCode());
        if(CollectionUtils.isEmpty(remindersList)) {
            log.info(MessageFormat.format("定标申请中标单位与特殊招标唯一单位不一致，未配置需提醒人员，未发送钉钉预警。如需配置，请在字典{0}里维护配置", DictCodeEnum.CA_SPECIAL_REMINDERS.getCode()));
            return;
        }

        ExtSouProject project = projectService.getById(projectId);
        //招标项目[${souNo}][${souName}]特殊招标申请中唯一单位为${niqueVendor}，实际定标单位为${actualVendor}
        Map<String, String> vars = new HashMap<>(15);
        vars.put("${souNo}", project.getSouNo());
        vars.put("${souName}", project.getSouName());
        vars.put("${niqueVendor}", uniqueVendorList.stream().collect(Collectors.joining(SrmConstant.SIG_3)));
        vars.put("${actualVendor}", outRangeVendorNameList.stream().collect(Collectors.joining(SrmConstant.SIG_3)));

        DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(remindersList.stream().map(d->d.getDictItemCode()).distinct().collect(Collectors.toList()), DingTalkConstant.CA_SPECIAL_REMINDERS, vars);
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        try {
            doPassFlow(businessId, param);
        } catch (Exception e) {
            log.error("定标申请审批通过执行异常passFlow", e);
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.REJECTED.getCode());
        qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.WITHDRAW.getCode());
        qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
        //1.修改招标状态
        CaDTO ca = qlService.readByKey(TypeEnum.Ca.getCode(),businessId,CaDTO.class);
        //2.修改寻源单据状态
        if (null != ca.getProjectId() && SouTypeEnum.bid.name().equals(ca.getSouType())) {
            extSouInitEventService.updateSouBidStatus(ca.getProjectId(),SouBiddingProStatusEnum.BUS_BID_OPEN);
        }

    }



    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        CaDTO ca = qlService.readByKey(TypeEnum.Ca.getCode(),businessId,CaDTO.class);

        String round = "";
        if(StringUtils.isNotBlank(ca.getCaRound())){
            DictItem dictItem = baseExtClient.getDictItem("NPM_CA_PRICE_ROUND",ca.getCaRound());
            round +="-"+dictItem.getDictItemName();
        }

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+ca.getSouName()+round);
        bpmParam.setProcessGroupId(processGroupId2);
        JSONObject vars = new JSONObject();
        String ifBid = YesOrNo.NO.getName();
        Boolean bidFlag = ExtSouBidComponent.getInstance().isBid(ca.getExtCategoryId());
        if(bidFlag) {
            ifBid = YesOrNo.YES.getName();
        }
        vars.put("SFSYZBFW", ifBid);
        vars.put("YS", ObjectUtils.allNotNull(ca.getExtBudget()) ? ca.getExtBudget().stripTrailingZeros().toPlainString() : "");

        bpmParam.setProcessVars(vars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        /**
         * 查询 定标申请 -   businessid  == CaId
         * 定标申请DTO  CaDTO
         * 供应商投标DTO  CaOrderDTO
         * 供应商选定结果DTO  CaSelectionResultDTO
         * 供应商总体情况    CaSupplierDTO
         */
        log.info("===================进入定标申请装数据方法开始11122233"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        // 定标申请DTO
        CaDTO ca = qlService.readByKey(TypeEnum.Ca.getCode(),businessId,CaDTO.class);
        log.info("定标申请数据==={}", JSONObject.toJSONString(ca));
        // 供应商总体情况
        List<CaSupplierDTO> caSupplierDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSupplier.getCode())
                .eq(CaSupplierDTO::getCaId, businessId), CaSupplierDTO.class);
        // 供应商选定结果
        List<CaSelectionResultDTO> caSelectionResultDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId, businessId), CaSelectionResultDTO.class);
        // 组装主表信息
        Map<String, Object> mainTableData = new HashMap<>(50);
        // 定标申请单
        mainTableData.put("DBSQD",ca.getCaNo());
        // 板块
        mainTableData.put("BK",ca.getExtOrgBuName());
        //公司
        mainTableData.put("GS",ca.getExtOrgOuName());
        //需求部门
        mainTableData.put("XQBM",ca.getDemandDepartmentName());
        //单据状态
        mainTableData.put("DJZD",getStatus(ca.getStatus()));
        //创建人
        mainTableData.put("CJR",ca.getCreatedFullName());
        //创建日期
        mainTableData.put("CJRQ",BpmResult.sdfDate(ca.getCreationDate()));
        //最后更新时间
        mainTableData.put("ZHGXSJ",BpmResult.sdfDate(ca.getLastUpdateDate()));
        //需求人
        mainTableData.put("XQR",ca.getDemandUserNickname());
        //招标项目单号
        mainTableData.put("ZBXMDH",ca.getExtProjectNo());
        //项目名称--寻源名称
        mainTableData.put("XMMC",ca.getSouName());
        //质保期
        mainTableData.put("ZBQ",ca.getWarrantyPeriod());
        //预算（万元）
        mainTableData.put("YS",ca.getExtBudget());
        //工期/交货期要求
        mainTableData.put("GQJHQYQ",ca.getTimeLimit());
        //发标时间
        mainTableData.put("FBSJ", BpmResult.sdfDate(ca.getPublishTime()));
        //收标时间
        mainTableData.put("SBSJ",BpmResult.sdfDate(ca.getBusEndTime()));
        //评标结束
        mainTableData.put("PBJS",BpmResult.sdfDate(ca.getTechEvaluationTime()));
        //开价格标时间
        mainTableData.put("KJGBSJ",BpmResult.sdfDate(ca.getPriceOpeningTime()));
        //项目概况与招标范围
        mainTableData.put("XMHK",ca.getProjectOverviewAndBidScope());
        //付款要求
        mainTableData.put("FKYQ",ca.getPaymentRequirements());
        //备注
        mainTableData.put("BZ",ca.getRemark());
//供应商参与情况
        mainTableData.put("GYSCYQK","");
        //供应商报价信息
        mainTableData.put("GYSBJXX","");
//技术评分结果
        mainTableData.put("JSPFJG","查看");
        //技术评分结果链接
        mainTableData.put("JSPFJGLJ",getViewSrmRollBackUrl("bidTechScoreDetail",ca.getProjectId() ,"定标审批单"));
//查看供应商风险
        mainTableData.put("CKGYSFX","查看");
        //查看供应商风险链接
        mainTableData.put("CKGYSFXLJ",getViewSrmRollBackUrl("vendorRisk",businessId ,"定标审批单-查看供应商风险"));
//打开标签页查看
        mainTableData.put("DKBQYCK","查看");
        //打开标签页查看链接
        mainTableData.put("DKBQYCKLJ",getViewSrmRollBackUrl("bidPriceComparison",ca.getProjectId() ,"定标审批单"));
        //是否询比价
        ExtSouProject extSouProject = projectService.getById(ca.getProjectId());
        mainTableData.put("SFXBJ", "INQUIRY".equals(extSouProject.getExtSouProcess()) ? "是" : "否");
        //* @param funName 这个对应功能的名字，前端一般用路由的name值来识别
        //* @param formId 这个是业务单据ID
        //* @param formNo 这个传的是单据标题或其他自定义标题

        // 供应商总体情况
        List<Object> itemdata = new ArrayList<>();
        if(null != caSupplierDtoList){
            caSupplierDtoList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                // 供应商名称
                map.put("GYSMC",e.getVendorName());
                // 投标含税总价（万元）
                map.put("TBHSZJ",e.getBidTotalPrice());
                // 技术得分
                map.put("JSDF",e.getTechScore());
                // 价格得分
                map.put("JGDF",e.getPriceScore());
                // 综合得分
                map.put("ZHDF",e.getCompositeScore());
                // 综合评定
                map.put("ZHPD",e.getComprehensiveEvaluation());
                map.put("__TABLE", "BO_EU_GYSZTQK");
                itemdata.add(map);
            });
        }

        String ifWrite = ca.getIfWrite();
        // 供应商选定结果
        extracted(ca, caSelectionResultDtoList, itemdata, ifWrite);

        List<Long> ids = new ArrayList<>();
        ids.add(businessId);
        List<SceneFile> fileList = baseClient.listSceneFileBatch(ids);
        extracted(businessId, itemdata, fileList);

        Map<String,Object> itemFile = new HashMap<>(50);
        List<String> fList = new ArrayList<>();
        fList.add("FJMC");
        itemFile.put("BO_EU_FJXX", fList);

        String processtitle = "定标审批-"+ca.getSouName();
        String maintable = "BO_EU_DBSP";

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

        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_GYSZTQK");
        itemtable.add("BO_EU_GYSXDJG");
        itemtable.add("BO_EU_FJXX");
        itemtable.add("BO_EU_TBGYS");

        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processGroupId, appId,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入定标审批单组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========定标审批单通知JSON=============="+dataPushFlowJsn.toString());
        Map<String, String> pageUrlMap = new HashMap<>(4);
        pageUrlMap.put("formUrl", getViewSrmRollBackUrl("bidPriceComparison",ca.getProjectId() ,"定标审批单"));
        dataPushFlowJsn.put("PROCESSVARS", pageUrlMap);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }

    /**
     * 供应商选定
     * @param ca 参数
     * @param caSelectionResultDtoList 参数
     * @param itemdata 参数
     * @param ifWrite 参数
     */
    private void extracted(CaDTO ca, List<CaSelectionResultDTO> caSelectionResultDtoList, List<Object> itemdata, String ifWrite) {
        if(null != caSelectionResultDtoList){
            for(int i = 0; i < caSelectionResultDtoList.size(); i ++){
                Map<String, Object> map = new HashMap<>(50);
                // 供应商编码
                map.put("GYSBM", caSelectionResultDtoList.get(i).getVendorCode());
                // 供应商名称
                map.put("GYSMCD", caSelectionResultDtoList.get(i).getVendorName());
                String isWin =  caSelectionResultDtoList.get(i).getIsWin();
                if(StringUtils.isNotEmpty(isWin)){
                    // 是否中标
                    map.put("SFZB", BpmResult.dealYesOrNo(caSelectionResultDtoList.get(i).getIsWin()));
                }else{
                    // 是否中标
                    map.put("SFZB",null);
                }

                Map<String, String> fileMap = baseClient.getDictItmeMapByDictCode("CA_WIN_BID_REASON");
                // 中/落标原因
                map.put("ZLBYY", fileMap.get(caSelectionResultDtoList.get(i).getWinReason()));
                //【获取中标范围的数据，调同步视图接口，bpm中该字段送空】
                if("Y".equals(ifWrite)){
                    // 页面手动填的值
                    // 中标范围
                    String zbfw = caSelectionResultDtoList.get(i).getWinRange() ;
                    // 中标范围
                    map.put("ZBFW", zbfw);
                    String zbfwcode = caSelectionResultDtoList.get(i).getOrderItemId();
                    if(StringUtils.isNotEmpty(zbfw) && StringUtils.isNotEmpty(zbfwcode)){
                        pjSouClient.pushZbfwToBpm(zbfw,zbfwcode, ca.getCaNo());
                    } else if ("Y".equals(isWin)) {
                        throw new RuntimeException("页面手动填写的中标范围为空");
                    }
                    map.put("ZBFWCODE", "");
                }else{  // 不是手动填写的中标范围，需要去报价信息那边查询
                    /**
                     * 报价信息主键：com.midea.cloud.srm.model.sou.sourcing.entity.SouItem#souItemId  scc_sou_item
                     * 先查到 报价信息表 SouItem  project_id 查询SouItem
                     * 然后判断是否是合并 合并去重包名
                     */
                    String zbfw = "" ;
                    String zbfwcode = "";
                    Long projectId = ca.getProjectId();
                    LambdaQueryWrapper<ExtSouItem> souItemQuery = new LambdaQueryWrapper<>();
                    souItemQuery.eq(ExtSouItem::getProjectId, projectId);
                    List<ExtSouItem> souItemList = iExtSouItemService.list(souItemQuery);
                    List<String> pnName = new ArrayList<>();
                    if(isMergeApplitionNum(projectId)){
                        // 是合并的
                        for(int m = 0; m < souItemList.size(); m++){
                            String souItemId = souItemList.get(m).getSouItemId().toString();
                            String extPackageName = souItemList.get(m).getExtPackageName();
                            if(m > 0 ){
                                if(!pnName.contains(extPackageName)){
                                    pnName.add(extPackageName);
                                    zbfw += souItemId + ";";
                                    zbfwcode += extPackageName + ";";
                                }
                            }else{
                                pnName.add(extPackageName);
                                zbfw += souItemId + ";";
                                zbfwcode += extPackageName + ";";
                            }
                        }
                        zbfw = zbfw.substring(0, zbfw.length() - 1);
                        zbfwcode = zbfwcode.substring(0, zbfwcode.length() - 1);
                        // 中标范围
                        pjSouClient.pushZbfwToBpm(zbfwcode,zbfw, ca.getCaNo());
                    }else{ // 非合并的
                        for(int m = 0; m < souItemList.size(); m++){
                            String souItemId = souItemList.get(m).getSouItemId().toString();
                            String itemDesc = souItemList.get(m).getItemDesc();
                            pnName.add(itemDesc);
                            zbfw += souItemId + ";";
                            zbfwcode += itemDesc + ";";
                        }
                        zbfw = zbfw.substring(0, zbfw.length() - 1);
                        zbfwcode = zbfwcode.substring(0, zbfwcode.length() - 1);
                        // 中标范围
                        pjSouClient.pushZbfwToBpm(zbfwcode,zbfw, ca.getCaNo());
                    }
                    map.put("ZBFWCODE", "");
                    map.put("ZBFW", "");
                    map.put("SFZB", "");
                    map.put("ZLBYY", "");
                }
                map.put("__TABLE", "BO_EU_GYSXDJG");
                itemdata.add(map);
            }
        }
    }

    /**
     * 附件
     * @param businessId 参数
     * @param itemdata 参数
     * @param fileList 参数
     */
    private void extracted(Long businessId, List<Object> itemdata, List<SceneFile> fileList) {
        // 附件
        for (SceneFile e : fileList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_FJXX");
            map.put("FJMC", BpmResult.getFileList(addressPath, e.getFileName(), e.getFileuploadId()));
            map.put("BZH", e.getRemark());
            itemdata.add(map);
        }
        //投标供应商
        List<CaOrderDTO> caOrdersList = qlService.queryByWrapper(QlWrappers.query("CaOrder").eq(CaOrderDTO::getCaId, businessId), CaOrderDTO.class);
        for (CaOrderDTO co : caOrdersList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_TBGYS");
            //供应商名称
            map.put("GYSMC", co.getVendorName());
            //供应商属性
            if (org.apache.commons.lang3.StringUtils.isNotBlank(co.getExtVendorAttr())) {
                String[] att = co.getExtVendorAttr().split(";");
                List<String> attr = new ArrayList<>();
                for (String s : att) {
                    attr.add(getDictName("SOU_RECOMM_VENDOR_NATRUE", s));
                }
                map.put("GYSSX", String.join(";", attr));
            } else {
                map.put("GYSSX", "");
            }
            //投标包名
            map.put("TBBM", co.getTenderPackageName());
            //投标状态
            map.put("TBZT", getDictName("SOU_ORDER_STATUS", co.getOrderStatus()));
            //不参与时间
            map.put("BCYSJ", co.getExtNotjoinReason());
            //废标说明
            map.put("FBSM", co.getRejectReason());
            itemdata.add(map);
        }
    }


    public String getViewSrmRollBackUrl( String funName,Long formId,String formNo){
        String url =  pjSouClient.getViewSrmRollBackUrl(funName,formId,formNo);
        return url;
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

    public String getStatus(String type){
        String draft = "DRAFT";
        String approving = "APPROVING";
        String approved = "APPROVED";
        String rejected = "REJECTED";
        String abandon = "ABANDON";
        String withdraw = "WITHDRAW";
        if(draft.equals(type)){
            return "拟定" ;
        }else if(approving.equals(type)){
            return "审批中" ;
        }else if(approved.equals(type)){
            return "已审批" ;
        }else if(rejected.equals(type)){
            return "已驳回" ;
        }else if(abandon.equals(type)){
            return "已废弃" ;
        }else if(withdraw.equals(type)){
            return "已撤回" ;
        }else {
            return type ;
        }
    }

    protected boolean isMergeApplitionNum(Long projectId) {
        LambdaQueryWrapper<ExtSouDemand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouDemand::getProjectId, projectId);
        int count = (int) demandService.count(queryWrapper);
        if (Integer.compare(count, 1) == 1) {
            return true;
        }
        return false;
    }


    /**
     * 预警信息
     */
    public void yj(Long businessId) {
        CaDTO ca = qlService.readByKey(TypeEnum.Ca.getCode(),businessId,CaDTO.class);
        //获取招标书审批人
        ApiExtSouProjectInfoDTO souProject = extSouInitQueryService.getProjectInfo(ca.getProjectId());
        log.info(JSONObject.toJSONString(souProject));
        String userNo = souProject.getProject().getApproveUserName();
        boolean hl = false;
        boolean qt = false;
        //供应商选定结果
        List<CaSelectionResultDTO> caSelectionResultDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId, businessId).eq(CaSelectionResultDTO::getIsWin, "N"), CaSelectionResultDTO.class);
        //合理低价法
        if (LOW_PRICE.equals(ca.getExtScoreRule())) {
            for (CaSelectionResultDTO caSelectionResultDTO : caSelectionResultDtoList) {
                //供应商报价对比详情
                ApiExtComparePriceRespDto comparePriceResp = extSouInitQueryService.getComparePrice(ca.getProjectId());
                List<ApiExtComparePriceDto> cpList = comparePriceResp.getComparePriceList();

                Predicate<ApiExtCompareVendorPriceDto> predicate = new Predicate<ApiExtCompareVendorPriceDto>() {
                    @Override
                    public boolean test(ApiExtCompareVendorPriceDto o) {
                        return !Objects.isNull(o.getExtPriceTax()) && BigDecimal.ZERO.compareTo(o.getExtPriceTax()) != 0;
                    }
                };
                for (ApiExtComparePriceDto apiExtComparePriceDto : cpList) {
                    List<ApiExtCompareVendorPriceDto> priceList = apiExtComparePriceDto.getPriceList();
                    Optional<ApiExtCompareVendorPriceDto> opt =  priceList.stream().filter(o -> predicate.test(o)).min(Comparator.comparing(ApiExtCompareVendorPriceDto::getExtPriceTax));
                    for (ApiExtCompareVendorPriceDto apiExtCompareVendorPriceDto : priceList) {
                        if(!predicate.test(apiExtCompareVendorPriceDto)) {
                            continue;
                        }
                        if (apiExtCompareVendorPriceDto.getVendorId().equals(caSelectionResultDTO.getVendorId()) &&
                                apiExtCompareVendorPriceDto.getExtPriceTax().equals(opt.get().getExtPriceTax())) {
                            if (!hl) {
                                hl = true;
                            }
                        }
                    }
                }
            }
        } else {
            //供应商总体情况
            List<CaSupplierDTO> caSupplierDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSupplier.getCode())
                    .eq(CaSupplierDTO::getCaId, businessId), CaSupplierDTO.class);
            if (CollectionUtils.isNotEmpty(caSupplierDtoList) && CollectionUtils.isNotEmpty(caSelectionResultDtoList)) {
                //最低价
                CaSupplierDTO priceScoreMin =  caSupplierDtoList.stream().min(Comparator.comparing(CaSupplierDTO::getPriceScore)).orElse(new CaSupplierDTO());
                //综合得分最高
                CaSupplierDTO compositeScoreMax =  caSupplierDtoList.stream().max(Comparator.comparing(CaSupplierDTO::getCompositeScore)).orElse(new CaSupplierDTO());;
                if (priceScoreMin.getVendorId().equals(compositeScoreMax.getVendorId())) {
                    for (CaSelectionResultDTO caSelectionResultDTO : caSelectionResultDtoList) {
                        if (caSelectionResultDTO.getVendorId().equals(priceScoreMin.getVendorId())) {
                            if (!qt) {
                                qt = true;
                            }
                        }
                    }
                }
            }
        }

        //只有一个供应商时，不发送提醒
        long vendorCount = qlService.countByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode()).eq(CaSelectionResultDTO::getCaId, businessId));
        if (Long.compare(vendorCount, SrmConstant.LONG_ONE) == 1 && (hl || qt)) {
            String msg = String.format("【%S】【%S】定标未定最低价或未定综合得分第一名，请关注！", ca.getExtProjectNo(), ca.getSouName());
            log.info(msg);
            List<String> userList = new ArrayList<>();
            userList.add(userNo);
            pjSouClient.workNotices(msg, userList);
        }
    }
}

