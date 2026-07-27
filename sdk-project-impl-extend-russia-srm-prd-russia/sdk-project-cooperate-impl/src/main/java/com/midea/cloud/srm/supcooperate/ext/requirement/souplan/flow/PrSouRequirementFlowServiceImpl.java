package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.flow;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.midea.cloud.srm.constant.NumConstant;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.scene.entity.SceneTemplate;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementApprovalUnPassDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 招标计划 - 立项审批流回调定义
 * PS: 审批流编码  MQL_PR_SOU_REQUIREMENT_INIT
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.TSZBJH.processGroupId}")
    private String tsProcessGroupId;

    @Value("${bpm.ZCZBJH.processGroupId}")
    private String zcProcessGroupId;
    @Value("${bpm.ZCZBJH.processGroupId2}")
    private String zcProcessGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    private MqlPrRequirementInitEventService mqlPrRequirementInitEventService;

    @Autowired
    private QlService qlService;

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    BaseExtClient baseExtClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private IDivisionCategoryService divisionCategoryService;

    @Autowired
    private RbacClient rbacClient;

    private static final int NUM = 16;

    private static final String BUSINESS_TYPE = "MQL_PR_SOU_REQUIREMENT_INIT";

    @Autowired
    private RedisUtil redisUtil;

    private static final String SPECIAL_SOU = "SPECIAL_SOU";

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

        QueryWrapper<DivisionCategory> dcQuery = new QueryWrapper<>();
        dcQuery.eq("ORG_ID", purchaseRequirementHeadDto.getOrgId());
        dcQuery.eq("CATEGORY_ID", purchaseRequirementHeadDto.getCategoryId());
        //金额   TOTAL_AMOUNT_BY_TEN_KILO
        if (SouConstant.SPECIAL_SOU.equals(extPrSouRequirementHead.getRequireFrom())) {
            dcQuery.eq("IF_MAIN_PERSON", "N");
        } else {
            int te = 10;
            if (extPrSouRequirementHead.getTotalAmountByTenKilo().compareTo(new BigDecimal(te)) <= 0) {
                //小于等于10万
                dcQuery.eq("IF_MAIN_PERSON", "N");
            } else {
                //大于10万
                dcQuery.eq("IF_MAIN_PERSON", "Y");
            }
        }

        String gysfzr = "";
        List<DivisionCategory> divisionList = divisionCategoryService.list(dcQuery);
        if (!divisionList.isEmpty()) {

            // 主供应商负责人
            DivisionCategory zgysfzr = divisionList.stream().filter(e -> "Supplier Leader".equals(e.getDuty())).collect(Collectors.toList())
                    .stream().findFirst().orElse(null);
            if(zgysfzr != null){
                gysfzr = zgysfzr.getPersonInChargeUsername();
            }
        }

        JSONObject processVars = new JSONObject();
        processVars.put("XQLY",getrequireFrom(extPrSouRequirementHead.getRequireFrom()));
        processVars.put("SQGS",purchaseRequirementHeadDto.getOrgName());
        processVars.put("GSJE",(extPrSouRequirementHead.getTotalAmountByTenKilo() == null)?  null :  extPrSouRequirementHead.getTotalAmountByTenKilo().toString());
        processVars.put("SFSYZBFW",((ifBid == "Y" ) ? "是" : "否"));
        processVars.put("XMMC",extPrSouRequirementHead.getProjectName());
        processVars.put("GYSFZR",gysfzr);

        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        String processTitle = "";
        if(StringUtils.equals(SPECIAL_SOU,extPrSouRequirementHead.getRequireFrom())){
            processTitle = "特殊招标";
        }else{
            processTitle = "招标计划";
        }
        bpmParam.setProcessTitle(processTitle+"-"+extPrSouRequirementHead.getProjectName());
        bpmParam.setProcessGroupId(zcProcessGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("===================进入招标组装数据方法"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ExtPrSouRequirementHead extPrSouRequirementHead = qlService.readByKey("ExtPrSouRequirementHead",businessId, ExtPrSouRequirementHead.class);
        List<PurchaseRequirementHeadDTO> requirementHeads = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementHead")
                .eq(PurchaseRequirementHeadDTO::getRequirementHeadId, businessId) ,PurchaseRequirementHeadDTO.class);
        PurchaseRequirementHeadDTO purchaseRequirementHeadDto = requirementHeads.get(0);

        List<ExtPrSouRequirementGroup> extPrSouRequirementGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class).
                eq(ExtPrSouRequirementGroup::getRequirementHeadId, businessId), ExtPrSouRequirementGroup.class);

        List<ExtPrSouRequirementVendor> extPrSouRequirementVendorList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class).
                eq(ExtPrSouRequirementVendor::getRequirementHeadId, businessId), ExtPrSouRequirementVendor.class);

        List<ExtPrSouRequirementAttach> extPrSouRequirementAttachList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class).
                eq(ExtPrSouRequirementAttach::getRequirementHeadId, businessId), ExtPrSouRequirementAttach.class);
        String zzbfzrCeeaempno = "";
        String zzbfzrNickname = "";

        String zgysfzrCeeaEmpNo = "";
        String zgysfzrNickname = "";

        //  采购需求的bpm审批，需要增加两个字段：招标负责人、供应商负责人 分别传工号和姓名
        PrRequirementHead prHead = qlService.readByKey(PrRequirementHead.class.getSimpleName(), businessId, PrRequirementHead.class);
        QueryWrapper<DivisionCategory> dcQuery = new QueryWrapper<>();
        dcQuery.eq("ORG_ID", prHead.getOrgId());
        dcQuery.eq("CATEGORY_ID", prHead.getCategoryId());
        //金额   TOTAL_AMOUNT_BY_TEN_KILO
        if (SouConstant.SPECIAL_SOU.equals(extPrSouRequirementHead.getRequireFrom())) {
            dcQuery.eq("IF_MAIN_PERSON", "N");
        } else {
            int te = 10;
            if (extPrSouRequirementHead.getTotalAmountByTenKilo().compareTo(new BigDecimal(te)) <= 0) {
                //小于等于10万
                dcQuery.eq("IF_MAIN_PERSON", "N");
            } else {
                //大于10万
                dcQuery.eq("IF_MAIN_PERSON", "Y");
            }
        }
        List<DivisionCategory> divisionList = divisionCategoryService.list(dcQuery);
        if (!divisionList.isEmpty()) {
            // 主招标负责人
            DivisionCategory zzbfzr = divisionList.stream().filter(e -> "Person in charge of bidding".equals(e.getDuty())).collect(Collectors.toList())
                    .stream().findFirst().orElse(null);
            if (zzbfzr != null) {
                zzbfzr.getPersonInChargeUsername();
                User user =  rbacClient.findByUsername(zzbfzr.getPersonInChargeUsername());
                // 工号
                zzbfzrCeeaempno =   user.getCeeaEmpNo();
                // 姓名
                zzbfzrNickname = user.getNickname();
            }
            // 主供应商负责人
            DivisionCategory zgysfzr = divisionList.stream().filter(e -> "Supplier Leader".equals(e.getDuty())).collect(Collectors.toList())
                    .stream().findFirst().orElse(null);
            if(zgysfzr != null){
                zgysfzr.getPersonInChargeUsername();
                User user =  rbacClient.findByUsername(zgysfzr.getPersonInChargeUsername());
                // 工号
                zgysfzrCeeaEmpNo = user.getCeeaEmpNo();
                // 姓名
                zgysfzrNickname = user.getNickname();

            }
        }




        String  phone = null;
        String techUsername = null ;
        for(ExtPrSouRequirementGroup item:extPrSouRequirementGroupList){
            // 工作成员职责类型
            String  groupType   =  item.getGroupType();
            if("TECH".equals(groupType)){
                // bpm技术负责人联系方式
                phone = (item.getPhone());
                // bpm技术负责人 -- srm技术负责人账号
                techUsername = (item.getFullName());
            }
            break;
        }
        JSONObject dataPushFlowJsn = null ;
        /***是否招标=是，且需求来源=年度/月度/计划外，----正常招标计划表单
         是否招标=是，且需求来源=特殊招标-----特殊招标表单
         是否招标=否，采购申请表单*/
        // 根据那个需求来源可以判断，月度/年度/计划外一个，特殊招标一个
        // 需求来源
        String requireFrom = extPrSouRequirementHead.getRequireFrom();
        //  是否招标，Y/N
        String extBidFlag = purchaseRequirementHeadDto.getExtBidFlag() ;
        // 所属品类 ,需要根据所属品类去查 是否属于招标范围
        String categoryCode = purchaseRequirementHeadDto.getCategoryCode();
        Long categoryId = purchaseRequirementHeadDto.getCategoryId();
        String ifBid = "N";
        PurchaseCategory category = new PurchaseCategory();
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
        String specialSou = "SPECIAL_SOU";
        // 主表信息
        // 这是招标
        if(YesOrNo.YES.getValue().equals(extBidFlag)){
            // 如果是特殊招标
            if(specialSou.equals(requireFrom) ){
                Map<String, Object> mainTableData = new HashMap<>(50);
                // 概算金额（万元）
                mainTableData.put("JE",extPrSouRequirementHead.getTotalAmountByTenKilo());
                //是否属于招标范围
                mainTableData.put("SFSYZBFW",((ifBid == "Y" ) ? "是" : "否"));
                //bpm是否招标
                mainTableData.put("SFZB","是");
                // 申请人--申请人账号
                mainTableData.put("SQR",extPrSouRequirementHead.getCreatedFullName());
                //申请单号
                mainTableData.put("SQDH",purchaseRequirementHeadDto.getRequirementHeadNum());
                // bpm需求类型
                mainTableData.put("XQLX",getdemandType(purchaseRequirementHeadDto.getDemandType()));
                // 所属板块--板块编码
                mainTableData.put("SSBK",extPrSouRequirementHead.getOrgBuName());
                // bpm申请公司--srm公司编码
                mainTableData.put("SQGS",purchaseRequirementHeadDto.getOrgName());
                // bpm申请部门 -- srm部门编码
                mainTableData.put("SQBM",purchaseRequirementHeadDto.getCeeaDepartmentName());
                //bpm申请日期 --
                mainTableData.put("SQRQ",(purchaseRequirementHeadDto.getApplyDate() == null) ? null : purchaseRequirementHeadDto.getApplyDate().toString() );
                mainTableData.put("JSFZR",techUsername);
                mainTableData.put("JSFZRLXFS",phone);
                // 需求来源
                mainTableData.put("XQLY",getrequireFrom(extPrSouRequirementHead.getRequireFrom()));
                // 项目名称
                mainTableData.put("XMMC",extPrSouRequirementHead.getProjectName());
                // 月份
                mainTableData.put("YF",(extPrSouRequirementHead.getProjectMonth() == null) ?  null : extPrSouRequirementHead.getProjectMonth().toString());
                // bpm所属品类 -- srm品类编码
                mainTableData.put("SSPL",categoryCode);
                // 投资编号
                mainTableData.put("TZBH",extPrSouRequirementHead.getInvestNo());
                // 数量/规模
                mainTableData.put("SLGM",(extPrSouRequirementHead.getRequireQuantity() == null) ?  null : extPrSouRequirementHead.getRequireQuantity().toString());
                // 概算金额（万元）
                mainTableData.put("GSJE",(extPrSouRequirementHead.getTotalAmountByTenKilo() == null)?  null :  extPrSouRequirementHead.getTotalAmountByTenKilo().toString());
                // 是否公示
                mainTableData.put("SFGS",(extPrSouRequirementHead.getNeedPublic() == null) ? null : (extPrSouRequirementHead.getNeedPublic().toString() == "Y" ? "是" : "否"));
                // 不公示理由
                mainTableData.put("BGSLY",extPrSouRequirementHead.getNoPublicReason());
                // 项目所在地
                mainTableData.put("XMSZD",extPrSouRequirementHead.getProjectAddress());
                //计划编号
                mainTableData.put("JHBH",extPrSouRequirementHead.getPlanNo());
                // 特殊招标类型
                mainTableData.put("TSZPLX",getSpecialSouType(extPrSouRequirementHead.getSpecialSouType()));
                // 特定原因
                mainTableData.put("TDYY",getSpecialReason(extPrSouRequirementHead.getSpecialReason()));
                //需求产生时间
                String xqcssj = null ;
                if(extPrSouRequirementHead.getRequireProductDate() != null){
                    log.info("==============="+extPrSouRequirementHead.getRequireProductDate());
                    xqcssj = extPrSouRequirementHead.getRequireProductDate().toString();
                }
                // 需求产生时间
                mainTableData.put("XQCSSJ",(xqcssj));
                // 需求产生时间附件
                mainTableData.put("XQCSSJFJ",dealFileList(extPrSouRequirementHead.getRequireProductFileId()));
//剩余时间
                mainTableData.put("SYSJ",extPrSouRequirementHead.getRemainingDay()) ;
// 工期交货期
                mainTableData.put("GQJHQ",extPrSouRequirementHead.getDeliveryDay());

                mainTableData.put("GQJHQFJ",dealFileList(extPrSouRequirementHead.getDeliveryDayFileId()));
                // 签合同用时
                mainTableData.put("QHTYS",(extPrSouRequirementHead.getSignContractDay() == null) ? null : extPrSouRequirementHead.getSignContractDay().toString());
                // 投入使用时间附件
                mainTableData.put("TRSJFJ",dealFileList(extPrSouRequirementHead.getPutIntoUseDateFileId()));
                //投入使用时间
                String trsysj = null ;
                if(extPrSouRequirementHead.getPutIntoUseDate() != null){
                    log.info("==============="+extPrSouRequirementHead.getPutIntoUseDate());
                    trsysj = extPrSouRequirementHead.getPutIntoUseDate().toString();
                }
                //投入使用时间
                mainTableData.put("TRSYSJ",trsysj);
                // 其他特殊原因补充
                mainTableData.put("QTTSYYBC",extPrSouRequirementHead.getOtherSpecialReason());
                // 项目概括及范围
                mainTableData.put("XMGKJFW",extPrSouRequirementHead.getProjectOverview());
                //技术要求
                mainTableData.put("JSYQ",extPrSouRequirementHead.getTechRequire());
                // 业绩要求
                mainTableData.put("YJYQ",extPrSouRequirementHead.getPerformanceRequire());
                // 供应商资质要求
                mainTableData.put("GYSZZYQ",extPrSouRequirementHead.getVendorQualificationRequire());

                // 主招标负责人
                // 工号
                mainTableData.put("ZZBFZR_CEEAEMPNO",zzbfzrCeeaempno);
                // 姓名
                mainTableData.put("ZZBFZR_NICKNAME",zzbfzrNickname);

                //主供应商负责人
                // 工号
                mainTableData.put("ZGYSFZR_CEEAEMPNO",zgysfzrCeeaEmpNo);
                // 姓名
                mainTableData.put("ZGYSFZR_NICKNAME",zgysfzrNickname);

                //招标计划推荐供应商表
                List<Object> itemdata = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(extPrSouRequirementVendorList)) {
                    extPrSouRequirementVendorList.forEach(e -> {
                        Map<String, Object> map = new HashMap<>(50);
                        map.put("TJDWMC",e.getVendorName());
                        map.put("LXFS",e.getPhone());
                        map.put("YX",e.getEmail());
                        map.put("TJDWLY",e.getRecommendFrom());
                        map.put("__TABLE", "BO_EU_CJZBJHZB1");
                        itemdata.add(map);
                    });
                }


                // 附件信息
                if (CollectionUtils.isNotEmpty(extPrSouRequirementAttachList)) {
                    extPrSouRequirementAttachList.forEach(e -> {
                        Map<String, Object> map = new HashMap<>(50);
                        List<Map<String, Object>> file = new ArrayList<>();
                        Map<String, Object> fileMap = new HashMap<>(50);

                        Fileupload fileupload = new Fileupload();
                        fileupload.setFileuploadId(e.getFileId()); // 【这里需要确认下是否是fileUploadid】
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
                        map.put("SCSJ",e.getUpdateDate()); // 文件上传时间
                        map.put("MJLX",e.getFileType()); // 文件类型
                        map.put("__TABLE", "BO_EU_XGFJSCZB");
                        itemdata.add(map);
                    });
                }
                Map<String, Object> headFileMap1 = new HashMap<>(NUM);
                headFileMap1.put("__TABLE", "BO_EU_TSZBFJZB");
                headFileMap1.put("BTMC", "需求产生时间附件");
                headFileMap1.put("FJMC", dealFileList(extPrSouRequirementHead.getRequireProductFileId()));
                itemdata.add(headFileMap1);
                Map<String, Object> headFileMap2 = new HashMap<>(NUM);
                headFileMap2.put("__TABLE", "BO_EU_TSZBFJZB");
                headFileMap2.put("BTMC", "工期交货期附件");
                headFileMap2.put("FJMC", dealFileList(extPrSouRequirementHead.getDeliveryDayFileId()));
                itemdata.add(headFileMap2);
                Map<String, Object> headFileMap3 = new HashMap<>(NUM);
                headFileMap3.put("__TABLE", "BO_EU_TSZBFJZB");
                headFileMap3.put("BTMC", "投入使用时间附件");
                headFileMap3.put("FJMC", dealFileList(extPrSouRequirementHead.getPutIntoUseDateFileId()));
                itemdata.add(headFileMap3);


                Map<String,Object> itemFile = new HashMap<>(50);
                List<String> fList = new ArrayList<>();
                fList.add("FJ");
                itemFile.put("BO_EU_XGFJSCZB", fList);
                itemFile.put("BO_EU_TSZBFJZB", BpmResult.getFileField("FJMC"));
                itemFile.put("BO_EU_CJZBJH", BpmResult.getFileField("XQCSSJFJ,GQJHQFJ,TRSJFJ"));

                String processtitle = "特殊招标-" + extPrSouRequirementHead.getProjectName();
                String maintable = "BO_EU_CJZBJH";

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                String createuser = loginAppUser.getUsername();

                String createorgid = null;
                SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createuser);
                if (sccPjUser != null && sccPjUser.getGroupId() != null) {
                    createorgid = String.valueOf(sccPjUser.getGroupId());
                }
                if (com.alibaba.cloud.commons.lang.StringUtils.isBlank(createorgid)) {
                    throw new RuntimeException("查询不到hr组织id");
                }

                ArrayList<String> mainFile = Lists.newArrayList("XQCSSJFJ","GQJHQFJ","TRSJFJ");

                List<String> itemtable = new ArrayList<>();
                itemtable.add("BO_EU_CJZBJHZB1");
                itemtable.add("BO_EU_XGFJSCZB");

                // 特殊招标要改成正常招标的流程组 220231119
                dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, zcProcessGroupId, appId,
                        createorgid, createuser, itemtable, itemdata, itemFile, mainFile);
                log.info("===================进入招标组装数据方法结束"+dataPushFlowJsn.toString());

            }
            else{
                //正常招标计划
                Map<String, Object> mainTableData = new HashMap<>(50);
                // 概算金额（万元）
                mainTableData.put("JE",extPrSouRequirementHead.getTotalAmountByTenKilo());
                //是否属于招标范围
                mainTableData.put("SFSYZBFW",((ifBid == "Y" ) ? "是" : "否"));
                //bpm是否招标
                mainTableData.put("SFZB","是");
                // 申请人--申请人账号
                mainTableData.put("SQR",extPrSouRequirementHead.getCreatedFullName());
                //申请单号
                mainTableData.put("SQDH",purchaseRequirementHeadDto.getRequirementHeadNum());
                // bpm需求类型
                mainTableData.put("XQLX",getdemandType(purchaseRequirementHeadDto.getDemandType()));
                // 所属板块--板块编码
                mainTableData.put("SSBK",extPrSouRequirementHead.getOrgBuName());
                // bpm申请公司--srm公司编码
                mainTableData.put("SQGS",purchaseRequirementHeadDto.getOrgName());
                // bpm申请部门 -- srm部门编码
                mainTableData.put("SQBM",purchaseRequirementHeadDto.getCeeaDepartmentName());
                log.info("===================申请部门=================="+purchaseRequirementHeadDto.getCeeaDepartmentName());
                //bpm申请日期 --
                mainTableData.put("SQRQ",(purchaseRequirementHeadDto.getApplyDate() == null) ? null : purchaseRequirementHeadDto.getApplyDate().toString() );
                mainTableData.put("JSFZR",techUsername);
                mainTableData.put("JSFZRLXFS",phone);
                // 需求来源
                mainTableData.put("XQLY",getrequireFrom(extPrSouRequirementHead.getRequireFrom()));
                // 月份
                mainTableData.put("YF",(extPrSouRequirementHead.getProjectMonth() == null) ?  null : extPrSouRequirementHead.getProjectMonth().toString());
                // bpm所属品类 -- srm品类编码
                mainTableData.put("SSPL",categoryCode);
                // 投资编号
                mainTableData.put("TZBH",extPrSouRequirementHead.getInvestNo());
                // 数量/规模
                mainTableData.put("SLGM",(extPrSouRequirementHead.getRequireQuantity() == null) ?  null : extPrSouRequirementHead.getRequireQuantity().toString());
                // 概算金额（万元）
                mainTableData.put("GSJE",(extPrSouRequirementHead.getTotalAmountByTenKilo() == null)?  null :  extPrSouRequirementHead.getTotalAmountByTenKilo().toString());
                // 是否公示
                mainTableData.put("SFGS",(extPrSouRequirementHead.getNeedPublic() == null) ? null : (extPrSouRequirementHead.getNeedPublic().toString() == "Y" ? "是" : "否"));
                // 不公示理由
                mainTableData.put("BGSLY",extPrSouRequirementHead.getNoPublicReason());
                // 项目所在地
                mainTableData.put("XMSZD",extPrSouRequirementHead.getProjectAddress());
                // 供应商资质要求
                mainTableData.put("GYSZZYQ",extPrSouRequirementHead.getVendorQualificationRequire());
                //计划编号
                mainTableData.put("JHBH",extPrSouRequirementHead.getPlanNo());
                // 项目概括及范围
                mainTableData.put("XMGKJFW",extPrSouRequirementHead.getProjectOverview());
                //技术要求
                mainTableData.put("JSYQ",extPrSouRequirementHead.getTechRequire());
                // 业绩要求
                mainTableData.put("YJYQ",extPrSouRequirementHead.getPerformanceRequire());
                // 项目名称
                mainTableData.put("XMMC",extPrSouRequirementHead.getProjectName());
                // 前置技术交流意向
                mainTableData.put("QZJSJLYX",(extPrSouRequirementHead.getPrefixTechDiscussion() == null) ? null : (extPrSouRequirementHead.getPrefixTechDiscussion().toString() == "Y" ? "是" : "否"));

                // 主招标负责人
                // 工号
                mainTableData.put("ZZBFZR_CEEAEMPNO",zzbfzrCeeaempno);
                // 姓名
                mainTableData.put("ZZBFZR_NICKNAME",zzbfzrNickname);

                //主供应商负责人
                // 工号
                mainTableData.put("ZGYSFZR_CEEAEMPNO",zgysfzrCeeaEmpNo);
                // 姓名
                mainTableData.put("ZGYSFZR_NICKNAME",zgysfzrNickname);

//递交招标资料时间
                String djzbzlsj = null ;
                if(extPrSouRequirementHead.getSendSouProfileEndDate() != null){
                    djzbzlsj = extPrSouRequirementHead.getSendSouProfileEndDate().toString();
                }
                // 递交招标资料时间
                mainTableData.put("DJZBZLSJ",(djzbzlsj));
//公示截止时间
                String jzsj = null ;
                if(extPrSouRequirementHead.getPublicEndTime() != null){
                    log.info("===================公示截止时间=================="+extPrSouRequirementHead.getPublicEndTime());
                    jzsj = simpleDateFormat.format(extPrSouRequirementHead.getPublicEndTime());
                    log.info("===================公示截止时间=================="+jzsj);
                }
                // 公示截止时间
                mainTableData.put("GSJZSJ",jzsj);
                // 未报月度计划原因
                mainTableData.put("WBYDJHYY",extPrSouRequirementHead.getNoReportMonthPlanReason());

                // 德敬添加六个字段
                // 不公示原因
                mainTableData.put("JTYYSM",getNoPublicReasonChoose(extPrSouRequirementHead.getNoPublicReasonChoose()));
                // 是否指定品牌
                mainTableData.put("SFZDPP",(extPrSouRequirementHead.getIfAppointBrand() == null) ? null : (StringUtils.equals(extPrSouRequirementHead.getIfAppointBrand().toString() , "Y") ? "是" : "否"));
                // 指定品牌文件
                mainTableData.put("ZDPPWJ",dealFileList(extPrSouRequirementHead.getAppointBrandFileId()));
                // 是否限定单位
                mainTableData.put("SFXDDW",(extPrSouRequirementHead.getIfQualifyUnit() == null) ? null : (StringUtils.equals(extPrSouRequirementHead.getIfQualifyUnit().toString() , "Y") ? "是" : "否"));
                // 限定单位文件
                mainTableData.put("XDDWWJ",dealFileList(extPrSouRequirementHead.getQualifyUnitFileId()));

                SceneTemplate sceneTemplate = new SceneTemplate();
                sceneTemplate.setSceneModuleCode("SCENE_EXT_REQ_SOU_FILE_DF1");
                List<SceneTemplate> sceneTemplatelist = baseExtClient.listSceneTemplate(sceneTemplate);
                if(CollectionUtils.isNotEmpty(sceneTemplatelist)){
                    // 板块接口人名单
                    mainTableData.put("BKJKRJD",dealFileList(sceneTemplatelist.get(0).getTemplateFileId()));
                }else{
                    // 板块接口人名单
                    mainTableData.put("BKJKRJD","");
                }



                //招标计划推荐供应商表
                List<Object> itemdata = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(extPrSouRequirementVendorList)) {
                    extPrSouRequirementVendorList.forEach(e -> {
                        Map<String, Object> map = new HashMap<>(50);
                        map.put("TJDWMC",e.getVendorName());
                        map.put("LXR",e.getContactName());
                        map.put("LXFS",e.getPhone());
                        map.put("YX",e.getEmail());
                        map.put("TJDWLY",e.getRecommendFrom());
                        map.put("__TABLE", "BO_EU_TJDWMC");
                        itemdata.add(map);
                    });
                }


                // 附件信息
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
                        map.put("LX",e.getFileType());
                        map.put("__TABLE", "BO_EU_SRMXGFJSC");
                        itemdata.add(map);
                    });
                }
                Map<String, Object> headFileMap1 = new HashMap<>(NUM);
                headFileMap1.put("__TABLE", "BO_EU_ZCZBFJZB");
                headFileMap1.put("BTMC", "需求产生时间附件");
                headFileMap1.put("FJMC", dealFileList(extPrSouRequirementHead.getRequireProductFileId()));
                itemdata.add(headFileMap1);
                Map<String, Object> headFileMap2 = new HashMap<>(NUM);
                headFileMap2.put("__TABLE", "BO_EU_ZCZBFJZB");
                headFileMap2.put("BTMC", "工期交货期附件");
                headFileMap2.put("FJMC", dealFileList(extPrSouRequirementHead.getDeliveryDayFileId()));
                itemdata.add(headFileMap2);
                Map<String, Object> headFileMap3 = new HashMap<>(NUM);
                headFileMap3.put("__TABLE", "BO_EU_ZCZBFJZB");
                headFileMap3.put("BTMC", "投入使用时间附件");
                headFileMap3.put("FJMC", dealFileList(extPrSouRequirementHead.getPutIntoUseDateFileId()));
                itemdata.add(headFileMap3);


                Map<String,Object> itemFile = new HashMap<>(50);
                List<String> fList = new ArrayList<>();
                fList.add("FJ");
                itemFile.put("BO_EU_SRMXGFJSC", fList);
                itemFile.put("BO_EU_ZCZBFJZB", BpmResult.getFileField("FJMC"));

                String processtitle = "正常招标计划-"+extPrSouRequirementHead.getProjectName();
                String mainTable = "BO_EU_SRMZCZB";

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                String createuser = loginAppUser.getUsername();

                String createorgid = null;
                SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createuser);
                if (sccPjUser != null && sccPjUser.getGroupId() != null) {
                    createorgid = String.valueOf(sccPjUser.getGroupId());
                }
                if (com.alibaba.cloud.commons.lang.StringUtils.isBlank(createorgid)) {
                    throw new RuntimeException("查询不到hr组织id");
                }

                ArrayList<String> mainFile = Lists.newArrayList("ZDPPWJ","XDDWWJ","BKJKRJD");

                List<String> itemTable = new ArrayList<>();
                itemTable.add("BO_EU_TJDWMC");
                itemTable.add("BO_EU_SRMXGFJSC");

                dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, mainTable, mainTableData, zcProcessGroupId, appId,
                        createorgid, createuser, itemTable, itemdata, itemFile, mainFile);
                log.info("===================进入招标组装数据方法结束"+dataPushFlowJsn.toString());

            }

        }else{   // 这是采购

        }


        return JsonUtil.entityToJsonStr(dataPushFlowJsn);


    }




    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        log.info("submitFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                FlowInstanceRecord record = pjProjectExtClient.getLastFlowInstanceRecord(new FlowInstanceRecord().setTemplateCode(BUSINESS_TYPE).setBusinessId(businessId));
                if(record!=null&&StringUtils.isNotBlank(record.getInstanceId())){
                    mqlPrRequirementInitEventService.callbackAfterApprovalSubmit(businessId);

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
            mqlPrRequirementInitEventService.callbackAfterApprovalSubmit(businessId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("passFlow: {}, {}", businessId, param);
        mqlPrRequirementInitEventService.callbackAfterApprovalPass(businessId);

        Record r = new Record();
        r.put(RequirementHead::getRequirementHeadId, businessId);
        r.put(RequirementHead::getAuditStatus, RequirementApproveStatus.APPROVED);
        r.put(PurchaseRequirementHeadDTO::getExtApproveTime, LocalDateTime.now());
        qlService.update("PurchaseRequirementHead", Arrays.asList(r));
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        log.info("rejectFlow: {}, {}", businessId, param);
        mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.REJECTED));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        log.info("withdrawFlow: {}, {}", businessId, param);
        mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.WITHDRAW));
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        log.info("destoryFlow: {}, {}", businessId, param);
        mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(
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

    public String getNoPublicReasonChoose(String type){
        String a = "TIME_URGENT";
        String b = "GROUP_STRATEGY";
        String c = "LIMIT_VENDOR";
        String d = "OTHERS";
        if(a.equals(type)){
            return "时间紧急" ;
        }else if(b.equals(type)){
            return "项目集团战略级别保密" ;
        }else if(c.equals(type)){
            return "限定供应商范围" ;
        }else if(d.equals(type)){
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
