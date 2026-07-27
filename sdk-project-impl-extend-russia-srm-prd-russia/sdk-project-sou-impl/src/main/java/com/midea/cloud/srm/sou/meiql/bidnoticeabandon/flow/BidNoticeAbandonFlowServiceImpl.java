package com.midea.cloud.srm.sou.meiql.bidnoticeabandon.flow;

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
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeAbandonTypeEnum;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitEventService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class BidNoticeAbandonFlowServiceImpl implements IFlowBusinessCallbackService {
    @Autowired
    private QlService qlService;

    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Autowired
    public BaseClient baseClient;
    @Autowired
    public BaseExtClient baseExtClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private ExtSouInviteService extSouInviteService;

    @Autowired
    private IExtSouProjectService projectService;

    @Value("${bpm.ZLBFQSQ.processGroupId}")
    private String processGroupId;
    @Value("${bpm.ZLBFQSQ.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Resource
    private PjProjectExtClient pjProjectExtClient;
    private static final String BUSINESS_TYPE = "SOU_ATN";
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void submitFlow(Long businessId, String param) {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(BidNoticeDTO::getBidNoticeId,businessId);
                r.put(BidNoticeDTO::getStatus, CaStatusEnum.APPROVING.getCode());
                r.put(BidNoticeDTO::getStartBpmDiscardUsername, loginAppUser.getUsername());
                r.put(BidNoticeDTO::getStartBpmDiscardNickname, loginAppUser.getNickname());
                qlService.update(TypeEnum.BidNoticeAbandon.getCode(), Arrays.asList(r));
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            Record r = new Record();
            r.put(BidNoticeDTO::getBidNoticeId,businessId);
            r.put(BidNoticeDTO::getStatus, CaStatusEnum.APPROVING.getCode());
            qlService.update(TypeEnum.BidNoticeAbandon.getCode(), Arrays.asList(r));
        }
    }

    @Override
    public void passFlow(Long businessId, String param) {
        //1.修改当前单据状态
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNoticeAbandon.getCode(),businessId,BidNoticeDTO.class);
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.APPROVED.getCode());
        r.put(BidNoticeDTO::getPassTime, new Date());
        qlService.update(TypeEnum.BidNoticeAbandon.getCode(), Arrays.asList(r));
        //2.修改原单据状态为已废弃
        r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,bidNoticeDTO.getOriginalBidNoticeId());
        r.put(BidNoticeDTO::getDiscardReason,bidNoticeDTO.getAbandonReason());
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        qlService.update(TypeEnum.BidNotice.getCode(), Arrays.asList(r));

        /*3.
         * 申请类型为：变更合同签署单位时、变更中标通知书时、其他时，审批通过后，更新中/落标通知书状态为已废弃，同时更新招标单状态为：待中/落标通知，可重新发起中/落标通知。
         * 申请类型为：变更中标供应商，重新定价申请。审批通过后，变更待归档、已归档为商务已开标。更新定标审批单为已废弃。
         */
        if (BidNoticeAbandonTypeEnum.CHANGE_VENDOR.getCode().equals(bidNoticeDTO.getDiscardType())) {
            //3.1 更新定标审批单为已废弃
            r = new Record();
            r.put(CaDTO::getCaId,bidNoticeDTO.getCaId());
            r.put(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode());
            qlService.update(TypeEnum.Ca.getCode(), Arrays.asList(r));
            //3.2 变更待归档、已归档为商务已开标
            if (null != bidNoticeDTO.getProjectId() && SouTypeEnum.bid.name().equals(bidNoticeDTO.getSouType())) {
                extSouInitEventService.updateSouBidStatus(bidNoticeDTO.getProjectId(),SouBiddingProStatusEnum.BUS_BID_OPEN);
            }
        } else if (BidNoticeAbandonTypeEnum.CHANGE_CONTRACT.getCode().equals(bidNoticeDTO.getDiscardType())
                || BidNoticeAbandonTypeEnum.CHANGE_NOTICE.getCode().equals(bidNoticeDTO.getDiscardType())
                || BidNoticeAbandonTypeEnum.OTHERS.getCode().equals(bidNoticeDTO.getDiscardType())) {
            //3.1 更新招标单状态为：待中/落标通知，可重新发起中/落标通知
            if (null != bidNoticeDTO.getProjectId() && SouTypeEnum.bid.name().equals(bidNoticeDTO.getSouType())) {
                extSouInitEventService.updateSouBidStatus(bidNoticeDTO.getProjectId(),SouBiddingProStatusEnum.WIN_LOSS_NOTICE);
            }
        }

        ExtSouProject extSouProject = projectService.getById(bidNoticeDTO.getProjectId());
        if(!Objects.isNull(extSouProject) && SouTypeEnum.bid.name().equals(extSouProject.getSouType())) {
            List<Record> details = qlService.queryByWrapper(QlWrappers
                    .query(TypeEnum.BidNoticeDetail.getCode())
                    .select(BidNoticeDetailDTO::getBidNoticeDetailId,
                            BidNoticeDetailDTO::getNoticeAttachmentId,
                            BidNoticeDetailDTO::getNoticeAttachmentName,
                            BidNoticeDetailDTO::getVendorId,
                            BidNoticeDetailDTO::getIsWin)
                    .eq(BidNoticeDetailDTO::getBidNoticeId,bidNoticeDTO.getOriginalBidNoticeId())
                    .eq(BidNoticeDetailDTO::getIsWin, YesOrNo.YES.getValue()),Record.class);

            /** 更新邀请供应商中标情况 */
            updateInviteVendorSccussBid(extSouProject, details);
        }

    }

    private void updateInviteVendorSccussBid(ExtSouProject extSouProject, List<Record> details) {
        if(CollectionUtils.isEmpty(details)) {
            return;
        }
        List<SouInviteItem> souInviteItems = new ArrayList<>(details.size());
        details.stream().forEach(detail -> {
            SouInviteItem souInviteItem = new SouInviteItem();
            souInviteItem.setVendorId(detail.get(BidNoticeDetailDTO::getVendorId));
            souInviteItem.setIsSuccBid(YesOrNo.NO.getValue());
            souInviteItems.add(souInviteItem);
        });
        extSouInviteService.updateIsSuccBidBatch(extSouProject, souInviteItems);
    }

    @Override
    public void rejectFlow(Long businessId, String param) {
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.REJECTED.getCode());
        qlService.update(TypeEnum.BidNoticeAbandon.getCode(), Arrays.asList(r));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) {
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.WITHDRAW.getCode());
        qlService.update(TypeEnum.BidNoticeAbandon.getCode(), Arrays.asList(r));
    }

    @Override
    public void destoryFlow(Long businessId, String param) {
        Record r = new Record();
        r.put(BidNoticeDTO::getBidNoticeId,businessId);
        r.put(BidNoticeDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        qlService.update(TypeEnum.BidNoticeAbandon.getCode(), Arrays.asList(r));
    }

    @Override
    public String getVariableFlow(Long businessId, String param) {
        return null;
    }


    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNoticeAbandon.getCode(),businessId,BidNoticeDTO.class);
        JSONObject processVars = new JSONObject();
        processVars.put("otherUrlParam","originalBidNoticeId="+bidNoticeDTO.getOriginalBidNoticeId());
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+bidNoticeDTO.getSouName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }
    @Override
    public String getDataPushFlow(Long businessId, String param) {
        log.info("===================进入中落标废弃申请装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        BidNoticeDTO bidNoticeDTO = qlService.readByKey(TypeEnum.BidNoticeAbandon.getCode(),businessId,BidNoticeDTO.class);
        List<BidNoticeDetailDTO> bidNoticeDetailDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode())
                .eq(BidNoticeDetailDTO::getBidNoticeId, businessId), BidNoticeDetailDTO.class);

        List<BidNoticeInternalDTO> bidNoticeInternalDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeInternal.getCode())
                .eq(BidNoticeInternalDTO::getBidNoticeId, businessId), BidNoticeInternalDTO.class);

        SceneFile sceneFileParam = (new SceneFile()).setBusinessId(businessId).setSceneModuleCode("SCENE_SOU_ATN_ATTACHMENT");
        List<SceneFile> sceneFileList = baseClient.listSceneFile(sceneFileParam);

        // 主表信息
        Map<String, Object> mainTableData = new HashMap<>(32);
        // 板块
        mainTableData.put("BK",bidNoticeDTO.getExtOrgBuName() );
        // 公司
        mainTableData.put("GS",bidNoticeDTO.getExtOrgOuName() );
        // 需求部门
        mainTableData.put("XQBM",bidNoticeDTO.getDemandDepartmentName());
        // 需求人
        mainTableData.put("XQR", bidNoticeDTO.getDemandUserNickname() );
        //招标技术负责人
        mainTableData.put("ZBJSFZR",bidNoticeDTO.getExtTechPrincipal());
        //联系电话
        mainTableData.put("LXDH",bidNoticeDTO.getExtTechPhone());
        //单据状态
        mainTableData.put("DJZT",getStatus(bidNoticeDTO.getStatus()));
        //  招标单号
        mainTableData.put("ZBDH",bidNoticeDTO.getSouNo());
        // 项目名称
        mainTableData.put("XMMC",bidNoticeDTO.getSouName());
        //招标项目编号
        mainTableData.put("ZBXMBH",bidNoticeDTO.getExtProjectNo());
        // 创建人
        mainTableData.put("CJR",bidNoticeDTO.getCreatedFullName() );

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(bidNoticeDTO.getCreationDate() != null){
            // 创建日期
            mainTableData.put("CJRQ",simpleDateFormat.format(bidNoticeDTO.getCreationDate()));
        }
        if(bidNoticeDTO.getLastUpdateDate() != null){
            // 最后更新时间
            mainTableData.put("ZHGXSJ",simpleDateFormat.format(bidNoticeDTO.getLastUpdateDate()));
        }
        //	申请类型
        mainTableData.put("SQLX",getDiscardType(bidNoticeDTO.getDiscardType()));
//	备注
        mainTableData.put("BZ",bidNoticeDTO.getRemark());
        //废弃原因
        mainTableData.put("FQYY",bidNoticeDTO.getDiscardReason() );

        // 中/落标通知明细表
        List<Object> itemdata = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bidNoticeDetailDtoList)) {
            bidNoticeDetailDtoList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(32);
                // 供应商编码
                map.put("GYSBM",e.getVendorCode());
                // 供应商名称
                map.put("GYSMC",e.getVendorName());
                // 是否中标
                map.put("SFZB",StringUtils.equals(e.getIsWin(),"Y") ? "是" : "否");
                // 中标金额（元）
                map.put("ZBJE",e.getWinAmount());
                // 合同签署单位
                map.put("HTQSDW",e.getContractSignUnit());
                // 合同周期
                map.put("HTZQ",e.getContractPeriod());
                //是否履约评价
                map.put("SFLYPJ",StringUtils.equals(e.getIsPerformanceEvaluated(), "Y") ? "是" : "否");
                //不履约评价的原因
                map.put("BLYPJDYY",e.getNonPerformanceReason());
                //是否现场考察
                map.put("SFXCKC",StringUtils.equals(e.getIsOnSiteInspected() , "Y") ? "是" : "否");
                //考察详情  这个字段没有
                map.put("KCXQ",null);
                //通知书附件
                map.put("TZSFJ",dealFileList(e.getNoticeAttachmentId()));
                //是否发送
                map.put("SFFS",StringUtils.equals(e.getIsSend() , "Y") ? "是" : "否");

                map.put("__TABLE", "BO_EU_ZLBTXXZB");
                itemdata.add(map);
            });
        }


        //内部通知书表
        if (CollectionUtils.isNotEmpty(bidNoticeInternalDtoList)) {
            bidNoticeInternalDtoList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(32);
                map.put("BK",e.getExtOrgBuName());  // 板块
                map.put("GS",e.getExtOrgOuName()); // 公司
                map.put("XQBM",e.getDemandDepartmentName()); // 需求部门
                map.put("TZSFJ",dealFileList(e.getAttachmentId())); // 通知书附件
                map.put("TZSMXFJ",null); // 通知书明细附件
                map.put("__TABLE", "BO_EU_NBTZSZB");
                itemdata.add(map);
            });
        }

        JSONObject dataPushFlowJsn = getJsonObject(sceneFileList, mainTableData, itemdata);
        log.info("===================进入中落标废弃申请组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========中落标废弃申请JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }

    /**
     * 组装
     * @param sceneFileList 参数
     * @param mainTableData 参数
     * @param itemdata 参数
     * @return 返回
     */
    @NotNull
    private JSONObject getJsonObject(List<SceneFile> sceneFileList, Map<String, Object> mainTableData, List<Object> itemdata) {
        // 附件
        if (CollectionUtils.isNotEmpty(sceneFileList)) {
            sceneFileList.forEach(e -> {
                if(e.getFileuploadId() != null){
                    Map<String, Object> map = new HashMap<>(32);
                    List<Map<String, Object>> file = new ArrayList<>();
                    Fileupload fileupload = new Fileupload();
                    fileupload.setFileuploadId(e.getFileuploadId());
                    fileupload.setPageNum(1);
                    fileupload.setPageSize(1);
                    PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
                    List<Fileupload> fileList = fileuploads.getList();
                    for (Fileupload fileUpload : fileList) {
                        Map<String, Object> fileMap = new HashMap<>(32);
                        fileMap.put("FILE_PATH_BYMOBILE", "");
                        fileMap.put("FILE_NAME", fileUpload.getFileSourceName());
                        String mes = "fileSourceName="+fileUpload.getFileSourceName()+"&fileuploadId="+fileUpload.getFileuploadId();
                        fileMap.put("FILE_PATH", fileDownloadPath+mes);
                        file.add(fileMap);
                    }
                    map.put("FJSC",file);
                    map.put("FJMC",e.getFileName());
                    map.put("__TABLE", "BO_EU_ZLBTZFJZB");
                    itemdata.add(map);
                }
            });
        }


        Map<String,Object> itemFile = new HashMap<>(32);
        // 中落标信息子表
        List<String> fList = new ArrayList<>();
        fList.add("TZSFJ");
        itemFile.put("BO_EU_ZLBTXXZB", fList);

        //内部通知书子表
        List<String> fList2 = new ArrayList<>();
        fList2.add("TZSFJ");
        fList2.add("TZSMXFJ");
        itemFile.put("BO_EU_NBTZSZB", fList2);

        // 中落标通知附件子表
        List<String> fList3 = new ArrayList<>();
        fList3.add("FJMC");
        itemFile.put("BO_EU_ZLBTZFJZB", fList3);

        // 附件信息子表
        List<String> fList4 = new ArrayList<>();
        fList4.add("FJMC");
        itemFile.put("BO_EU_ZLBFJXXZB", fList4);


        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_ZLBTXXZB");
        itemtable.add("BO_EU_NBTZSZB");
        itemtable.add("BO_EU_ZLBTZFJZB");
        itemtable.add("BO_EU_ZLBFJXXZB");

        String processtitle = "中落标废弃申请";
        String maintable = "BO_EU_ZLBTZYC";

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
        return dataPushFlowJsn;
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
                Map<String, Object> map = new HashMap<>(32);
                map.put("FILE_PATH_BYMOBILE", "");
                map.put("FILE_NAME", e.getFileSourceName());
                String mes = "fileSourceName="+e.getFileSourceName()+"&fileuploadId="+e.getFileuploadId();
                map.put("FILE_PATH", fileDownloadPath+mes);
                fileList.add(map);
            });
        }

        return fileList;
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

    public  String getDiscardType(String type){
        String changeContract = "CHANGE_CONTRACT";
        String changeNotice = "CHANGE_NOTICE";
        String changeVendor = "CHANGE_VENDOR";
        String others = "OTHERS";
        if(changeContract.equals(type)){
            return "变更合同签署单位";
        }else if(changeNotice.equals(type)){
            return "变更中标通知书";
        }else if(changeVendor.equals(type)){
            return "变更中标供应商，重新定标";
        }else if(others.equals(type)){
            return "其他";
        }else{
            return type;
        }
    }

}
