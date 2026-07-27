package com.midea.cloud.srm.sup.info.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.meicloud.paas.ies.core.IesTask;
import com.meicloud.paas.ies.core.model.ExcelImportRequest;
import com.meicloud.paas.ies.core.model.SheetConfig;
import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.constants.SystemConfigureKey;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.SupplierDataSourceType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlDispatcher;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.common.enums.CategoryStatus;
import com.midea.cloud.srm.model.common.enums.CompanyStatusEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.WarningStatusEnum;
import com.midea.cloud.srm.model.common.enums.review.FinanceSourceDataEnum;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sup.info.dto.NpmSerciceCustomInfoDTO;
import com.midea.cloud.srm.model.supplier.info.dto.OrgCategoryModelDTO;
import com.midea.cloud.srm.model.supplier.info.entity.*;
import com.midea.cloud.srm.sies.pojo.*;
import com.midea.cloud.srm.sies.processor.AbstractImportProcessor;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.info.service.IFinanceInfoService;
import com.midea.cloud.srm.sup.info.sies.CompanyInfoImportValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 供应商导入（个人）Processor
 * @author 100014323
 */
@Component
@Slf4j
public class CompanyInfoPersonImportProcessor extends AbstractImportProcessor {


    private static final int IMPORT_BATCH_SIZE = 1000;

    public static final String COMPANY_NAME_ID_KEY = "-nameIdMap";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private QlDispatcher dispatcher;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private ICompanyInfoService companyInfoService;

    @Autowired
    private IFinanceInfoService financeInfoService;

    private final int TWO = 2;
    private final int THREE = 3;
    private final int FOUR = 4;
    private final int FIVE = 5;

    @Override
    public SiesImportResult doImport(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        //获取校验完的结果
        List<ImportResultModel.ErrorRow> errorRows = new ArrayList<>();
        String vaildatorTaskId = (String) param.getExtData().get("vaildatorTaskId");
        // 组装数据
        // todo 最后一步再获取供应商编码,防止跳号
        SiesImportResult importResult = new SiesImportResult();
        if (sheetNo == 0) {
            // 组装供应商主数据
            buildCompanyInfo(vaildatorTaskId, data, importResult);
        } else if (sheetNo == 1) {
            // 组装联系人
            buildContactInfo(vaildatorTaskId, data, importResult);
        } else if (sheetNo == TWO) {
            // 组装银行
            buildBankInfo(vaildatorTaskId, data, importResult);
        } else if (sheetNo == THREE) {
            // 组装账号
            buildUserInfo(vaildatorTaskId, data, importResult);
        } else if (sheetNo == FOUR) {
            // 组装合作信息
            buildOrgCateInfo(vaildatorTaskId, data, importResult);
        } else if (sheetNo ==FIVE) {
            /*     try {*/
            // 组装服务信息
            buildNpmSerciceCustomInfo(vaildatorTaskId, data, importResult);
  /*          } finally {
                deleteRedis(vaildatorTaskId);
            }*/
        }
        //return new SiesImportResult(errorRows);
        return importResult;
    }

//    @Override
//    public void doFinish(ExcelImportRequest<SiesImportParam> request, String iesTaskId, IesTask iesTask) {
//        List<SheetConfig<SiesImportParam>> sheets = request.getSheets();
//        if (CollectionUtils.isEmpty(sheets)) {
//            throw new BaseException("Sheet不能为空");
//        }
//        SiesImportExtData extData = sheets.get(0).getDataParam().getExtData();
//        String vaildatorTaskId = (String) extData.get("vaildatorTaskId");
//        try {
//            //调用标准保存
//            super.doFinish(request, iesTaskId, iesTask);
//            //注册用户
//            List<?> users = extData.getAllManualData().get("User");
//            if (CollectionUtils.isNotEmpty(users)) {
//                rbacClient.registerVendorBatch((List<User>) users);
//            }
//            //更新编码
//            //updateCompanyCode(vaildatorTaskId);
//        } finally {
//            if (!StringUtils.isEmpty(vaildatorTaskId)) {
//                deleteRedis(vaildatorTaskId);
//            }
//        }
//    }


    @Override
    public void doFinish(SiesImportParam param) {
        List<SiesSheet> sheets = param.getMediator().getSheets();
        if (CollectionUtils.isEmpty(sheets)) {
            throw new BaseException("Sheet不能为空");
        }
        SiesImportExtData extData = param.getExtData();
        String vaildatorTaskId = (String) extData.get("vaildatorTaskId");
        try {
            //调用标准保存
            super.doFinish(param);;
            //注册用户
            List<?> users = extData.getAllManualData().get("User");
            if (CollectionUtils.isNotEmpty(users)) {
                rbacClient.registerVendorBatch((List<User>) users);
            }
            //更新编码
            //updateCompanyCode(vaildatorTaskId);
        } finally {
            if (!StringUtils.isEmpty(vaildatorTaskId)) {
                deleteRedis(vaildatorTaskId);
            }
        }
    }

    private Object getRedisData(String key, String hashKey) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
            return "";
        }
        Object o = redisTemplate.opsForHash().get(key, hashKey);
        if (key.contains(CompanyInfoImportValidator.DICT_KEY)) {
            DictItemDTO o1 = (DictItemDTO) o;
            if (null == o1) {
                return null;
            }
            return o1.getDictItemCode();
        } else {
            return o;
        }
    }

    private void buildCompanyInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        Map<String, Long> companyNameIdMap = new HashMap<>(16);
        for (Map<String, Object> row : data) {
            Record companyInfo = Record.of(row);
            companyInfo.put(CompanyInfo::getCompanyId, IdGenrator.generate());
            //企业类型默认个人
            companyInfo.put(CompanyInfo::getOverseasRelation, "PERSONAL");
            String companyTypeName=companyInfo.getString("companyTypeName");
            if(StringUtils.isNotBlank(companyTypeName)){
                companyInfo.put(CompanyInfo::getCompanyType, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getCompanyTypeName)));
            }
            String registCurrencyName=companyInfo.getString("registCurrencyName");
            if(StringUtils.isNotBlank(registCurrencyName)){
                PurchaseCurrency curreny = (PurchaseCurrency) getRedisData(vaildatorTaskId + CompanyInfoImportValidator.CURRENY_KEY, companyInfo.get(CompanyInfo::getRegistCurrencyName));
                companyInfo.put(CompanyInfo::getRegistCurrency, curreny.getCurrencyCode());
            }
            String ceeaBusinessModel=companyInfo.getString("ceeaBusinessModel");
            if(StringUtils.isNotBlank(ceeaBusinessModel)){
                companyInfo.put(CompanyInfo::getCeeaBusinessModel, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getCeeaBusinessModel)));
            }
            String ceeaSupBusinessType=companyInfo.getString("ceeaSupBusinessType");
            if(StringUtils.isNotBlank(ceeaSupBusinessType)){
                companyInfo.put(CompanyInfo::getCeeaSupBusinessType, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getCeeaSupBusinessType)));
            }
            String companyCountry=companyInfo.getString("companyCountry");
            if(StringUtils.isNotBlank(companyCountry)){
                companyInfo.put(CompanyInfo::getCompanyCountry, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getCompanyCountry)));
            }
            //供应商类型默认非材类
            companyInfo.put(CompanyInfo::getSupplierType, "NO_MATERIAL");
            String companyProvince=companyInfo.getString("companyProvince");
            if(StringUtils.isNotBlank(companyProvince)){
                companyInfo.put(CompanyInfo::getCompanyProvince, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.PROVINCE_KEY, companyInfo.get(CompanyInfo::getCompanyProvince)));
            }
            String companyCity=companyInfo.getString("companyCity");
            if(StringUtils.isNotBlank(companyCity)){
                companyInfo.put(CompanyInfo::getCompanyCity, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.CITY_KEY, companyInfo.get(CompanyInfo::getCompanyCity)));
            }
            companyInfo.put("extSex", getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, companyInfo.getString("extSex")));
            String ceeaIfListed=companyInfo.getString("ceeaIfListed");
            if(StringUtils.isNotBlank(ceeaIfListed)){
                companyInfo.put(CompanyInfo::getCeeaIfListed, YesOrNo.YES.getName().equals(companyInfo.get(CompanyInfo::getCeeaIfListed))? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            }

            companyInfo.put(CompanyInfo::getDataSources, SupplierDataSourceType.INITIALIZE.getValue());
            companyInfo.put(CompanyInfo::getStatus, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, (String) row.get("statusName")));
            companyInfo.put(CompanyInfo::getPotentialFlag, Enable.N.name());
            companyInfo.put(CompanyInfo::getApprovedDate, LocalDate.now());
            companyInfo.put(CompanyInfo::getFirstLoginFlag, Enable.N.name());
            try {
                Date companyCreationDate = DateUtil.parseDate(row.get("companyCreationDate"));
                Date businessStartDate = DateUtil.parseDate(row.get("businessStartDate"));
                Date businessEndDate = DateUtil.parseDate(row.get("businessEndDate"));
                LocalDate companyCreationDateLd = DateUtil.dateToLocalDate(companyCreationDate);
                LocalDate businessStartDateLd = DateUtil.dateToLocalDate(businessStartDate);
                LocalDate localDateLd = DateUtil.dateToLocalDate(businessEndDate);
                companyInfo.put(CompanyInfo::getCompanyCreationDate, companyCreationDateLd);
                companyInfo.put(CompanyInfo::getBusinessStartDate, businessStartDateLd);
                companyInfo.put(CompanyInfo::getBusinessEndDate, localDateLd);
            } catch (Exception e) {
                log.error("mql绿色通道导入-日期转换报错:" + e);
            }
            companyInfo.put("infoCompleteFlag",Enable.N.name());
            String companyName = companyInfo.get(CompanyInfo::getCompanyName);
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(companyInfo));
            companyNameIdMap.put(companyInfo.get(CompanyInfo::getIdNumber), companyInfo.get(CompanyInfo::getCompanyId));
        }
        redisTemplate.opsForHash().putAll(vaildatorTaskId + COMPANY_NAME_ID_KEY, companyNameIdMap);
        redisTemplate.opsForHash().getOperations().expire(vaildatorTaskId + COMPANY_NAME_ID_KEY, CompanyInfoImportValidator.EXPIRE_TIME, TimeUnit.SECONDS);

        // 转成mql格式,保存
/*        try {
            List<Serializable> apply = qlService.apply("CompanyInfo", "importSave", records);
            if (!CollectionUtils.isEmpty(apply)) {
                for (int i = 0; i < records.size(); i++) {
                    Record record = records.get(i);

                }
            } else {
                throw new BaseException("MQL供应商主数据导入失败;");
            }
            redisTemplate.opsForHash().putAll(vaildatorTaskId + COMPANY_NAME_ID_KEY, companyNameIdMap);
            redisTemplate.opsForHash().getOperations().expire(vaildatorTaskId + COMPANY_NAME_ID_KEY, CompanyInfoImportValidator.EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("MQL供应商主数据导入失败:" + e);
            throw new BaseException("MQL供应商主数据导入失败");
        }*/
    }



    private void buildContactInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record contactInfo = Record.of(row);
            contactInfo.put(ContactInfo::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, contactInfo.getString("idNumber")));
            contactInfo.put(ContactInfo::getCeeaGender, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, contactInfo.get(ContactInfo::getCeeaGender)));
            contactInfo.put(ContactInfo::getCeeaDefaultContact, YesOrNo.YES.getName().equals(contactInfo.get(ContactInfo::getCeeaDefaultContact)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(contactInfo));
        }
    }

    private void buildBankInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record bankInfo = Record.of(row);
            bankInfo.put(BankInfo::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, bankInfo.getString("idNumber")));
            PurchaseCurrency curreny = (PurchaseCurrency) getRedisData(vaildatorTaskId + CompanyInfoImportValidator.CURRENY_KEY, bankInfo.get(BankInfo::getCurrencyName));
            bankInfo.put(BankInfo::getCurrencyCode, curreny.getCurrencyCode());
            bankInfo.put(BankInfo::getCeeaMainAccount, YesOrNo.YES.getName().equals(bankInfo.get(BankInfo::getCeeaMainAccount)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            bankInfo.put(BankInfo::getCeeaEnabled, YesOrNo.YES.getName().equals(bankInfo.get(BankInfo::getCeeaEnabled)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(bankInfo));
        }
    }

    private void buildUserInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<User> saveList = new ArrayList<>();
        SystemConfigureDTO systemConfigureDTO = baseClient.getSystemConfigure(SystemConfigureKey.GREEN_CHANNEL_INIT_PASSWORD.name());
        for (Map<String, Object> row : data) {
            Record userInfo = Record.of(row);
            userInfo.put(User::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, userInfo.getString("idNumber")));
            //获取初始化密码
            String password = null;
            if (systemConfigureDTO != null && Enable.Y == systemConfigureDTO.getParamStatus() && StringUtils.isNotBlank(systemConfigureDTO.getParamValue())) {
                password = DigestUtils.sha1Hex(systemConfigureDTO.getParamValue());
            }
            userInfo.put(User::getPassword, password);
            userInfo.put(User::getMainType, YesOrNo.YES.getName().equals(userInfo.get(User::getMainType)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            User user = BeanCopyUtil.convertWithExtensions(userInfo, User.class);
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(user, false));
        }

/*        try {
            rbacClient.registerVendorBatch(saveList);
        } catch (Exception e) {
            log.error("mql导入-账号生成失败:" + e);
            throw new BaseException(e.getMessage());
        }*/
    }

    private void buildOrgCateInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record orgCategory = Record.of(row);
            orgCategory.put(OrgCategory::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, orgCategory.getString("idNumber")));
            Organization organization = (Organization) getRedisData(vaildatorTaskId + CompanyInfoImportValidator.ORG_ORG_KEY, orgCategory.get(OrgCategory::getOrgName));
            PurchaseCategory purchaseCategory = (PurchaseCategory) getRedisData(vaildatorTaskId + CompanyInfoImportValidator.ORG_CATE_KEY, orgCategory.get(OrgCategory::getCategoryCode));

            orgCategory.put(OrgCategory::getOrgId, organization.getOrganizationId());
            orgCategory.put(OrgCategory::getOrgName, organization.getOrganizationName());
            orgCategory.put(OrgCategory::getOrgCode, organization.getOrganizationCode());
            orgCategory.put(OrgCategory::getCategoryId, purchaseCategory.getCategoryId());
            orgCategory.put(OrgCategory::getCategoryCode, purchaseCategory.getCategoryCode());
            orgCategory.put(OrgCategory::getCategoryName, purchaseCategory.getCategoryName());
            orgCategory.put(OrgCategory::getCompanyStatus, CompanyStatusEnum.QUALIFIED.name());
            orgCategory.put(OrgCategory::getWarningStatus, WarningStatusEnum.GREEN.name());
            String serviceStatus=orgCategory.getString("serviceStatus");
            if(StringUtils.isNotBlank(serviceStatus)){
                orgCategory.put(OrgCategory::getServiceStatus, getRedisData(vaildatorTaskId + CompanyInfoImportValidator.DICT_KEY, serviceStatus));
            }else{
                orgCategory.put(OrgCategory::getServiceStatus, CategoryStatus.QUALIFIED.name());
            }

            importResult.addSuccessRow(new SiesImportResult.SuccessRow(orgCategory));
        }
    }

    private void buildNpmSerciceCustomInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        Map<String,NpmSerciceCustomInfoDTO> headMap=new HashMap(16);
        for (Map<String, Object> row : data) {
            Record npmSerciceCustomInfo = Record.of(row);
            NpmSerciceCustomInfoDTO entity = BeanUtil.mapToBean(row, NpmSerciceCustomInfoDTO.class, true, null);
            String companyId=getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, npmSerciceCustomInfo.getString("idNumber")).toString();
            entity.setVendorId(Long.valueOf(companyId));
            String groupkey=entity.getLcCode()+"-"+entity.getCategoryCode()+"-"+entity.getCategoryName()+"-"+entity.getContactName();
            if(!headMap.containsKey(groupkey)){
                entity.setCategoryJournalId(IdGenrator.generate());
                headMap.put(groupkey,entity);
            }
        }
        //保持服务信息头
        qlService.create("CateJournalCompany", MeiQl.toListValue(headMap.values(),Record.class));

        for (Map<String, Object> row : data) {
            NpmSerciceCustomInfoDTO entity = BeanUtil.mapToBean(row, NpmSerciceCustomInfoDTO.class, true, null);
            Record npmSerciceCustomInfo = Record.of(row);
            String groupkey=entity.getLcCode()+"-"+entity.getCategoryCode()+"-"+entity.getCategoryName()+"-"+entity.getContactName();
            if(headMap.containsKey(groupkey)){
                NpmSerciceCustomInfoDTO serciceCustomInfoDTO=headMap.get(groupkey);
                npmSerciceCustomInfo.put(NpmSerciceCustomInfoDTO::getCategoryJournalId,serciceCustomInfoDTO.getCategoryJournalId());
                npmSerciceCustomInfo.put("companyId", serciceCustomInfoDTO.getVendorId());
            }
            Object categoryObj=getRedisData(vaildatorTaskId + CompanyInfoPersonImportValidator.PURCHASE_CATEGORY_KEY, npmSerciceCustomInfo.getString("categoryCode"));;
            if(categoryObj!=null){
                PurchaseCategory purchaseCategory=(PurchaseCategory)categoryObj;
                npmSerciceCustomInfo.put("categoryId", purchaseCategory.getCategoryId());
                npmSerciceCustomInfo.put("categoryFullName", purchaseCategory.getCategoryFullName());
            }
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(npmSerciceCustomInfo));
        }


    }

    private void updateCompanyCode(String vaildatorTaskId) {
        try {
            // 最后生成供应商编码,并补齐供应商主信息和财务信息
            Map<String, Long> nameIdMap = (Map<String, Long>) redisTemplate.opsForHash().entries(vaildatorTaskId + COMPANY_NAME_ID_KEY);
            List<CompanyInfo> companyInfoList = companyInfoService.list(Wrappers.lambdaQuery(CompanyInfo.class)
                    .in(CompanyInfo::getCompanyId, nameIdMap.values()));
            // 空的供应商编码才需要补全
            List<CompanyInfo> emptyCodeList = companyInfoList.stream().filter(item -> StringUtils.isEmpty(item.getCompanyCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(emptyCodeList)) {
                int size = emptyCodeList.size();
                List<String> companyCodes = baseClient.batchGetSeq(SequenceCodeConstant.SEQ_SUP_COMPANY_CODE, size);
                for (int i = 0; i < companyCodes.size(); i++) {
                    String companyCode = companyCodes.get(i);
                    emptyCodeList.get(i).setCompanyCode(companyCode);
                }
                companyInfoService.updateBatchById(emptyCodeList);
            }
            if (CollectionUtils.isNotEmpty(companyInfoList)) {
                Map<Long, String> idCodeMap = companyInfoList.stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, CompanyInfo::getCompanyCode));
                List<FinanceInfo> list = financeInfoService.list(Wrappers.lambdaQuery(FinanceInfo.class)
                        .in(FinanceInfo::getCompanyId, nameIdMap.values()));
                for (FinanceInfo financeInfo : list) {
                    financeInfo.setCompanyCode(idCodeMap.get(financeInfo.getCompanyId()));
                }
                financeInfoService.updateBatchById(list);
            }
        } catch (Exception e) {
            log.error("MQL供应商编码生成失败:" + e);
            throw new BaseException("MQL供应商编码生成失败");
        }
    }

    private void deleteRedis(String vaildatorTaskId) {
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.ID_NUMBER_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.DICT_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.CURRENY_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.PROVINCE_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.CITY_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.SITE_ORG_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.ORG_ORG_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.ORG_CATE_KEY);
        redisTemplate.delete(vaildatorTaskId + CompanyInfoPersonImportValidator.PURCHASE_CATEGORY_KEY);
        redisTemplate.delete(vaildatorTaskId + COMPANY_NAME_ID_KEY);
    }
}
