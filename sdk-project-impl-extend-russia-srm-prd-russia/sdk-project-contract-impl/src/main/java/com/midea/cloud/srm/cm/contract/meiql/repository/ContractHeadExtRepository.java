package com.midea.cloud.srm.cm.contract.meiql.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.enums.contract.ContractType;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.result.RepoData;
import com.midea.cloud.meiql.api.spec.result.RepoRecMap;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.cm.contract.mapper.ExtPartnerMapper;
import com.midea.cloud.srm.cm.contract.service.IContractExtService;
import com.midea.cloud.srm.cm.contract.service.IContractFileService;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.ContractSouExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.constant.ContractMaterialFieldName;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;
import com.midea.cloud.srm.model.contract.dto.ContractOperationLog;
import com.midea.cloud.srm.model.contract.enums.ContractHeadPlanStatusEnums;
import com.midea.cloud.srm.model.contract.enums.ContractSourceTypeEnums;
import com.midea.cloud.srm.model.contract.dto.ContractOperationLog;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementInfo;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.model.sou.enums.JcAgreementTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceContractSignDTO;
import com.midea.cloud.srm.model.supplier.info.dto.InfoDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName.*;

/**
 * @author 100014336 ganyh19
 */
@Slf4j
@Component
public class ContractHeadExtRepository extends ProxyRepository {


    public static final String CONTRACT_MATERIALS = "contractMaterials";
    @Autowired
    private QlService qlService;


    @Autowired
    private ContractSouExtClient contractSouClient;

    @Autowired
    private IContractExtService contractExtService;

    @Autowired
    private ContractPjExtClient contractPjExtClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private ExtPartnerMapper extPartnerMapper;

    @Override
    public QlResult execute(QlQueryAction queryAction) throws Exception {
        return super.execute(queryAction);
    }


    private static final String LOCK_KEY = "ContractHeadSave";


    @Override
    public QlResult save(QlQueryAction queryAction) {
        List<Record> records = getPayloadListForType(queryAction, Record.class);

        if (CollUtil.isNotEmpty(records)) {
            Set<String> keys = new HashSet<>();
            synchronized (LOCK_KEY) {
                List<Record> newRecords = new ArrayList<>();
                for (Record record : records) {
                    String sourceType = record.getString(SOURCE_TYPE_FIELD);
                    if(ContractSourceTypeEnums.BID_NOTICE.name().equals(sourceType)){
                        String sourceNumber = record.get(ContractHeadExt::getSourceNumber);
                        Long vendorId = record.get(ContractHeadExt::getVendorId);
                        Long buId = record.get(ContractHeadExt::getBuId);
                        if (ObjectUtil.isAllNotEmpty(vendorId, sourceNumber, buId)) {
                            String makeKey = sourceNumber + "_" + vendorId + "_" + buId;
                            if (keys.contains(makeKey)) {
                                //重复的条过
                                log.info(MessageFormat.format("批量数据重复: sourceNumber:{0} ,vendorId:{1}, buId:{2}", sourceNumber, vendorId, buId));
                                continue;
                            } else {
                                keys.add(makeKey);
                            }
                        }
                        //去重后的数据
                        newRecords.add(record);
                        if (!checkData(record)) {
                            return QlResult.empty();
                        }
                    }
                }
                queryAction.setPayload(newRecords);
            }
        }

        QlResult result=super.doSave(queryAction,records);
        RepoData data=result.getRef();
        RepoRecMap map=data.get("ContractPartner");
        if(MapUtils.isNotEmpty(map)) {
            for(Map.Entry<Serializable, Record>v:map.entrySet()){
                String s=v.getKey().toString();
                extPartnerMapper.updateUnStampState(s);

            }
        }

        return result;
    }

    @SneakyThrows(Exception.class)
    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeCreate(queryAction, records);
        handleDataBeforeCreate(records);
    }

    @Override
    protected void afterCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.afterCreate(queryAction, records);
        handleDataAfterCreate(records);
    }

    @Override
    protected void afterUpdate(QlQueryAction queryAction, Collection<Record> records) {
        super.afterUpdate(queryAction, records);
        handleDataAfterUpdate(queryAction,records);
    }



    @Override
    public QlResult delete(QlQueryAction queryAction) {
        QlResult result;
        List<Record> tempRecords = new ArrayList<>();
        List<Long> contractHeadIds = getPayloadListForType(queryAction,Long.class);
        if(CollUtil.isNotEmpty(contractHeadIds)){
            for (Long contractHeadId:contractHeadIds){
                Record tempRecord = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(),contractHeadId,Record.class);
                if(ObjectUtil.isNotNull(tempRecord)){
                    if (ContractSourceTypeEnums.TEMP_PROCURE.getCode().equals(tempRecord.getString(SOURCE_TYPE_FIELD))){
                        List<Record> contractMaterials = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractMaterial.getType()).eq(CONTRACT_HEAD_ID_FIELD,contractHeadId),Record.class);
                        tempRecord.put(CONTRACT_MATERIALS,contractMaterials);
                    }
                    tempRecords.add(tempRecord);
                }
            }
        }

        result = super.delete(queryAction);
        for(Record record:tempRecords){
            handleLcPriceAfterDelete(record);
        }
        return result;
    }


    @Override
    public QlResult query(QlQueryAction queryAction) {
        QlResult qlResult = super.query(queryAction);
        List contractHeadIds = qlResult.getRecords();
        //添加sourceType 和 planType
        if(CollUtil.isNotEmpty(contractHeadIds)){
            String contractHeadIdField = CONTRACT_HEAD_ID_FIELD;
            QlQueryWrapper perPlanWrapper = QlWrappers.query(ContractMqlSchemaType.PerPlan.getType()).select("status",contractHeadIdField).in(contractHeadIdField,contractHeadIds);
            QlQueryWrapper contractHeadWrapper = QlWrappers.query(ContractMqlSchemaType.ContractHead.getType()).select("sourceType",contractHeadIdField,"extInviteHeadId").in(contractHeadIdField,contractHeadIds);
            List<PerPlan> perPlans = qlService.queryByWrapper(perPlanWrapper, PerPlan.class);
            List<ContractHeadExt> contractHeads = qlService.queryByWrapper(contractHeadWrapper,ContractHeadExt.class);
            Map<Long,String> planStatus = new HashMap<>(16);
            Map<Long,String> contractSourceType = new HashMap<>(16);
            if(CollUtil.isNotEmpty(perPlans)){
                perPlans.forEach(perPlan -> planStatus.putIfAbsent(perPlan.getContractHeadId(),perPlan.getStatus()));

            }
            RepoRecMap data  = qlResult.getRef().get(ContractMqlSchemaType.ContractHead.getType());
            if(CollUtil.isNotEmpty(contractHeads)){
                contractHeads.forEach(contractHead -> contractSourceType.putIfAbsent(contractHead.getContractHeadId(),contractHead.getSourceType()));
                setInviteInfo(contractHeads,data);
            }

            Enumeration<Serializable> keys = data.keys();
            while (keys.hasMoreElements()){
                Long contractHeadId = (Long)keys.nextElement();
                String sourceType = contractSourceType.get(contractHeadId);
                String planStatusStr = planStatus.get(contractHeadId);
                Record record  = data.get(contractHeadId);
                record.put(ContractHead::getSourceType,sourceType);
                record.put("sourceTypeDesc",getSourceTypeDesc(sourceType));
                String headPlanStatus = getHeadPlanStatus(planStatusStr);
                record.put("planStatus",headPlanStatus);
                record.put("planStatusDesc",getHeadPlanTypeDesc(headPlanStatus));
            }
        }
        return qlResult;
    }



    private String getSourceTypeDesc(String sourceType){
        String desc = "";
        List<ContractSourceTypeEnums>  contractSourceTypeEnums = Arrays.stream(ContractSourceTypeEnums.values()).filter(e->e.getCode().equals(sourceType)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(contractSourceTypeEnums)){
            desc = contractSourceTypeEnums.get(0).getDesc();
        }
        return desc;
    }

    /**
     * 转换成合同头的履约状态
     * @param planStatus 履约计划状态
     * @return 返回合同头的履约状态
     */
    private String getHeadPlanStatus(String planStatus){
        //除了履约中和履约完成其他状态均为未开始
        if(!ContractHeadPlanStatusEnums.IN_PERFORMANCE.getCode().equals(planStatus)
                &&!ContractHeadPlanStatusEnums.COMPLETE_PERFORMANCE.getCode().equals(planStatus)){
            return ContractHeadPlanStatusEnums.NEVER_START.getCode();
        }
        return planStatus;
    }

    /**
     *  根据合同头履约状态获取履约描述信息
     * @param planType
     * @return
     */
    private String getHeadPlanTypeDesc(String planType){
        String desc = "";
        List<ContractHeadPlanStatusEnums>  contractHeadPlanStatusEnums = Arrays.stream(ContractHeadPlanStatusEnums.values()).filter(e->e.getCode().equals(planType)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(contractHeadPlanStatusEnums)){
            desc = contractHeadPlanStatusEnums.get(0).getDesc();
        }
        return desc;
    }

    private  void handleDataAfterUpdate(QlQueryAction queryAction, Collection<Record> records){
        if(CollUtil.isNotEmpty(records)){
            for (Record record : records) {
                String operationType = record.getString(CONTRACT_STATUS_FIELD);
                if(ContractStatus.ARCHIVED.name().equals(operationType)){
                    //创建价格库
                    handleDataAfterArchived(queryAction,record);
                }

                if(logOperation().contains(operationType)){
                    addOperationLog(queryAction,record,operationType);
                }
            }
        }

    }



    /**
     *
     * @param queryAction
     * @param record
     */
    private void handleDataAfterArchived(QlQueryAction queryAction, Record record) {
        record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(),record.getLong(CONTRACT_HEAD_ID_FIELD),Record.class);
        String sourceType = record.getString(SOURCE_TYPE_FIELD);
        String poolFlag = record.getString(EXT_PRICE_POOL_FLAG_FIELD);

        if(Enable.Y.name().equalsIgnoreCase(poolFlag)&&poolSourceTypes().contains(sourceType)){
            //创建价格库
            contractSouClient.saveOrUpdateJcAgreement(createJcAgreement(record));
        }
    }

    private  void  addOperationLog(QlQueryAction queryAction, Record record,String operationType){
        if(ObjectUtil.isNotEmpty(operationType)){
            ContractOperationLog contractOperationLog = new ContractOperationLog();
            Long contractHeadId = record.getLong(CONTRACT_HEAD_ID_FIELD);
            contractOperationLog.setContractHeadId(contractHeadId);
            contractOperationLog.setOperationType(operationType);
            //根据类型获取描述
            handleLogByType(record,operationType,contractOperationLog);
            qlService.create(OPERATION_LOG_MEIQL_TYPE, Collections.singletonList(contractOperationLog));
        }

    }

    private void handleLogByType(Record record,String operationType,ContractOperationLog contractOperationLog) {
        if(ContractStatus.SUPPLIER_REJECTED.name().equals(operationType)){
            contractOperationLog.setOperationDesc(record.getString(VENDOR_REJECT_REASON_FIELD));
        }
        if(ContractStatus.TERMINATED.name().equals(operationType)){
            contractOperationLog.setOperationDesc(record.getString(CONTRACT_TERMINATION_REASON_FIELD));
        }
    }


    private void handleDataAfterCreate(Collection<Record> records){
        if(CollUtil.isNotEmpty(records)) {
            for (Record record : records) {
                Long contractHeadId = record.getLong(CONTRACT_HEAD_ID_FIELD);
                if(contractHeadId!=null) {
//                    record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(), contractHeadId, Record.class);
                    if (ObjectUtil.isNotEmpty(record)) {
                        if(ContractSourceTypeEnums.TEMP_PROCURE.getCode().equals(record.getString(SOURCE_TYPE_FIELD))
                        ||ContractSourceTypeEnums.CENT_PURCHASE.getCode().equals(record.getString(SOURCE_TYPE_FIELD))){
                            handleLcPriceAfterCreate(record);
                        }
                    }
                }

            }
        }
    }



    private void handleDataAfterDelete(Collection<Record> records){
        if(CollUtil.isNotEmpty(records)) {
            for (Record record : records) {
                Long contractHeadId = record.getLong(CONTRACT_HEAD_ID_FIELD);
                if(contractHeadId!=null) {
//                    record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(), contractHeadId, Record.class);
                    if (ObjectUtil.isNotEmpty(record)) {
                        if (ContractSourceTypeEnums.TEMP_PROCURE.getCode().equals(record.getString(SOURCE_TYPE_FIELD))
                        ||ContractSourceTypeEnums.CENT_PURCHASE.getCode().equals(record.getString(SOURCE_TYPE_FIELD))) {
                            handleLcPriceAfterDelete(record);
                        }
                    }
                }

            }

        }
    }


    private void handleDataBeforeCreate(Collection<Record> records) throws Exception {
        if(CollUtil.isNotEmpty(records)){
            for (Record record:records){
                //处理补充协议
                handleSupplementalAgreement(record);
                //处理 中标通知单创建
                handleBidNoticeCreate(record);
            }
        }
    }

    /**
     * 插入数据检查
     * @param record
     */
    private boolean checkData(Record record) {
        String sourceNumber = record.get(ContractHeadExt::getSourceNumber);
        Long vendorId = record.get(ContractHeadExt::getVendorId);
        Long buId = record.get(ContractHeadExt::getBuId);
        //只有没有headId的时候裁判重复
        Long headId = record.get(ContractHeadExt::getContractHeadId);
        boolean pass = true;
        log.info(MessageFormat.format("保存数据: sourceNumber:{0} ,vendorId:{1}, buId:{2}",sourceNumber,vendorId,buId));
        if(ObjectUtil.isEmpty(headId)){
            if(ObjectUtil.isAllNotEmpty(sourceNumber,vendorId,buId)){
                 long headCount =  qlService.countByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractHead.getType())
                        .eq(ContractHeadExt::getSourceNumber,sourceNumber)
                        .eq(ContractHeadExt::getBuId,buId)
                        .eq(ContractHeadExt::getVendorId,vendorId));
                pass = !(headCount>0);
            }
        }

        return pass;
    }

    private void handleBidNoticeCreate(Record record) {
        String sourceType = record.getString(SOURCE_TYPE_FIELD);
        //
        if(ContractSourceTypeEnums.BID_NOTICE.name().equals(sourceType)){
            Long vendorId = record.getLong(VENDOR_ID_FIELD);
            InfoDTO info  = null;
            if(ObjectUtil.isNotNull(vendorId)){
                info = contractExtService.getVendorInfo(vendorId);
            }

            //补充乙方信息
            contractExtService.fillPartners(record,null,info);
            List<Record> partners = record.getSubRecords("contractPartners");
            //补充甲方信息
            if(CollUtil.isEmpty(partners)){
                partners = new ArrayList<>();
            }
            record.put("contractPartners",partners);
            contractExtService.fillIncorporatedPartner(partners,record);
            record.put(ContractHeadExt::getExtPricePoolFlag,Enable.N.toString());
            record.put(ContractHeadExt::getNeedVendorConfirm,Enable.Y.toString());
        }
    }

    private void handleSupplementalAgreement(Record record) {
        String contractType = record.getString(CONTRACT_TYPE_FIELD);
        if(ContractType.SUPPLEMENTAL_AGREEMENT.name().equals(contractType)){
            try {
                String contractNewNo  = contractExtService.getGenerateExtCode(record.getLong(CEEA_CONTRACT_OLD_ID_FIELD));
                record.put(ContractHead::getContractNo,contractNewNo);
                record.put(ContractHead::getContractCode,contractNewNo);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new BaseException("生成序列号失败");
            }
        }
    }

    private List<String> logOperation(){
        List<String> types = new ArrayList<>();
        types.add(ContractStatus.ARCHIVED.name());
        types.add(ContractStatus.SIGNATUREING.name());
        types.add(ContractStatus.PUBLISHED.name());
        types.add(ContractStatus.REFUSED.name());
        types.add(ContractStatus.CLOSE.name());
        types.add(ContractStatus.SUPPLIER_CONFIRMED.name());
        types.add(ContractStatus.TERMINATED.name());
        types.add(ContractStatus.UN_ARCHIVED.name());
        types.add(ContractStatus.SUPPLIER_REJECTED.name());
        types.add(ContractStatus.ABANDONED.name());
        types.add(ContractStatus.UNPUBLISHED.name());
        types.add(ContractStatus.APPROVAL.name());
        return types;
    }

    private List<String> poolSourceTypes(){
        List<String> sourceTypes = new ArrayList<>();
        sourceTypes.add(ContractSourceTypeEnums.TEMP_PROCURE.getCode());
        sourceTypes.add(ContractSourceTypeEnums.CENT_PURCHASE.getCode());
        return sourceTypes;
    }






    /**
     * 处理临采定价单删除
     */
    private void handleLcPriceAfterDelete(Record record){
        changeContractSign(record,Enable.N);
    }

    /**
     * 处理临采定价单创建
     */
    private void handleLcPriceAfterCreate(Record record){
        changeContractSign(record,Enable.Y);
    }

    private void changeContractSign(Record record,Enable enable){
        Long contractHeadId = record.getLong(CONTRACT_HEAD_ID_FIELD);
        String sourceType = record.getString(SOURCE_TYPE_FIELD);
        List<Record> contractMaterials = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractMaterial.getType()).eq(CONTRACT_HEAD_ID_FIELD,contractHeadId),Record.class);
        if(CollUtil.isEmpty(contractMaterials)){
            contractMaterials = record.getSubRecords(CONTRACT_MATERIALS);
        }
        if(CollUtil.isNotEmpty(contractMaterials)){
            if (ContractSourceTypeEnums.TEMP_PROCURE.getCode().equals(sourceType)) {
                List<ExtFixPriceContractSignDTO> extFixPriceContractSignDTOS = new ArrayList<>();
                for (Record m : contractMaterials) {
                    ExtFixPriceContractSignDTO extFixPriceContractSignDTO = new ExtFixPriceContractSignDTO();
                    extFixPriceContractSignDTO.setFixPriceLineId(m.getLong(EXT_FIX_PRICE_LINE_ID_FIELD));
                    extFixPriceContractSignDTO.setHasSignedContract(enable);
                    extFixPriceContractSignDTOS.add(extFixPriceContractSignDTO);
                }
                contractSouClient.updateTempPurchaseContractSigns(extFixPriceContractSignDTOS);
            } else if (ContractSourceTypeEnums.CENT_PURCHASE.getCode().equals(sourceType)) {
                List<ExtPurFixPriceContractSignDTO> extPurFixPriceContractSignDTOS = new ArrayList<>();
                for (Record m : contractMaterials){
                    extPurFixPriceContractSignDTOS.add(new ExtPurFixPriceContractSignDTO(m.getLong(EXT_FIX_PRICE_LINE_ID_FIELD),enable));
                }
                contractSouClient.updateCentPurchaseContractSigns(extPurFixPriceContractSignDTOS);
            }
        }

    }




    private SccSouJcAgreement createJcAgreement(Record record){
        Long contractHeadId =record.getLong(CONTRACT_HEAD_ID_FIELD);
        String sourceType = record.getString(SOURCE_TYPE_FIELD);
/*      record = qlService.readByKey(ContractMqlSchemaType.ContractHead.getType(),contractHeadId,Record.class); */
        SccSouJcAgreement sccSouJcAgreement = new SccSouJcAgreement();
        //获取协议类型
        sccSouJcAgreement.setAgreementType(getJcAgreementType(sourceType));
        sccSouJcAgreement.setAgreementCode(record.getString(CONTRACT_CODE_FIELD));
        sccSouJcAgreement.setAgreementName(record.getString(CONTRACT_CODE_FIELD));
        sccSouJcAgreement.setCompanyCode(record.getString(COMPANY_CODE_FIELD));
        sccSouJcAgreement.setCompanyId(record.getLong(COMPANY_ID_FIELD));
        sccSouJcAgreement.setCompanyName(record.getString(COMPANY_NAME_FIELD));
        sccSouJcAgreement.setSupCode(record.getString(VENDOR_CODE_FIELD));
        sccSouJcAgreement.setSupId(record.getLong(VENDOR_ID_FIELD));
        sccSouJcAgreement.setSupName(record.getString(VENDOR_NAME_FIELD));
        sccSouJcAgreement.setCurrencyType(record.getString(CURRENCY_CODE_FIELD));
        sccSouJcAgreement.setAgreementStatus(AgreementStatusEnums.DRAFT.getCode());
        List<SccSouJcAgreementInfo> sccSouJcAgreementInfos = new ArrayList<>();
        List<Record> mts = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractMaterial.getType()).eq(CONTRACT_HEAD_ID_FIELD,contractHeadId),Record.class);
        if(CollUtil.isNotEmpty(mts)){
            for (Record mt:mts){
                SccSouJcAgreementInfo sccSouJcAgreementInfo = new SccSouJcAgreementInfo();
                sccSouJcAgreementInfo.setMaterialCode(mt.getString(ContractMaterialFieldName.MATERIAL_CODE));
                sccSouJcAgreementInfo.setMaterialName(mt.getString(ContractMaterialFieldName.MATERIAL_NAME));
                sccSouJcAgreementInfo.setMaterialId(mt.getLong(ContractMaterialFieldName.MATERIAL_ID));
                sccSouJcAgreementInfo.setStandards(mt.getString(ContractMaterialFieldName.SPECIFICATION));
                sccSouJcAgreementInfo.setSellByDate(mt.getInteger(ContractMaterialFieldName.SHELF_LIFE));
                sccSouJcAgreementInfo.setGoodsTypeCode(mt.getString(ContractMaterialFieldName.CATEGORY_CODE));
                sccSouJcAgreementInfo.setGoodsTypeName(mt.getString(ContractMaterialFieldName.CATEGORY_NAME));
                sccSouJcAgreementInfo.setGoodsTypeId(mt.getLong(ContractMaterialFieldName.CATEGORY_ID));
                //未税价格
                sccSouJcAgreementInfo.setPriceTax(mt.getBigDecimal(ContractMaterialFieldName.UNTAXED_PRICE));
                //税率
                sccSouJcAgreementInfo.setTaxRate(mt.getBigDecimal(ContractMaterialFieldName.TAX_RATE));
                //含税价格
                sccSouJcAgreementInfo.setRatePrice(mt.getBigDecimal(ContractMaterialFieldName.TAXED_PRICE));
                sccSouJcAgreementInfo.setStartNum(mt.getBigDecimal(ContractMaterialFieldName.CONTRACT_QUANTITY).intValue());
                sccSouJcAgreementInfo.setUnit(mt.getString(ContractMaterialFieldName.UNIT_CODE));
                sccSouJcAgreementInfos.add(sccSouJcAgreementInfo);
            }
        }
        sccSouJcAgreement.setSccSouJcAgreementInfoList(sccSouJcAgreementInfos);
        return sccSouJcAgreement;
    }

    private String getJcAgreementType(String sourceType) {

        if(ContractSourceTypeEnums.TEMP_PROCURE.name().equals(sourceType)){
            return JcAgreementTypeEnum.CONTRACT.getCode();
        }else if(ContractSourceTypeEnums.CENT_PURCHASE.name().equals(sourceType)){
            return JcAgreementTypeEnum.CENT_PURCHASE.getCode();
        }else {
            String msg = "来源类型不正确";
            throw new BaseException(msg);
        }
    }


    public void setInviteInfo(List<ContractHeadExt> contractHeadExts,RepoRecMap data){

        String extInviteHeadIdName = "extInviteHeadId";
        List<Long> extInviteHeadIds = contractHeadExts.stream().map(ContractHeadExt::getExtInviteHeadId).collect(Collectors.toList());
        //添加招标人信息
        if(CollUtil.isNotEmpty(extInviteHeadIds)){
            List<User> users = rbacClient.getByUserIds(extInviteHeadIds);
            if(CollUtil.isNotEmpty(users)){
                Map<Long,List<User>> userMap = users.stream().collect(Collectors.groupingBy(User::getUserId));
                for(Record recordItem :data.values()){
                    List<User> users1 = userMap.get(recordItem.getLong(extInviteHeadIdName));
                    if(CollUtil.isNotEmpty(users1)){
                        User user = users1.get(0);
                        recordItem.put(ContractHeadExt::getExtInviteHeadName,user.getNickname());
                        recordItem.put(ContractHeadExt::getExtInviteHeadAccount,user.getUsername());
                    }

                }
            }
        }

    }




}
