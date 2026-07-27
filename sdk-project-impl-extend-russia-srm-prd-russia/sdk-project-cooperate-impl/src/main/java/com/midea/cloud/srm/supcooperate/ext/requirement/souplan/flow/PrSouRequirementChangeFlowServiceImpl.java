package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementApprovalUnPassDTO;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 招标计划 - 变更审批流回调定义
 * PS: 审批流编码  MQL_PR_SOU_REQUIREMENT_CHANGE
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementChangeFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private MqlPrRequirementInitEventService prRequirementInitEventService;
    @Value("${bpm.XQBG.processGroupId}")
    private String processGroupId;
    @Value("${bpm.XQBG.processGroupId2}")
    private String processGroupId2;
    @Value("${bpm.zzsc.appId}")
    private String appId;
    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;
    @Autowired
    private QlService qlService;
    @Autowired
    public FileCenterClient fileCenterClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private BaseClient baseClient;

    private static final String BUSINESS_TYPE = "MQL_PR_SOU_REQUIREMENT_CHANGE";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    BaseExtClient baseExtClient;
    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey("ExtPrSouRequirementHead",businessId, ExtPrSouRequirementHead.class);
        List<PurchaseRequirementHeadDTO> requirementHeads = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementHead")
                .eq(PurchaseRequirementHeadDTO::getRequirementHeadId, businessId) ,PurchaseRequirementHeadDTO.class);
        PurchaseRequirementHeadDTO purchaseRequirementHeadDto = requirementHeads.get(0);
        String ifBid = "N";
        PurchaseCategory category = new PurchaseCategory();
        String categoryCode = purchaseRequirementHeadDto.getCategoryCode();
        Long categoryId = purchaseRequirementHeadDto.getCategoryId();
        category.setCategoryCode(categoryCode);
        category.setCategoryId(categoryId);
        category = baseClient.getPurchaseCategoryByParm(category);
        if (null != category) {
            String ifBidStr = "ifBid";
            if (StringUtils.isNotEmpty((String) category.getExtensions().get(ifBidStr))) {
                if (YesOrNo.YES.getValue().equals(category.getExtensions().get(ifBidStr))){
                    ifBid = "Y";
                }
            }
            categoryCode = category.getCategoryName();
        }
        JSONObject processVars = new JSONObject();
        processVars.put("XQLY",getrequireFrom(extPrSouRequirementHead.getRequireFrom()));
        processVars.put("SQGS",extPrSouRequirementHead.getOrgBuName());
        processVars.put("GSJE",(extPrSouRequirementHead.getTotalAmountByTenKilo() == null)?  null :  extPrSouRequirementHead.getTotalAmountByTenKilo().toString());
        processVars.put("SFSYZBFW",((ifBid == "Y" ) ? "是" : "否"));
        processVars.put("XMMC",extPrSouRequirementHead.getProjectName());

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        /**
         * 查询 招标需求变更 -   businessid  就是 requirementCancelId
         */
        log.info("===================进入招标计划变更装数据方法开始"+businessId);
        List<PurchaseRequirementHeadDTO> requirementHeads = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementHead")
                .eq(PurchaseRequirementHeadDTO::getRequirementHeadId, businessId) ,PurchaseRequirementHeadDTO.class);
        PurchaseRequirementHeadDTO purchaseRequirementHeadDto = requirementHeads.get(0);

        ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey("ExtPrSouRequirementHead",businessId, ExtPrSouRequirementHead.class);

        List<ExtPrSouRequirementGroup> extPrSouRequirementGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class).
                eq(ExtPrSouRequirementGroup::getRequirementHeadId, businessId), ExtPrSouRequirementGroup.class);

        List<ExtPrSouRequirementVendor> extPrSouRequirementVendorList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class).
                eq(ExtPrSouRequirementVendor::getRequirementHeadId, businessId), ExtPrSouRequirementVendor.class);

        List<ExtPrSouRequirementAttach> extPrSouRequirementAttachList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class).
                eq(ExtPrSouRequirementAttach::getRequirementHeadId, businessId), ExtPrSouRequirementAttach.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String  phone = null;
        String techUsername = null ;
        for(ExtPrSouRequirementGroup item:extPrSouRequirementGroupList){
            // 工作成员职责类型
            String  groupType   =  item.getGroupType();
            if("TECH".equals(groupType)){
                // bpm技术负责人联系方式
                phone = (item.getPhone());
                // bpm技术负责人 -- srm技术负责人账号
                techUsername = (item.getUsername());
            }
            break;
        }

        String categoryCode = purchaseRequirementHeadDto.getCategoryCode();
        Long categoryId = purchaseRequirementHeadDto.getCategoryId();
        PurchaseCategory category = new PurchaseCategory();
        category.setCategoryCode(categoryCode);
        category.setCategoryId(categoryId);
        category = baseClient.getPurchaseCategoryByParm(category);
        if (null != category) {
            categoryCode = category.getCategoryName();
        }

        // 组装主表数据
        Map<String, Object> mainTableData = new HashMap<>(50);
        // 是否招标
        mainTableData.put("SFZB",(purchaseRequirementHeadDto.getExtBidFlag() == "Y" ? "是": "否"));
        // 申请人
        mainTableData.put("SQR",extPrSouRequirementHead.getCreatedFullName());
        // 申请单号
        mainTableData.put("SQDH",purchaseRequirementHeadDto.getRequirementHeadNum());
        // 需求类型
        mainTableData.put("XQLX",getdemandType(purchaseRequirementHeadDto.getDemandType()));
        // 所属板块
        mainTableData.put("SSBK",extPrSouRequirementHead.getOrgBuName() );
        // 申请公司
        mainTableData.put("SQGS",purchaseRequirementHeadDto.getOrgName());
        // 申请部门
        mainTableData.put("SQBM",purchaseRequirementHeadDto.getCeeaDepartmentName() );
//申请日期
        String sqrq = null ;
        if(purchaseRequirementHeadDto.getApplyDate() != null){
            log.info("=============="+purchaseRequirementHeadDto.getApplyDate());
            sqrq = purchaseRequirementHeadDto.getApplyDate().toString();
        }
        // 申请日期
        mainTableData.put("SQRQ",sqrq);
        // 技术负责人
        mainTableData.put("JSFZR",techUsername);
        mainTableData.put("JSFZRLXFS",phone);
        // 需求来源
        mainTableData.put("XQLY",getrequireFrom(extPrSouRequirementHead.getRequireFrom()));
        //未报月度计划原因
        mainTableData.put("WBYDJHYY",extPrSouRequirementHead.getNoReportMonthPlanReason() );
        // 项目名称
        mainTableData.put("XMMC",extPrSouRequirementHead.getProjectName());
        // 月份
        mainTableData.put("YF",(extPrSouRequirementHead.getProjectMonth() == null) ?  null : extPrSouRequirementHead.getProjectMonth().toString() );
        // 所属品类
        mainTableData.put("SSPL",categoryCode);
        // 投资编号
        mainTableData.put("TZBH",extPrSouRequirementHead.getInvestNo());
        // 数量/规模
        mainTableData.put("SLGM",(extPrSouRequirementHead.getRequireQuantity() == null) ? null : extPrSouRequirementHead.getRequireQuantity());
        // 概算金额（万元）
        mainTableData.put("GSJR",(extPrSouRequirementHead.getTotalAmountByTenKilo() == null)?  null :  extPrSouRequirementHead.getTotalAmountByTenKilo().toString());
        // 是否公示
        mainTableData.put("SFGS",(extPrSouRequirementHead.getNeedPublic() == null) ? null : (extPrSouRequirementHead.getNeedPublic().toString() == "Y" ? "是" : "否"));
        // 不公示理由
        mainTableData.put("BGSLY",extPrSouRequirementHead.getNoPublicReason());
        //项目所在地
        mainTableData.put("XMSZD",extPrSouRequirementHead.getProjectAddress() );
        // 前置技术交流意向
        mainTableData.put("QZJSJLYX",(extPrSouRequirementHead.getPrefixTechDiscussion() == null) ? null : (extPrSouRequirementHead.getPrefixTechDiscussion().toString() == "Y" ? "是" : "否"));
        //公示截止时间
        String jzsj = null ;
        if(extPrSouRequirementHead.getPublicEndTime() != null){
            jzsj = simpleDateFormat.format(extPrSouRequirementHead.getPublicEndTime());
        }
        // 公示截止时间
        mainTableData.put("GSJZSJ",jzsj);

        //递交招标资料时间
        mainTableData.put("DJZBZLSJ",(extPrSouRequirementHead.getSendSouProfileEndDate() == null ) ? null : extPrSouRequirementHead.getSendSouProfileEndDate().toString());
        // 计划编号
        mainTableData.put("JHBH",extPrSouRequirementHead.getPlanNo());
        // 特殊招标类型
        //特定原因
        mainTableData.put("TSZBLX",getSpecialSouType(extPrSouRequirementHead.getSpecialSouType()));

        mainTableData.put("TDYY",getSpecialReason(extPrSouRequirementHead.getSpecialReason()));
        //需求产生时间
        String xqcssj = null ;
        if(extPrSouRequirementHead.getRequireProductDate() != null){
            xqcssj = extPrSouRequirementHead.getRequireProductDate().toString();
        }
        // 需求产生时间
        mainTableData.put("XQCSSJ",(xqcssj));
        // 需求产生时间附件
        mainTableData.put("XQCSSJFJ",dealFileList(extPrSouRequirementHead.getRequireProductFileId()));
        // 剩余时间
        mainTableData.put("SYSJ",extPrSouRequirementHead.getRemainingDay());
        // 工期交货期
        mainTableData.put("GQJHQ",extPrSouRequirementHead.getDeliveryDay());
        // 工期交货期附件
        mainTableData.put("GQJHQFJ",dealFileList(extPrSouRequirementHead.getDeliveryDayFileId()));
        //签合同用时
        mainTableData.put("QHTYS",(extPrSouRequirementHead.getSignContractDay() == null) ? null : extPrSouRequirementHead.getSignContractDay().toString());
        // 投入使用时间附件
        mainTableData.put("TRSYSJFJ",dealFileList(extPrSouRequirementHead.getPutIntoUseDateFileId()));
        //投入使用时间
        String trsysj = null ;
        if(extPrSouRequirementHead.getPutIntoUseDate() != null){
            trsysj = extPrSouRequirementHead.getPutIntoUseDate().toString();
        }
        //投入使用时间
        mainTableData.put("TRSYSJ",trsysj);
        // 其他特殊原因
        mainTableData.put("QTTSYY",extPrSouRequirementHead.getOtherSpecialReason());
        //变更后概算金额（万元）
        mainTableData.put("BGHGSJE",(extPrSouRequirementHead.getAfterTotalAmountByTenKilo() == null) ? null : extPrSouRequirementHead.getAfterTotalAmountByTenKilo().toString() );
        // 变更原因
        mainTableData.put("BGYY",extPrSouRequirementHead.getChangeReason());
        // 项目概况及范围
        mainTableData.put("XMGKJFW",extPrSouRequirementHead.getProjectOverview() );
        // 技术要求
        mainTableData.put("JSYQ",extPrSouRequirementHead.getTechRequire());
        // 业绩要求
        mainTableData.put("YJYQ",extPrSouRequirementHead.getPerformanceRequire());
        //供应商资质要求
        mainTableData.put("GYSZZYQ",extPrSouRequirementHead.getVendorQualificationRequire());

        //招标计划推荐供应商表
        List<Object> itemdata = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(extPrSouRequirementVendorList)) {
            extPrSouRequirementVendorList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                // 推荐单位名称
                map.put("TJDWMC","");
                // 联系方式
                map.put("LXFS","");
                // 邮箱
                map.put("YX","");
                map.put("__TABLE", "BO_EU_XQBGZB1");
                itemdata.add(map);
            });
        }


        //招标计划附件表
        if (CollectionUtils.isNotEmpty(extPrSouRequirementAttachList)) {
            extPrSouRequirementAttachList.forEach(e -> {
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
                // 文件上传时间
                map.put("SCSJ",e.getUpdateDate());
                // 文件类型
                map.put("WJLX",e.getFileType());
                map.put("__TABLE", "BO_EU_XQBGZB2");
                itemdata.add(map);
            });
        }

        String processtitle = "招标需求变更";
        String maintable = "BO_EU_XQBG";


        ArrayList<String> mainFile = Lists.newArrayList("XQCSSJFJ","GQJHQFJ","TRSYSJFJ");

        Map<String,Object> itemFile = new HashMap<>(50);
        List<String> fList = new ArrayList<>();
        fList.add("FJ");
        itemFile.put("BO_EU_XQBGZB2", fList);

        List<String> mainfList = new ArrayList<>();
        mainfList.add("XQCSSJFJ");
        mainfList.add("GQJHQFJ");
        mainfList.add("TRSYSJFJ");
        itemFile.put("BO_EU_XQBG", mainfList);

        // 其他子表
        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_XQBGZB1");
        itemtable.add("BO_EU_XQBGZB2");



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
                createOrgId, createUser, itemtable, itemdata, itemFile, mainFile);
        log.info("===================进入招标计划变更组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========招标需求变更JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        log.info("submitFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                FlowInstanceRecord record = pjProjectExtClient.getLastFlowInstanceRecord(new FlowInstanceRecord().setTemplateCode(BUSINESS_TYPE).setBusinessId(businessId));
                if(record!=null&& StringUtils.isNotBlank(record.getInstanceId())){
                    prRequirementInitEventService.callbackAfterApprovalSubmit(businessId);

                    LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                    Record r = new Record();
                    r.put(ExtPrSouRequirementHead::getRequirementHeadId,businessId);
                    r.put(ExtPrSouRequirementHead::getStartBpmUsername, loginAppUser.getUsername());
                    r.put(ExtPrSouRequirementHead::getStartBpmNickname, loginAppUser.getNickname());
                    qlService.update("ExtPrSouRequirementHead", Arrays.asList(r));
                    pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
                }
            }
        }else{
            prRequirementInitEventService.callbackAfterApprovalSubmit(businessId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("passFlow: {}, {}", businessId, param);
        prRequirementInitEventService.callbackAfterApprovalPass(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        log.info("rejectFlow: {}, {}", businessId, param);
        prRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.REJECTED));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        log.info("withdrawFlow: {}, {}", businessId, param);
        prRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.WITHDRAW));
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        log.info("destoryFlow: {}, {}", businessId, param);
        prRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.ABANDONED));
    }


    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        return null;
    }


    public String getdemandType(String type){
        String materialCategory = "Material_category";
        String engineering = "Engineering";
        String informationTechnology = "Information_technology";
        String marketing = "MARKETING";
        String service = "SERVICE";
        String equipment = "Equipment";
        if(materialCategory.equals(type)){
            return "物资类" ;
        }else if(engineering.equals(type)){
            return "工程类" ;
        }else if(informationTechnology.equals(type)){
            return "信息技术类" ;
        }else if(marketing.equals(type)){
            return "营销类" ;
        }else if(service.equals(type)){
            return "服务类" ;
        }else if(equipment.equals(type)){
            return "设备类" ;
        }else {
            return type ;
        }

    }


    public String getSpecialSouType(String type){
        String specialVendorOne = "SPECIAL_VENDOR_ONE";
        String timeUrgent = "TIME_URGENT";
        String other = "OTHER";
        if(specialVendorOne.equals(type)){
            return "特定原因使得供应商唯一" ;
        }else if(timeUrgent.equals(type)){
            return "时间紧急" ;
        }else if(other.equals(type)){
            return "其他" ;
        }else {
            return type ;
        }

    }


    public String getrequireFrom(String type){
        String year = "YEAR";
        String month = "MONTH";
        String withoutPlan = "WITHOUT_PLAN";
        String specialSou = "SPECIAL_SOU";
        if(year.equals(type)){
            return "年度" ;
        }else if(month.equals(type)){
            return "月度" ;
        }else if(withoutPlan.equals(type)){
            return "计划外" ;
        }else if(specialSou.equals(type)){
            return "特殊招标" ;
        }else {
            return type ;
        }

    }

    public String getSpecialReason(String type){
        String monopoly = "MONOPOLY";
        String government = "GOVERNMENT";
        String factoryCoop = "FACTORY_COOP";
        String other = "OTHER";
        if(monopoly.equals(type)){
            return "垄断" ;
        }else if(government.equals(type)){
            return "政府定制" ;
        }else if(factoryCoop.equals(type)){
            return "原厂合作" ;
        }else if(other.equals(type)){
            return "其他" ;
        }else {
            return type ;
        }

    }

    public List<Map<String, Object>> dealFileList(Long fileId)  {
        List<Map<String, Object>> fileList = new ArrayList<>();
        if(fileId != null){
            com.midea.cloud.srm.model.file.upload.entity.Fileupload fileupload = new com.midea.cloud.srm.model.file.upload.entity.Fileupload();
            fileupload.setFileuploadId(fileId);
            fileupload.setPageNum(1);
            fileupload.setPageSize(1);
            PageInfo<com.midea.cloud.srm.model.file.upload.entity.Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
            List<com.midea.cloud.srm.model.file.upload.entity.Fileupload> list = fileuploads.getList();

            list.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                map.put("FILE_PATH_BYMOBILE", "");
                map.put("FILE_NAME", e.getFileSourceName());
                String mes = "fileSourceName="+e.getFileSourceName()+"&fileuploadId="+e.getFileuploadId();
                map.put("FILE_PATH", fileDownloadPath+mes);
                fileList.add(map);
            });
        }
        return fileList;
    }

}
