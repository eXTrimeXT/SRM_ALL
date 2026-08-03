package com.midea.cloud.srm.biz.pj.supplier.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.review.FormType;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.workflow.WorkflowThirdService;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.feign.pj.pj.PjBpmClient;
import com.midea.cloud.srm.feign.pj.supplier.ReviewFormClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.supplier.rev.dto.*;
import com.midea.cloud.srm.model.pj.supplier.rev.entity.*;
import com.midea.cloud.srm.model.workflow.dto.FlowCallbackDTO;
import com.midea.cloud.srm.model.workflow.dto.FlowResponseDTO;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 * 供应商资质审查
 */

@Slf4j
@Service
public class QualificationReviewServiceImpl implements IFlowBusinessCallbackService, WorkflowThirdService {

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    private ReviewFormClient reviewFormClient;
    @Autowired
    private BaseExtClient baseExtClient;

    @Autowired
    PjBpmClient pjBpmClient;

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        //需自定义开发
        log.info("---------getDataPushFlow-----------");
        log.info("businessId:" + businessId);
        log.info("param:" + param);
        //供应商资质审查
        ReviewForm reviewForm = reviewFormClient.getReviewFormInfo(businessId);
        BpmReviewFormDto bpmReviewFormDto = new BpmReviewFormDto();
        bpmReviewFormDto.setZzsclx(reviewForm.getQuaReviewType());
        bpmReviewFormDto.setGysmc(reviewForm.getVendorName());
        bpmReviewFormDto.setZzscdh(reviewForm.getReviewFormNumber());
        bpmReviewFormDto.setShzt(reviewForm.getApproveStatus());
        bpmReviewFormDto.setCjr(reviewForm.getCreatedBy());
        bpmReviewFormDto.setBm(reviewForm.getCeeaDeptName());
        bpmReviewFormDto.setCjsj(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(reviewForm.getCreationDate()));
        bpmReviewFormDto.setSfzb(reviewForm.getIfSiteForm());
        bpmReviewFormDto.setDjsm(reviewForm.getReviewExplain());

        Long formId = reviewForm.getVendorId();
        String formType = FormType.REVIEW_FORM.name();
        Long vendorId = reviewForm.getVendorId();
        //银行信息
        QlOpenQueryWrapper bankJournalWrapper = QlOpenWrappers.query("BankJournal");
        bankJournalWrapper.eq("formId", formId);
        bankJournalWrapper.eq("formType", formType);
        bankJournalWrapper.eq("vendorId", vendorId);
        List<BankJournal> backJournalInfoList = qlOpenClient.query(ContextPath.SUP, bankJournalWrapper, BankJournal.class);
        List<BpmBankJournalDto> bpmBankJournalDtoList = new ArrayList<>();
        backJournalInfoList.forEach(e -> {
            BpmBankJournalDto bpmBankJournalDto = new BpmBankJournalDto();
            bpmBankJournalDto.setYhdm(e.getBankCode());
            bpmBankJournalDto.setYhmc(e.getBankName());
            bpmBankJournalDto.setKhhmc(e.getOpeningBank());
            bpmBankJournalDto.setFhbm(e.getUnionCode());
            bpmBankJournalDto.setZhmc(e.getBankAccountName());
            bpmBankJournalDto.setYhzh(e.getBankAccount());
            bpmBankJournalDto.setBz(e.getCurrencyCode());
            bpmBankJournalDto.setSfzzh(e.getCeeaMainAccount());
            bpmBankJournalDto.setQy(e.getCeeaEnabled());
            bpmBankJournalDtoList.add(bpmBankJournalDto);
        });
        //引入品类和组织
        QlOpenQueryWrapper orgJournalWrapper = QlOpenWrappers.query("OrgJournal");
        orgJournalWrapper.eq("vendorId", vendorId);
        List<OrgJournal> orgJournalList = qlOpenClient.query(ContextPath.SUP, orgJournalWrapper, OrgJournal.class);
        List<BpmOrgJournalDto> bpmOrgJournalDtoList = new ArrayList<>();
        orgJournalList.forEach(e ->  {
            BpmOrgJournalDto bpmOrgJournalDto = new BpmOrgJournalDto();
            bpmOrgJournalDto.setYrgs(String.valueOf(e.getOrgId()));
            bpmOrgJournalDtoList.add(bpmOrgJournalDto);
        });
        //引入品类和组织2
        QlOpenQueryWrapper cateJournalWrapper = QlOpenWrappers.query("CateJournal");
        cateJournalWrapper.eq("vendorId", vendorId);
        List<CateJournal> cateJournalList = qlOpenClient.query(ContextPath.SUP, cateJournalWrapper, CateJournal.class);
        List<BpmCateJournalDto> bpmCateJournalDtoList = new ArrayList<>();
        cateJournalList.forEach(e -> {
            BpmCateJournalDto bpmCateJournalDto = new BpmCateJournalDto();
            bpmCateJournalDto.setYrpl(String.valueOf(e.getCategoryId()));
            bpmCateJournalDto.setPlbnd(String.valueOf(e.getThisYearAmount()));
            bpmCateJournalDtoList.add(bpmCateJournalDto);
        });
        //资质审查原因
        QlOpenQueryWrapper reviewFormExpWrapper = QlOpenWrappers.query("ReviewFormExp");
        reviewFormExpWrapper.eq("vendorId", vendorId);
        List<ReviewFormExp> reviewFormExpList = qlOpenClient.query(ContextPath.SUP, reviewFormExpWrapper, ReviewFormExp.class);
        List<BpmReviewFormExpDto> bpmReviewFormExpDtoList = new ArrayList<>();
        reviewFormExpList.forEach(e -> {
            BpmReviewFormExpDto bpmReviewFormExpDto = new BpmReviewFormExpDto();
            bpmReviewFormExpDto.setYy(e.getReviewReason());
            bpmReviewFormExpDto.setYyms(e.getReasonExplain());
            bpmReviewFormExpDtoList.add(bpmReviewFormExpDto);
        });
        //附件
        QlOpenQueryWrapper fileRecordWrapper = QlOpenWrappers.query("FileRecord");
        fileRecordWrapper.eq("vendorId", vendorId);
        List<FileRecord> fileRecordList = qlOpenClient.query(ContextPath.SUP, fileRecordWrapper, FileRecord.class);
        String processTitle = "供应商资质审查";
        String mainTable = "BO_EU_GYSZZSC";
        BpmReviewFormDto mainTableData = bpmReviewFormDto;
        String processGroupId = "";
        String appId = "";
        String createOrgId = "";
        String createUser = "";
        List<String> itemTable = null;
        List<Object> itemData = null;
        Object itemFile = "";
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainTableData, processGroupId, appId, createOrgId, createUser, itemTable, itemData, itemFile);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    @Override
    public FlowCallbackDTO getDataPushFlow(FlowCallbackDTO flowCallbackDTO) throws Exception {
        log.info("-------------getDataPushFlow-----------------");
        log.info("flowCallbackDTO:"+JSONObject.toJSONString(flowCallbackDTO));
        IFlowBusinessCallbackService iFlowBusinessCallbackService = null;

        Class clazz = Class.forName(flowCallbackDTO.getServiceBean());
        Object bean = SpringContextHolder.getApplicationContext().getBean(clazz);
        iFlowBusinessCallbackService = (IFlowBusinessCallbackService) bean;

        String flowData = iFlowBusinessCallbackService.getDataPushFlow(flowCallbackDTO.getBusinessId(), flowCallbackDTO.getParam());
        flowCallbackDTO.setFlowParam(flowData);
        return flowCallbackDTO;
    }

    @Override
    public FlowResponseDTO startThird(FlowCallbackDTO flowCallbackDTO) throws Exception {
        log.info("--------------startThird----------------");
        log.info("flowCallbackDTO:"+ JSONObject.toJSONString(flowCallbackDTO));

        // 查询流程配置是否启用，没启用的直接返回一个InstanceId
        String businessType = flowCallbackDTO.getBusinessType();
        Map<String, Object> mapParam =new HashMap<>(16);
        mapParam.put("businessType",businessType);
        Boolean isOpen = baseExtClient.getIsEnableFlow(JSONObject.toJSONString(mapParam));

        BpmResultDTO<BpmCreateResult> resultDTO ;
        FlowResponseDTO flowResponseDTO = new FlowResponseDTO();
        if(isOpen){
            DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",flowCallbackDTO.getBusinessType());
            if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())){
                BpmStartProcessParam bpmParam = JSONObject.parseObject(flowCallbackDTO.getFlowParam(),BpmStartProcessParam.class);
                bpmParam = bpmParam==null?new BpmStartProcessParam():bpmParam;
                resultDTO = pjBpmClient.startProcessByCategoty(flowCallbackDTO.getBusinessId(),flowCallbackDTO.getBusinessType(),bpmParam);
            }else{
                resultDTO = pjBpmClient.createProcessByCategory(JSONObject.parseObject(flowCallbackDTO.getFlowParam())
                        ,flowCallbackDTO.getBusinessId().toString(),flowCallbackDTO.getBusinessType());
            }
            log.info("返回的信息===" + JSONObject.toJSONString(resultDTO));
            flowResponseDTO.setInstanceId(resultDTO.getData()==null?null:resultDTO.getData().getProcessInstId());
        }else{
            flowResponseDTO.setInstanceId(flowCallbackDTO.getBusinessId().toString());
        }
        flowResponseDTO.setBusinessId(flowCallbackDTO.getBusinessId());
        flowResponseDTO.setFlowParam(flowCallbackDTO.getFlowParam());
        flowResponseDTO.setParam(null);
        flowResponseDTO.setDealStatus(null);
        return flowResponseDTO;
    }



    @Override
    public void submitFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

}
