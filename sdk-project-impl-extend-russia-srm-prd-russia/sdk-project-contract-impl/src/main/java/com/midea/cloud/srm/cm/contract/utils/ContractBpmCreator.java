package com.midea.cloud.srm.cm.contract.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.cm.annex.Annex;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.cm.contract.entity.ContractPartner;
import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptance;
import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptanceAtt;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.constant.ContractBpmDefine;
import com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName;
import com.midea.cloud.srm.model.contract.dto.ContractMaterialExt;
import com.midea.cloud.srm.model.contract.dto.PayPlanExt;
import com.midea.cloud.srm.model.contract.entity.BasisAnnex;
import com.midea.cloud.srm.model.contract.enums.ContractAcceptanceHeadBPM;
import com.midea.cloud.srm.model.contract.enums.ContractAcceptanceLineLcbmxBPM;
import com.midea.cloud.srm.model.contract.enums.ContractAcceptanceLineXgfjmxBPM;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * @author 100014336 ganyh16
 */
public class ContractBpmCreator {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private BaseClient baseClient;

    private String downloadBasePath;

    private String appId;

    private String groupId;

    private ContractPjExtClient pjExtClient;

    private QlService qlService;

    private Map<String,List<DictItemDTO>> dictItemMap = new HashMap<>();



    public ContractBpmCreator(BaseClient baseClient, QlService qlService, String downloadBasePath, String appId, String groupId, ContractPjExtClient pjExtClient) {
        this.baseClient = baseClient;
        this.downloadBasePath = downloadBasePath;
        this.appId = appId;
        this.groupId = groupId;
        this.pjExtClient = pjExtClient;
        this.qlService = qlService;
    }

    public JSONObject createContractTempBPM(Record tempRecord){
        Map<String, Object> mainTableData =  new HashMap<>(16);
        setBaseContractInfo(mainTableData,tempRecord);
        Long contractHeadId = tempRecord.get(ContractHead::getContractHeadId);
        String processTitle = "合同审批-"+tempRecord.get(ContractHead::getContractName);
        //附件信息
        List<Record> annexes = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.Annex.getType()).eq(Annex::getContractHeadId,contractHeadId),Record.class);
        //支付计划信息
        List<Record> contractMaterials = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractMaterial.getType()).eq(ContractMaterialExt::getContractHeadId,contractHeadId),Record.class);
        //合作伙伴
        List<Record> contractPartners = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractPartner.getType()).eq(ContractPartner::getContractHeadId,contractHeadId),Record.class);
        //依据
        List<Record> basisAnnexes = qlService.queryByWrapper(QlWrappers.query("ContractBasisAnnex").eq(BasisAnnex::getContractHeadId,contractHeadId),Record.class);;

        List<Record> playPlans = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.PayPlan.getType()).eq(PayPlanExt::getContractHeadId,contractHeadId),Record.class);
        List<String> itemTables = new ArrayList<>();
        List<Object> itemData = new ArrayList<>();
        //添加子表
        addAllSubTables(itemTables);
        /*
        添加子表数据
         */
        setContractMaterials(itemData,contractMaterials);
        setPayPlans(itemData,playPlans);
        setAnnexes(itemData,annexes);
        setBasisAnnexes(itemData,basisAnnexes);
        setPartners(itemData,contractPartners);
        setFinancialInfos(itemData,tempRecord);

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = getOrgId(createUser);

        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put(ContractBpmDefine.LINE_BO_EU_LCHTYJ,BpmResult.getFileField(ContractBpmDefine.LINE_FIELD_YJ_FJMC));
        itemFile.put(ContractBpmDefine.LINE_BO_EU_LCHTFJXX,BpmResult.getFileField(ContractBpmDefine.LINE_FIELD_FJ_FJMX));

        return BpmResult.generateBpmJson(processTitle, ContractBpmDefine.HEAD_BO_EU_LCHTSH,mainTableData,groupId,appId,createOrgId,createUser,itemTables,itemData,itemFile);
    }

    public JSONObject createContractTerminateBPM(Record tempRecord){
        Map<String, Object> mainTableData =  new HashMap<>(16);
        String processTitle = "合同终止-"+tempRecord.get(ContractHead::getContractName);
        Long contractHeadId =  tempRecord.get(ContractHead::getContractHeadId);
        Map<String,Object> itemFile = new HashMap<>(16);
        List<String> itemTables = new ArrayList<>();
        List<Object> itemData = new ArrayList<>();
        setBaseContractInfo(mainTableData,tempRecord);

        mainTableData.put("ZZYY",tempRecord.get(ContractHead::getContractTerminationReason));
        mainTableData.put("SFKJXY",Enable.Y.name().equals(tempRecord.get(ContractHead::getIsFrameworkAgreement))?"是":"否");
        mainTableData.put("WFQY",tempRecord.get(ContractHead::getBuName));
        mainTableData.put("SFXYGYS",Enable.Y.name().equals(tempRecord.get(ContractHead::getNeedVendorConfirm))?"是":"否");
        removeNotNeedContractInfo(mainTableData);
        //附件信息
        List<Record> annexes = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.Annex.getType()).eq(Annex::getContractHeadId,contractHeadId),Record.class);
        setHtzjAnnexes(itemData,annexes);
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = getOrgId(createUser);
        String annexName = "BO_EU_HTFJXX";
        itemTables.add(annexName);
        itemFile.put(annexName,BpmResult.getFileField("FJMC"));
        return BpmResult.generateBpmJson(processTitle,"BO_EU_HTYS",mainTableData,groupId,appId,createOrgId,createUser,itemTables,itemData,itemFile);
    }

    public JSONObject createAcceptanceBPM(Record tempRecord){
        Map<String, Object> mainTableData =  new HashMap<>(16);
        String processTitle = "合同验收单-"+tempRecord.get(PerAcceptance::getPerAcceptanceNo);
        setAcceptanceMain(mainTableData,tempRecord);

        List<String> itemTables = new ArrayList<>();
        List<Object> itemData = new ArrayList<>();
        Record milestone = qlService.readByKey(ContractMqlSchemaType.PerPlanMilestone.getType(),tempRecord.get(PerAcceptance::getPerPlanMilestoneId),Record.class);
        setAcceptanceMilestone(itemData,milestone);
        List<Record> atts = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.PerAcceptanceAtt.getType()).eq(PerAcceptanceAtt::getPerAcceptanceId,tempRecord.get(PerAcceptance::getPerAcceptanceId)),Record.class);
        if(CollUtil.isNotEmpty(atts)){
            for(Record att:atts){
                setAcceptanceAtt(itemData,att);
            }
        }
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = getOrgId(createUser);
        Map<String,Object> itemFile = new HashMap<>(16);
        itemTables.add(ContractAcceptanceLineLcbmxBPM.values()[0].getTableName());
        itemTables.add(ContractAcceptanceLineXgfjmxBPM.values()[0].getTableName());
        itemFile.put(ContractAcceptanceLineXgfjmxBPM.values()[0].getTableName(),BpmResult.getFileField(ContractAcceptanceLineXgfjmxBPM.FJSC.getBpmFieldName()));
        return BpmResult.generateBpmJson(processTitle,"BO_EU_HTZZ",mainTableData,groupId,appId,createOrgId,createUser,itemTables,itemData,itemFile);
    }

    private void setAcceptanceAtt(List<Object> itemData, Record att) {
        Map<String,Object> item = new HashMap<>(16);
        String tableName = "";
        for (ContractAcceptanceLineXgfjmxBPM contractAcceptanceLineXgfjmxBPM:ContractAcceptanceLineXgfjmxBPM.values()){
            item.put(contractAcceptanceLineXgfjmxBPM.getBpmFieldName(),handleAcceptanceAtt(att,contractAcceptanceLineXgfjmxBPM));
            tableName = contractAcceptanceLineXgfjmxBPM.getTableName();
        }
        addItemWithTableName(itemData,item,tableName);
    }

    private Object handleAcceptanceAtt(Record att, ContractAcceptanceLineXgfjmxBPM acceptanceLineXgfjmxBPM) {
        if(acceptanceLineXgfjmxBPM.getIsFile()){
            return BpmResult.getFileList(downloadBasePath,att.get(PerAcceptanceAtt::getFileName),att.get(PerAcceptanceAtt::getFileId));
        }
        if(acceptanceLineXgfjmxBPM.getIsDate()){
            return BpmResult.sdfDate(att.getDate(acceptanceLineXgfjmxBPM.getFieldName()));
        }
        return att.get(acceptanceLineXgfjmxBPM.getFunction());
    }

    private void setAcceptanceMain(Map<String, Object> mainTableData, Record tempRecord) {
        Record perPlan= qlService.readByKey(ContractMqlSchemaType.PerPlan.getType(),tempRecord.get(PerPlan::getPerPlanId),Record.class);
        for (ContractAcceptanceHeadBPM acceptanceHeadBPM:ContractAcceptanceHeadBPM.values()){
           if(acceptanceHeadBPM.getType().equals(PerAcceptance.class)){

               mainTableData.put(acceptanceHeadBPM.getBpmFieldName(),handleAcceptanceValue(tempRecord,acceptanceHeadBPM));
           } else if(acceptanceHeadBPM.getType().equals(PerPlan.class)){
               if(ContractAcceptanceHeadBPM.BZ.equals(acceptanceHeadBPM)){
                   handleBz(perPlan);
               }
               mainTableData.put(acceptanceHeadBPM.getBpmFieldName(),handleAcceptanceValue(perPlan,acceptanceHeadBPM));
           }
        }
    }

    private void handleBz(Record perPlan) {
        perPlan.put(PerPlan::getCurrencyName,BasicDataUtil.newInstance(baseClient).getCurrencyName(perPlan.get(PerPlan::getCurrencyCode)));
    }





    private void setAcceptanceMilestone(List<Object> itemData,Record milestone){
        Map<String,Object> item = new HashMap<>(16);
        String tableName = "";
        for (ContractAcceptanceLineLcbmxBPM acceptanceLineLcbmxBPM:ContractAcceptanceLineLcbmxBPM.values()){
            item.put(acceptanceLineLcbmxBPM.getBpmFieldName(),handleAcceptanceMilestoneValue(milestone,acceptanceLineLcbmxBPM));
            tableName = acceptanceLineLcbmxBPM.getTableName();
        }
        addItemWithTableName(itemData,item,tableName);
    }

    private Object handleAcceptanceMilestoneValue(Record milestone, ContractAcceptanceLineLcbmxBPM acceptanceLineLcbmxBPM) {
        if(acceptanceLineLcbmxBPM.getIsDate()){
            Object date = milestone.get(acceptanceLineLcbmxBPM.getFieldName());
            if(date instanceof LocalDate){
                return BpmResult.formatLocalDate((LocalDate) date);
            } else {
                return BpmResult.formatDate(date);
            }
        }
        if(acceptanceLineLcbmxBPM.getIsDict()){
            return getDictName(acceptanceLineLcbmxBPM.getDictCode(),(String) milestone.get(acceptanceLineLcbmxBPM.getFunction()));
        }
        return milestone.get(acceptanceLineLcbmxBPM.getFunction());
    }

    private Object handleAcceptanceValue(Record tempRecord, ContractAcceptanceHeadBPM acceptanceHeadBPM) {
        if(acceptanceHeadBPM.getIsDate()){
            Object date = tempRecord.get(acceptanceHeadBPM.getFieldName());
            if(date instanceof LocalDate){
                return BpmResult.formatLocalDate((LocalDate) date);
            } else {
                return BpmResult.formatDate(date);
            }

        }
        return tempRecord.get((SFunction)acceptanceHeadBPM.getFunction());
    }

    private void removeNotNeedContractInfo(Map<String, Object> mainTableData) {
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_QYDZ);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_SFQYMB);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_MBMC);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_BZHT);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_CJSJ);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_SFKHXY);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_WFQYZT);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_CJR);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_ZT);
        mainTableData.remove(ContractBpmDefine.HEAD_FIELD_SFXYGYSQR);

    }

    private String getOrgId(String createUser) {
        String createOrgId = "";
        SccPjUser sccPjUser = pjExtClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        return createOrgId;
    }

    private void setFinancialInfos(List<Object> itemData, Record tempRecord) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_CW_BZH,BasicDataUtil.newInstance(baseClient).getCurrencyName(tempRecord.get(ContractHead::getCurrencyCode)));
        item.put(ContractBpmDefine.LINE_FIELD_CW_HTZJE,tempRecord.get(ContractHead::getIncludeTaxAmount));
        addItemWithTableName(itemData,item, ContractBpmDefine.LINE_BO_EU_LCHTCWXX);

    }


    /**
     * 添加合作伙伴信息
     * @param itemData
     * @param contractPartners
     */
    private void setPartners(List<Object> itemData, List<Record> contractPartners) {
        if(ObjectUtil.isNotEmpty(contractPartners)){
            for (Record record:contractPartners){
                setPartner(itemData,record);
            }
        }
    }

    private void setPartner(List<Object> itemData, Record record) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_HB_DZXX, record.get(ContractPartner::getAddress));
        item.put(ContractBpmDefine.LINE_FIELD_HB_HBMC,record.get(ContractPartner::getPartnerName));
        item.put(ContractBpmDefine.LINE_FIELD_HB_KHH, record.get(ContractPartner::getBankName));
        item.put(ContractBpmDefine.LINE_FIELD_HB_LXDH, record.get(ContractPartner::getPhone));
        item.put(ContractBpmDefine.LINE_FIELD_HB_SQDB, record.get(ContractPartner::getContactName));
        item.put(ContractBpmDefine.LINE_FIELD_HB_YHZH, record.get(ContractPartner::getBankAccount));
        item.put(ContractBpmDefine.LINE_FIELD_HB_HBLX, record.get(ContractPartner::getPartnerType));
        addItemWithTableName(itemData,item, ContractBpmDefine.LINE_BO_EU_LCHZHB);
    }

    /**
     * 添加合同签约明细
     * @param itemData
     * @param contractMaterials
     */
    private void setContractMaterials(List<Object> itemData, List<Record> contractMaterials) {
        if(ObjectUtil.isNotEmpty(contractMaterials)){
            for (Record record:contractMaterials){
                setContractMaterial(itemData,record);
            }
        }
    }

    private void setContractMaterial(List<Object> itemData, Record record) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_MX_FPLX,getDictName("EXT_SOU_PURINQ_ORDER_INVOICE_TYPE",record.get(ContractMaterialExt::getExtInvoiceType)));
        item.put(ContractBpmDefine.LINE_FIELD_MX_GGXH,record.get(ContractMaterialExt::getSpecification));
        item.put(ContractBpmDefine.LINE_FIELD_MX_HSDJ,record.get(ContractMaterialExt::getTaxedPrice));
        item.put(ContractBpmDefine.LINE_FIELD_MX_JLDW,record.get(ContractMaterialExt::getUnitName));
        item.put(ContractBpmDefine.LINE_FIELD_MX_SL,record.get(ContractMaterialExt::getContractQuantity));
        item.put(ContractBpmDefine.LINE_FIELD_MX_WLBM,record.get(ContractMaterialExt::getMaterialCode));
        item.put(ContractBpmDefine.LINE_FIELD_MX_WLMC,record.get(ContractMaterialExt::getMaterialName));
        item.put(ContractBpmDefine.LINE_FIELD_MX_WSDJ,record.get(ContractMaterialExt::getUntaxedPrice));
        addItemWithTableName(itemData,item, ContractBpmDefine.LINE_BO_EU_LCHTQYMX);
    }

    /**
     * 添加合同依据数据
     * @param itemData
     * @param basisAnnexes
     */
    private void setBasisAnnexes(List<Object> itemData, List<Record> basisAnnexes) {
        if(ObjectUtil.isNotEmpty(basisAnnexes)){
            for (Record record:basisAnnexes){
                setBasisAnnex(itemData,record);
            }
        }
    }

    private void setBasisAnnex(List<Object> itemData, Record record) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_YJ_BZ,record.get(BasisAnnex::getRemark));
        item.put(ContractBpmDefine.LINE_FIELD_YJ_FJMC,BpmResult.getFileList(downloadBasePath,record.get(BasisAnnex::getFileSourceName),record.get(BasisAnnex::getFileuploadId)));
        item.put(ContractBpmDefine.LINE_FIELD_YJ_SCSJ,BpmResult.sdfDate(record.get(BasisAnnex::getCreationDate)));
        addItemWithTableName(itemData,item, ContractBpmDefine.LINE_BO_EU_LCHTYJ);
    }

    /**
     * 添加附件数据
     * @param itemData
     * @param annexes
     */
    private void setAnnexes(List<Object> itemData, List<Record> annexes) {
        if(ObjectUtil.isNotEmpty(annexes)){
            for (Record record:annexes){
                setAnnex(itemData,record);
            }
        }
    }

    private void setAnnex(List<Object> itemData, Record record) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_FJ_FJLX,getDictName("CONTRACT_AGREEMENT_ATTACHMENT",record.get(Annex::getFileType)));
        item.put(ContractBpmDefine.LINE_FIELD_FJ_SCR,record.get(Annex::getCreatedFullName));
        item.put(ContractBpmDefine.LINE_FIELD_FJ_SCSJ,BpmResult.sdfDate(record.get(Annex::getCreationDate)));
        item.put(ContractBpmDefine.LINE_FIELD_FJ_FJMX,BpmResult.getFileList(downloadBasePath,record.get(Annex::getFileSourceName),record.get(Annex::getFileuploadId)));
        addItemWithTableName(itemData,item, ContractBpmDefine.LINE_BO_EU_LCHTFJXX);
    }


    /**
     * 添加附件数据
     * @param itemData
     * @param annexes
     */
    private void setHtzjAnnexes(List<Object> itemData, List<Record> annexes) {
        if(ObjectUtil.isNotEmpty(annexes)){
            for (Record record:annexes){
                setHtzjAnnex(itemData,record);
            }
        }
    }

    private void setHtzjAnnex(List<Object> itemData, Record record) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_FJ_FJLX,getDictName("CONTRACT_AGREEMENT_ATTACHMENT",record.get(Annex::getFileType)));
        item.put(ContractBpmDefine.LINE_FIELD_FJ_SCR,record.get(Annex::getCreatedFullName));
        item.put(ContractBpmDefine.LINE_FIELD_FJ_SCSJ,BpmResult.sdfDate(record.get(Annex::getCreationDate)));
        item.put("FJMC",BpmResult.getFileList(downloadBasePath,record.get(Annex::getFileSourceName),record.get(Annex::getFileuploadId)));
        addItemWithTableName(itemData,item,"BO_EU_HTFJXX");
    }




    /**
     * 添加所有子表
     * @param itemTables
     */
    private void addAllSubTables(List<String> itemTables) {
        itemTables.add(ContractBpmDefine.LINE_BO_EU_LCFKJH);
        itemTables.add(ContractBpmDefine.LINE_BO_EU_LCHTCWXX);
        itemTables.add(ContractBpmDefine.LINE_BO_EU_LCHTFJXX);
        itemTables.add(ContractBpmDefine.LINE_BO_EU_LCHTQYMX);
        itemTables.add(ContractBpmDefine.LINE_BO_EU_LCHTYJ);
        itemTables.add(ContractBpmDefine.LINE_BO_EU_LCHZHB);
    }

    private void setPayPlans(List<Object> itemData,List<Record> playPlans) {
        if(ObjectUtil.isNotEmpty(playPlans)){
            for (Record record:playPlans){
                setPayPlan(itemData,record);
            }
        }
    }

    private void setPayPlan(List<Object> itemData, Record record) {
        Map<String,Object> item = new HashMap<>(16);
        item.put(ContractBpmDefine.LINE_FIELD_JH_CDBL, record.get(PayPlanExt::getExtAcceptanceRatio));
        item.put(ContractBpmDefine.LINE_FIELD_JH_CDQX, ObjectUtil.isNotEmpty(record.get(PayPlanExt::getExtAcceptanceDate))?BpmResult.formatDate(record.get(PayPlanExt::getExtAcceptanceDate)):record.get(PayPlanExt::getExtAcceptanceDate));
        item.put(ContractBpmDefine.LINE_FIELD_JH_FKFS, getDictName("PAYMENT_MODE",record.get(PayPlanExt::getPayMethod)));
        item.put(ContractBpmDefine.LINE_FIELD_JH_FKJD,getDictName("PAYMENT_STAGE",record.get(PayPlanExt::getPaymentStage)));
        item.put(ContractBpmDefine.LINE_FIELD_JH_FKQS,record.get(PayPlanExt::getPaymentPeriod));
        item.put(ContractBpmDefine.LINE_FIELD_JH_FKTJ,record.get(PayPlanExt::getPayExplain));
        item.put(ContractBpmDefine.LINE_FIELD_JH_JDFKJE,record.get(PayPlanExt::getStagePaymentAmount));
        item.put(ContractBpmDefine.LINE_FIELD_JH_JHFKRQ, BpmResult.formatLocalDate(record.get(PayPlanExt::getPlannedPaymentDate)));
        addItemWithTableName(itemData,item, ContractBpmDefine.LINE_BO_EU_LCFKJH);
    }


    private void setModelLines(List<Record> modelLines) {
    }

    private void setBaseContractInfo(Map<String, Object> mainTableData,Record tempRecord) {
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_BZ,tempRecord.get(ContractHead::getContractRemark));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_BZHT,Enable.Y.name().equals(tempRecord.get(ContractHeadFieldName.ENABLE_FIELD))?"是":"否");
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_CJR,tempRecord.get(ContractHead::getCreatedFullName));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_CJSJ,dateFormat.format(tempRecord.get(ContractHead::getCreationDate)));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_GYSMC,tempRecord.get(ContractHead::getVendorName));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTBH,tempRecord.get(ContractHead::getContractNo));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTJBR,tempRecord.get(ContractHeadFieldName.EXT_CONTRACT_HANDLER_NAME));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTLX,getDictName("ELEM_CONTRACT_TYPE",tempRecord.get(ContractHead::getContractClass)));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTMC,tempRecord.get(ContractHead::getContractName));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTMS,getDictName("MANAGEMENT_CONTROL_MODEL",tempRecord.get(ContractHead::getCeeaControlMethod)));
        Date startDate = (Date) tempRecord.get("effectiveDateFrom");
        Date endDate = (Date)tempRecord.get("effectiveDateTo");
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTYXQC, ObjectUtil.isNotEmpty(startDate)?dateFormat.format(startDate):startDate);
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_HTYXQZ, ObjectUtil.isNotEmpty(endDate)?dateFormat.format(endDate):endDate);
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_MBMC,tempRecord.get(ContractHead::getModelName));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_QCRYJ,tempRecord.get(ContractHead::getDrafterOpinion));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_QSFS,getDictName("CONTRACT_FORM2",tempRecord.get(ContractHead::getFormal)));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_QYDZ,tempRecord.get(ContractHead::getSigningAddress));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_SFJJGK, Enable.Y.name().equals(tempRecord.get(ContractHeadFieldName.EXT_PRICE_POOL_FLAG_FIELD))?"是":"否");
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_SFKHXY,Enable.Y.name().equals(tempRecord.get(ContractHead::getIsFrameworkAgreement))?"是":"否");
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_SFQYMB,Enable.Y.name().equals(tempRecord.get(ContractHeadFieldName.MODEL_ENABLE_FIELD))?"是":"否");
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_SFXYGYSQR,Enable.Y.name().equals(tempRecord.get(ContractHead::getNeedVendorConfirm))?"是":"否");
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_TZBH,tempRecord.get(ContractHeadFieldName.EXT_INVEST_NO_FIELD));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_WFQYZT,tempRecord.get(ContractHead::getBuName));
        mainTableData.put(ContractBpmDefine.HEAD_FIELD_ZT,getDictName("CONTRACT_STATUS",tempRecord.get(ContractHead::getContractStatus)));

    }

    private void addItemWithTableName(List<Object> itemData, Map<String, Object> item, String tableName) {
        item.put("__TABLE",tableName);
        itemData.add(item);
    }


    private String getDictName(String dictCode, String va) {
        List<DictItemDTO> dictItemDTOS = dictItemMap.containsKey(dictCode)?dictItemMap.get(dictCode):baseClient.listAllByDictCode(dictCode);
        dictItemMap.putIfAbsent(dictCode,dictItemDTOS);
        for (DictItemDTO e : dictItemDTOS) {
            if (e.getDictItemCode().equals(va)) {
                return e.getDictItemName();
            }
        }
        return null;
    }


}
