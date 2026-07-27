package com.midea.cloud.srm.cm.contract.service.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log;
import com.google.gson.JsonObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.enums.contract.ContractType;
import com.midea.cloud.common.enums.contract.OperationTypeProcessEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.cm.contract.service.IContractFileService;
import com.midea.cloud.srm.cm.contract.service.IContractHeadService;
import com.midea.cloud.srm.cm.contract.service.IOperationLogService;
import com.midea.cloud.srm.cm.contract.utils.ContractBpmCreator;
import com.midea.cloud.srm.feign.ContractBaseExtClient;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.ContractSouExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;
import com.midea.cloud.srm.model.contract.enums.ContractSourceTypeEnums;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorResultList;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorRootDTO;
import com.midea.cloud.srm.model.pj.sup.company.entity.SupplierInfo;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorResultList;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorRootDTO;
import com.midea.cloud.srm.model.pj.sup.company.entity.SupplierInfo;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.enums.DictCodeEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName.*;

/**
 * @author 100014336 ganyh19
 */
@Slf4j
@Service("ContractPjFlowServiceImpl")
public class ContractPjFlowServiceImpl extends ContractFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.LCHTSP.processGroupId}")
    private String lcHtSpProcessGroupId;

    @Value("${bpm.LCHTSP.appId}")
    private String lcHtSpAppId;

    @Value("${bpm.HTZZ.processGroupId}")
    private String lcHtZzProcessGroupId;

    @Value("${bpm.HTZZ.processGroupId2}")
    private String lcHtZzProcessGroupId2;

    @Value("${bpm.HTZZ.appId}")
    private String lcHtZzAppId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private IContractFileService contractFileService;

    @Autowired
    private ContractSouExtClient contractSouClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ContractBaseExtClient baseExtClient;

    @Autowired
    private ContractPjExtClient pjExtClient;

    @Autowired
    private RbacClient rbacClient;

    private static final String BUSINESS_TYPE = "CONTRACT";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private QlOpenClient qlOpenClient;



    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                super.submitFlow(businessId,param);
                operationLogService.addByType(ContractStatus.UNDER_REVIEW.name(), businessId);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(ContractHeadExt::getContractHeadId,businessId);
                r.put(ContractHeadExt::getStartBpmUsername, loginAppUser.getUsername());
                r.put(ContractHeadExt::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update(ContractMqlSchemaType.ContractHead.getType(), Arrays.asList(r));
            }
        }else{
            super.submitFlow(businessId,param);
            operationLogService.addByType(ContractStatus.UNDER_REVIEW.name(), businessId);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        super.passFlow(businessId,param);
        ContractHeadExt contractHead = new ContractHeadExt();
        Record record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(), businessId,Record.class);
        contractHead.setContractHeadId(record.getLong("contractHeadId"));
        contractHead.setExtContentFinal(record.getString("extContentFinal"));
        String modelEnable = "modelEnable";
        if(Enable.Y.name().equals(record.getString(modelEnable))){
            contractFileService.makeHtmlFormalPdf(contractHead);
        }
        operationLogService.addByType(ContractStatus.APPROVAL.name(), businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        super.rejectFlow(businessId,param);
        operationLogService.addByType(ContractStatus.REJECTED.name(), businessId);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        super.withdrawFlow(businessId,param);
        operationLogService.addByType(ContractStatus.WITHDRAW.name(), businessId);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        super.destoryFlow(businessId,param);
        handleDataAfterTempProcureDataAfterAbandon(businessId);
        operationLogService.addByType(ContractStatus.ABANDONED.name(),businessId);

    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return super.getVariableFlow(businessId,param);
    }
    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        Record record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(),businessId,Record.class);

        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        String processTitle = "合同审批";
        if(ContractType.TERMINATION.name().equals(record.get(ContractHead::getContractType))){
            processTitle = "合同终止";
        }
        JSONObject processVars = new JSONObject();

        Long companyId = record.getLong("vendorId");
        //供应商信息
        Record record1 = qlOpenClient.read(ContextPath.SUP, "CompanyInfo", companyId);
        //社会编码
        String lcCode = record1.getString("lcCode");
        //贸易伙伴
        String tradePartner = "";
        try {
            List<String> taxCodesList = new ArrayList<>();
            taxCodesList.add(lcCode);
            //根据社会编码获取mdm供应商信息
            PurveyorRootDTO purveyorRootDTO = pjProjectExtClient.searchListByTaxCodes(taxCodesList, "CHN");
            List<PurveyorResultList> list = purveyorRootDTO.getResult();
            if (!list.isEmpty()) {
                List<SupplierInfo> comList = list.get(0).getSupplierInfoList();
                tradePartner = comList.get(0).getTradePartner();
            }
        } catch (Exception e) {

            Log.error("获取MDM供应商信息失败:" , e);
            throw new BaseException("获取MDM供应商信息失败:" + e.getMessage());
        }
        //G为关联单位，N为内部单位，空为外部单位
        String partner = "";
        String relPartner = "G";
        String internalPartner = "N";
        if(StringUtils.equals(relPartner, record1.getString("partner"))) {
            partner = relPartner;
        } else if(StringUtils.equals(internalPartner, record1.getString("partner"))) {
            partner = internalPartner;
        }
        processVars.put("GYSLX", partner);

        //印章管理者
        String sealId = record.getString("sealId");
        if(StringUtils.isNotBlank(sealId)) {
            String sealEmployeers = pjProjectExtClient.sealDetailEmployees(Long.valueOf(sealId));
            processVars.put("YZGLY", sealEmployeers);
        }

        List<DictItemDTO> dictItemDTOList = baseClient.listByDictCode(Arrays.asList(DictCodeEnum.CONTRACT_SOURCE_TYPE.getCode(), DictCodeEnum.CONTRACT_FORM2.getCode()));
        Map<String, List<DictItemDTO>> dictGroup = dictItemDTOList.stream().collect(Collectors.groupingBy(DictItemDTO::getDictCode));
        Map<String, String> contractSourceTypeMap = dictGroup.getOrDefault(DictCodeEnum.CONTRACT_SOURCE_TYPE.getCode(), new ArrayList<>())
                .stream().collect(Collectors.toMap(k -> k.getDictItemCode(), v -> v.getDictItemName(), (k1, k2) -> k2));
        Map<String, String> contractFormMap = dictGroup.getOrDefault(DictCodeEnum.CONTRACT_FORM2.getCode(), new ArrayList<>())
                .stream().collect(Collectors.toMap(k -> k.getDictItemCode(), v -> v.getDictItemName(), (k1, k2) -> k2));

        String sourceType  = record.getString("sourceType");
        String formal  = record.getString("formal");
        processVars.put("LYLX", contractSourceTypeMap.get(sourceType));
        processVars.put("QSFS", contractFormMap.get(formal));

        Long extInviteHeadId = record.getLong("extInviteHeadId");
        if(ObjectUtil.isNotNull(extInviteHeadId)) {
            User user = rbacClient.getUserByIdAnon(extInviteHeadId);
            if(ObjectUtil.isNotNull(user)) {
                processVars.put("ZBFZR", contractFormMap.get(formal));
            }
        }

        processVars.put("MYHB", "".equals(tradePartner) ? YesOrNo.NO.getValue() : YesOrNo.YES.getValue());

        //获取收支方向 OUT-我方付款, IN-我方收款
        String extIncome = record.getString("extIncome");
        processVars.put("SZFX",extIncome);
        //币种编码
        String currencyCode = record.getString("currencyCode");
        processVars.put("BB",currencyCode);

        bpmParam.setProcessTitle(processTitle+"-"+record.getString(CONTRACT_CODE_FIELD)+AppUserUtil.getLoginAppUser().getNickname());
        bpmParam.setProcessGroupId(lcHtZzProcessGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }

        Record record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(),businessId,Record.class);
        if(ObjectUtil.isNotEmpty(record)){
            log.info("getDataPushFlow: {}, {}", businessId, param);
            ContractBpmCreator contractBPMCreator;
            if(ContractType.TERMINATION.name().equals(record.get(ContractHead::getContractType))){
                contractBPMCreator = new ContractBpmCreator(baseClient,qlService,fileDownloadPath,lcHtZzAppId,lcHtZzProcessGroupId,pjExtClient);
                JSONObject jsonObject = contractBPMCreator.createContractTerminateBPM(record);
                log.info("===================合同终止{}",jsonObject);
                return JsonUtil.entityToJsonStr(jsonObject);
            } else {
                contractBPMCreator = new ContractBpmCreator(baseClient,qlService,fileDownloadPath,lcHtSpAppId,lcHtSpProcessGroupId,pjExtClient);
                JSONObject jsonObject = contractBPMCreator.createContractTempBPM(record);
                log.info("===================进入合同审批组装数据结束{}",jsonObject);
                return JsonUtil.entityToJsonStr(jsonObject);
            }
        }
        return super.getDataPushFlow(businessId,param);

    }

    private void handleDataAfterTempProcureDataAfterAbandon(Long contractHeadId){
        if(contractHeadId!=null){
            Record record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(),contractHeadId,Record.class);
            if(ObjectUtil.isNotEmpty(record)){
                List<Record> contractMaterials = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractMaterial.getType()).eq(CONTRACT_HEAD_ID_FIELD,contractHeadId),Record.class);
                if(CollUtil.isNotEmpty(contractMaterials)) {
                    if (ContractSourceTypeEnums.TEMP_PROCURE.getCode().equals(record.getString(SOURCE_TYPE_FIELD))) {
                        List<ExtFixPriceContractSignDTO> extFixPriceContractSignDTOS = new ArrayList<>();
                        for (Record m : contractMaterials) {
                            ExtFixPriceContractSignDTO extFixPriceContractSignDTO = new ExtFixPriceContractSignDTO();
                            extFixPriceContractSignDTO.setFixPriceLineId(m.getLong(EXT_FIX_PRICE_LINE_ID_FIELD));
                            extFixPriceContractSignDTO.setHasSignedContract(Enable.N);
                            extFixPriceContractSignDTOS.add(extFixPriceContractSignDTO);
                        }
                        contractSouClient.updateTempPurchaseContractSigns(extFixPriceContractSignDTOS);

                    } else if (ContractSourceTypeEnums.CENT_PURCHASE.getCode().equals(record.getString(SOURCE_TYPE_FIELD))) {
                        List<ExtPurFixPriceContractSignDTO> extPurFixPriceContractSignDTOS = new ArrayList<>();
                        for (Record m : contractMaterials){
                            extPurFixPriceContractSignDTOS.add(new ExtPurFixPriceContractSignDTO(m.getLong(EXT_FIX_PRICE_LINE_ID_FIELD),Enable.N));
                        }
                        contractSouClient.updateCentPurchaseContractSigns(extPurFixPriceContractSignDTOS);
                    }
                }
            }
        }
    }






}
