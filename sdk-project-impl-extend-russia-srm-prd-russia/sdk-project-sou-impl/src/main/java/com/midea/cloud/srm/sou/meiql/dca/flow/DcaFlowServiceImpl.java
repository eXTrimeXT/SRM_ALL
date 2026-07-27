package com.midea.cloud.srm.sou.meiql.dca.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
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
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaOrderDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSupplierDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitEventService;
import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class DcaFlowServiceImpl implements IFlowBusinessCallbackService {
    @Autowired
    private QlService qlService;

    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Autowired
    public BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private PjSouClient pjSouClient;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Value("${bpm.DBFQSQ.processGroupId}")
    private String processGroupId;
    @Value("${bpm.DBFQSQ.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Resource
    private BaseExtClient baseExtClient;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PjProjectExtClient pjProjectExtClient;
    private static final String BUSINESS_TYPE = "SOU_DCA";

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(org.apache.commons.lang3.StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(CaDTO::getCaId,businessId);
                r.put(CaDTO::getStatus, CaStatusEnum.APPROVING.getCode());
                r.put(CaDTO::getStartBpmDiscardUsername, loginAppUser.getUsername());
                r.put(CaDTO::getStartBpmDiscardNickname, loginAppUser.getNickname());
                qlService.update(TypeEnum.Dca.getCode(), Arrays.asList(r));
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            Record r = new Record();
            r.put(CaDTO::getCaId,businessId);
            r.put(CaDTO::getStatus, CaStatusEnum.APPROVING.getCode());
            qlService.update(TypeEnum.Dca.getCode(), Arrays.asList(r));
        }


    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        //1.修改当前单据状态
        CaDTO ca = qlService.readByKey(TypeEnum.Dca.getCode(),businessId,CaDTO.class);
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode());
        qlService.update(TypeEnum.Dca.getCode(), Arrays.asList(r));
        //2.修改原单据状态为已废弃
        r = new Record();
        r.put(CaDTO::getCaId,ca.getOriginalCaId());
        r.put(CaDTO::getDiscardDescription,ca.getAbandonDesc());
        r.put(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
        //3.废弃审批通过后，更新招标项目的状态：定标中为商务已开标
        if (null != ca.getProjectId() && SouTypeEnum.bid.name().equals(ca.getSouType())) {
            //修改招标单据状态
            extSouInitEventService.updateSouBidStatus(ca.getProjectId(),SouBiddingProStatusEnum.BUS_BID_OPEN);
        }
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.REJECTED.getCode());
        qlService.update(TypeEnum.Dca.getCode(), Arrays.asList(r));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.WITHDRAW.getCode());
        qlService.update(TypeEnum.Dca.getCode(), Arrays.asList(r));
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        Record r = new Record();
        r.put(CaDTO::getCaId,businessId);
        r.put(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        qlService.update(TypeEnum.Dca.getCode(), Arrays.asList(r));
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
        CaDTO ca = qlService.readByKey(TypeEnum.Dca.getCode(),businessId,CaDTO.class);
        JSONObject processVars = new JSONObject();
        processVars.put("otherUrlParam","originalCaId="+ca.getOriginalCaId());
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+ca.getSouName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        /**
         * 查询 定标废弃申请 -   businessid  == CaId
         * 定标申请DTO  CaDTO
         * 供应商投标DTO  CaOrderDTO
         * 供应商选定结果DTO  CaSelectionResultDTO
         * 供应商总体情况    CaSupplierDTO
         */
        log.info("===================进入定标废弃申请装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        // 定标申请DTO
        CaDTO ca = qlService.readByKey(TypeEnum.Dca.getCode(),businessId,CaDTO.class);

        // 供应商总体情况
        List<CaSupplierDTO> caSupplierDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSupplier.getCode())
                .eq(CaSupplierDTO::getCaId, businessId), CaSupplierDTO.class);
        // 供应商选定结果
        List<CaSelectionResultDTO> caSelectionResultDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode())
                .eq(CaSelectionResultDTO::getCaId, businessId), CaSelectionResultDTO.class);
        // 附件信息
        Fileupload fileupload = new Fileupload();
        fileupload.setBusinessId(businessId);
        fileupload.setPageNum(100);
        fileupload.setPageSize(1);
        PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
        List<com.midea.cloud.srm.model.file.upload.entity.Fileupload> fileList = fileuploads.getList();


        // 原附件信息
        Fileupload fileupload1 = new Fileupload();
        log.info("========="+ca.getOriginalCaId());
        fileupload1.setBusinessId(ca.getOriginalCaId());
        fileupload1.setPageNum(100);
        fileupload1.setPageSize(1);
        PageInfo<Fileupload> fileuploads1 = fileCenterClient.listPage(fileupload,"N");
        List<com.midea.cloud.srm.model.file.upload.entity.Fileupload> fileList1 = fileuploads1.getList();

        // 组装主表信息
        Map<String, Object> mainTableData = getStringObjectMap(businessId, ca);


        //* @param funName 这个对应功能的名字，前端一般用路由的name值来识别
        //* @param formId 这个是业务单据ID
        //* @param formNo 这个传的是单据标题或其他自定义标题

        List<Object> itemdata = new ArrayList<>();

        // 投标供应商
        List<CaOrderDTO> caOrders = ca.getCaOrders();
        if(null != caOrders){
            caOrders.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                // 供应商名称
                map.put("GYSMC",e.getVendorName());
                // 供应商属性
                map.put("GYSSX",e.getExtVendorAttr());
                // 投标包名
                map.put("TBBM",e.getTenderPackageName());
                // 投标状态
                map.put("TBZT",getOrderStatus(e.getOrderStatus()));
                // 不参与时间【应该是不参与原因】
                map.put("BCYSJ",e.getWithdrawReason());
                // 废标说明
                map.put("FBSM",e.getRejectReason());
                map.put("__TABLE", "BO_EU_TBGYS");
                itemdata.add(map);
            });
        }


        // 供应商总体情况
        if(null != caSupplierDtoList){
            caSupplierDtoList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                map.put("GYSMC",e.getVendorName());  // 供应商名称
                map.put("TBHSZJIA",e.getBidTotalPrice()); // 供应商含税总价（万元）
                map.put("JSDFEN",e.getTechScore()); // 技术得分
                map.put("JGDFEN",e.getPriceScore()); // 价格得分
                map.put("ZHDFEN",e.getCompositeScore()); // 综合得分
                map.put("ZHPDING",e.getComprehensiveEvaluation()); // 综合评定
                map.put("__TABLE", "BO_EU_GYSZTQK1");
                itemdata.add(map);
            });
        }


        // 供应商选定结果
        if(null != caSelectionResultDtoList){
            caSelectionResultDtoList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                map.put("GYSBM",e.getVendorCode());  // 供应商编码
                map.put("GYSMC",e.getVendorName()); // 供应商名称
                map.put("SFZBIAO",(e.getIsWin() == "Y") ? "是" : "否"); // 是否中标
                map.put("JZBFWEI",e.getWinRange()); // 中标范围
                map.put("ZLBYYIN",e.getWinReason()); // 中/落标原因
                map.put("__TABLE", "BO_EU_GYSXDJG1");
                itemdata.add(map);
            });
        }


        // 原定标申请附件
        for (Fileupload fileUpload : fileList1) {

            Map<String, Object> map = new HashMap<>(50);
            List<Map<String, Object>> file = new ArrayList<>();
            Map<String, Object> fileMap = new HashMap<>(50);

            fileMap.put("FILE_PATH_BYMOBILE", "");
            fileMap.put("FILE_NAME", fileUpload.getFileSourceName());
            String mes = "fileSourceName="+fileUpload.getFileSourceName()+"&fileuploadId="+fileUpload.getFileuploadId();
            fileMap.put("FILE_PATH", fileDownloadPath+mes);
            file.add(fileMap);

            map.put("FJMC",file);
            map.put("BZ",fileUpload.getComment());
            map.put("__TABLE", "BO_EU_YDBSQFJ");
            itemdata.add(map);
        }


        // 附件
        for (Fileupload fileUpload : fileList) {

            Map<String, Object> map = new HashMap<>(50);
            List<Map<String, Object>> file = new ArrayList<>();
            Map<String, Object> fileMap = new HashMap<>(50);

            fileMap.put("FILE_PATH_BYMOBILE", "");
            fileMap.put("FILE_NAME", fileUpload.getFileSourceName());
            String mes = "fileSourceName="+fileUpload.getFileSourceName()+"&fileuploadId="+fileUpload.getFileuploadId();
            fileMap.put("FILE_PATH", fileDownloadPath+mes);
            file.add(fileMap);

            map.put("FJMCHENG",file);
            map.put("BZHU",fileUpload.getComment());
            map.put("__TABLE", "BO_EU_FJXX1");
            itemdata.add(map);
        }

        Map<String,Object> itemFile = new HashMap<>(50);
        //原定标申请附件
        List<String> fList = new ArrayList<>();
        fList.add("FJMC");
        itemFile.put("BO_EU_YDBSQFJ", fList);

        //附件信息
        List<String> fList1 = new ArrayList<>();
        fList1.add("FJMCHENG");
        itemFile.put("BO_EU_FJXX1", fList1);

        String processtitle = "定标废弃申请";
        String maintable = "BO_EU_DBFQ";

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
        itemtable.add("BO_EU_TBGYS");
        itemtable.add("BO_EU_GYSZTQK1");
        itemtable.add("BO_EU_GYSXDJG1");
        itemtable.add("BO_EU_YDBSQFJ");
        itemtable.add("BO_EU_FJXX1");

        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processGroupId, appId,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入定标废弃申请组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========定标废弃申请JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }

    /**
     * 组装数据
     * @param businessId 参数
     * @param ca 参数
     * @return 返回
     */
    @NotNull
    private Map<String, Object> getStringObjectMap(Long businessId, CaDTO ca) {
        Map<String, Object> mainTableData = new HashMap<>(50);
        // 定标废弃申请单
        mainTableData.put("DBZFSQD", ca.getCaNo());
        // 板块
        mainTableData.put("BK", ca.getExtOrgBuName());
        //公司
        mainTableData.put("GS", ca.getExtOrgOuName());
        //需求部门
        mainTableData.put("XQBM", ca.getDemandDepartmentName());
        //单据状态
        mainTableData.put("DJZT",getStatus(ca.getStatus()));
        //创建人
        mainTableData.put("CJR", ca.getCreatedFullName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(ca.getCreationDate() != null){
            //创建日期
            mainTableData.put("CJSJ",simpleDateFormat.format(ca.getCreationDate()));
        }

        if(ca.getLastUpdateDate() != null){
            //最后更新时间
            mainTableData.put("ZHGXSJ",simpleDateFormat.format(ca.getLastUpdateDate()));
        }
//需求人
        mainTableData.put("XQR", ca.getDemandUserNickname());
        //合同经办人
        mainTableData.put("HTJBR", ca.getContractOperatorNickname());
        //定标申请单
        mainTableData.put("DBSQD", ca.getOriginalCaNo());
        //招标项目编号
        mainTableData.put("ZBXMBH", ca.getExtProjectNo());
        //项目名称
        mainTableData.put("XMMX", ca.getSouName());
        //质保期
        mainTableData.put("ZBQ", ca.getWarrantyPeriod());
        //预算（万元）
        mainTableData.put("YS", ca.getExtBudget());

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        if(ca.getPublishTime() != null){
            //发标时间
            mainTableData.put("FBSJ",simpleDateFormat.format(ca.getPublishTime()));
        }
        if(ca.getBusEndTime() != null){
            //收标时间
            mainTableData.put("SBSJ",simpleDateFormat.format(ca.getBusEndTime()));
        }
        if(ca.getPriceOpeningTime() != null){
            //开价格标时间
            mainTableData.put("KJGBSJ",simpleDateFormat.format(ca.getPriceOpeningTime()));
        }
        //工期/交货期要求
        mainTableData.put("GQJHQYQ", ca.getTimeLimit());
        //评分规则
        mainTableData.put("PFGZ", ca.getExtScoreRule());
        //项目概括与招标范围
        mainTableData.put("XMGKYZBFW", ca.getProjectOverviewAndBidScope());
        //付款要求
        mainTableData.put("FKYQ", ca.getPaymentRequirements());
        //备注
        mainTableData.put("BZ", ca.getRemark());
        //废弃定标说明
        mainTableData.put("FQDBSM", ca.getAbandonDesc());
//评标结果
        mainTableData.put("PBJG","");
        //技术评分结果
        mainTableData.put("JSPFJG","");
        //技术评分结果链接
        mainTableData.put("SJPFJGLJ",getViewSrmRollBackUrl("bidTechScoreDetail", businessId,"定标废弃申请"));
        return mainTableData;
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


    public String getViewSrmRollBackUrl( String funName,Long formId,String formNo){
        String url =  pjSouClient.getViewSrmRollBackUrl(funName,formId,formNo);
        return url;
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


    public String getOrderStatus(String type){
        String draft = "DRAFT";
        String submission = "SUBMISSION";
        String withdraw = "WITHDRAW";
        String cancel = "CANCEL";
        if(draft.equals(type)){
            return "未投标" ;
        }else if(submission.equals(type)){
            return "已投标" ;
        }else if(withdraw.equals(type)){
            return "已撤回" ;
        }else if(cancel.equals(type)){
            return "作废" ;
        }else {
            return type ;
        }
    }

}
