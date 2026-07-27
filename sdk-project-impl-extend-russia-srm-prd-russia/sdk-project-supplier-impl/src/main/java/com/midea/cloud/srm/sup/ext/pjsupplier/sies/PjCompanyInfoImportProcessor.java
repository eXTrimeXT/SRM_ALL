package com.midea.cloud.srm.sup.ext.pjsupplier.sies;

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
import com.midea.cloud.meiql.core.core.QlDispatcher;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.common.enums.*;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.info.entity.*;
import com.midea.cloud.srm.sies.pojo.*;
import com.midea.cloud.srm.sies.processor.AbstractImportProcessor;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.info.service.IFinanceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luxc18
 * @description 绿色通道导入
 * @date 2023/7/18 17:30
 */
@Component
@Slf4j
public class PjCompanyInfoImportProcessor extends AbstractImportProcessor {
    private static final int IMPORT_BATCH_SIZE = 1000;

    public static final String COMPANY_NAME_ID_KEY = "-nameIdMap";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BaseClient baseClient;

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
    private final int SIX = 6;


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
        } else if (sheetNo == FIVE) {
            // 公司规模
            buildCompanySize(vaildatorTaskId, data, importResult);
        } else if (sheetNo == SIX) {
            // 组装服务范围
            buildServiceCustom(vaildatorTaskId, data, importResult);
        }
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
//            updateCompanyCode(vaildatorTaskId);
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
            super.doFinish(param);
            //注册用户
            List<?> users = extData.getAllManualData().get("User");
            if (CollectionUtils.isNotEmpty(users)) {
                rbacClient.registerVendorBatch((List<User>) users);
            }
            //更新编码
            updateCompanyCode(vaildatorTaskId);
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
        if (key.contains(PjCompanyInfoImportValidator.DICT_KEY)) {
            DictItemDTO o1 = (DictItemDTO) o;
            if (null == o1) {
                return null;
            }
            return o1.getDictItemCode();
        } else {
            return o;
        }
    }

    private <T> T getRedisData(String key, String hashKey, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
                return clazz.newInstance();
            }
            return clazz.cast(getRedisData(key, hashKey));
        } catch (BaseException e) {
            log.error("获取redis对象失败" + e);
        } catch (Exception e) {
            log.error("获取redis对象失败" + e);
        }
        return null;
    }

    private void buildCompanyInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        Map<String, Long> companyNameIdMap = new HashMap<>(16);
        for (Map<String, Object> row : data) {
            Record companyInfo = Record.of(row);
            companyInfo.put(CompanyInfo::getCompanyId, IdGenrator.generate());
            companyInfo.put(CompanyInfo::getOverseasRelation, getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getOverseasRelation)));
            PurchaseCurrency curreny = getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.CURRENY_KEY, companyInfo.get(CompanyInfo::getRegistCurrencyName), PurchaseCurrency.class);
            companyInfo.put(CompanyInfo::getRegistCurrency, curreny.getCurrencyCode());
            companyInfo.put(CompanyInfo::getCeeaBusinessModel, getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getCeeaBusinessModel)));
            companyInfo.put(CompanyInfo::getCompanyCountry, getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY, companyInfo.get(CompanyInfo::getCompanyCountry)));
            companyInfo.put(CompanyInfo::getSupplierType, SupplierTypeEnum.NO_MATERIAL.name());
            companyInfo.put(CompanyInfo::getCompanyProvince, getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.PROVINCE_KEY, companyInfo.get(CompanyInfo::getCompanyProvince)));
            companyInfo.put(CompanyInfo::getCompanyCity, getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.CITY_KEY, companyInfo.get(CompanyInfo::getCompanyCity)));
            companyInfo.put(CompanyInfo::getCeeaIfListed, YesOrNo.YES.getName().equals(companyInfo.get(CompanyInfo::getCeeaIfListed)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            companyInfo.put(CompanyInfo::getCeeaHasParentCompany, YesOrNo.YES.getName().equals(companyInfo.get(CompanyInfo::getCeeaHasParentCompany)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            // 二开字段处理
            companyInfo.put("groupCountry", getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY, (String) companyInfo.get("groupCountry")));
            companyInfo.put("pjCompanyStatus", getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY, (String) companyInfo.get("pjCompanyStatus")));
            // gscp先不处理
            companyInfo.put("focusFlag", YesOrNo.YES.getName().equals((String) companyInfo.get("focusFlag")) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            companyInfo.put("keySupervisionFlag", YesOrNo.YES.getName().equals((String) companyInfo.get("keySupervisionFlag")) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            companyInfo.put("biddingFlag", YesOrNo.YES.getName().equals((String) companyInfo.get("biddingFlag")) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            companyInfo.put("contractVerification", YesOrNo.YES.getName().equals((String) companyInfo.get("contractVerification")) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            companyInfo.put("infoCompleteFlag",Enable.N.name());
            companyInfo.put(CompanyInfo::getDataSources, SupplierDataSourceType.INITIALIZE.getValue());
            companyInfo.put(CompanyInfo::getStatus, ApproveStatusType.APPROVED.getValue());
            companyInfo.put(CompanyInfo::getStatusName, ApproveStatusType.APPROVED.getName());
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
            String companyName = companyInfo.get(CompanyInfo::getCompanyName);
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(companyInfo));
            companyNameIdMap.put(companyInfo.get(CompanyInfo::getCompanyName), companyInfo.get(CompanyInfo::getCompanyId));
        }
        redisTemplate.opsForHash().putAll(vaildatorTaskId + COMPANY_NAME_ID_KEY, companyNameIdMap);
        redisTemplate.opsForHash().getOperations().expire(vaildatorTaskId + COMPANY_NAME_ID_KEY, PjCompanyInfoImportValidator.EXPIRE_TIME, TimeUnit.SECONDS);

    }

    private void buildContactInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record contactInfo = Record.of(row);
            contactInfo.put(ContactInfo::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, contactInfo.getString("companyName")));
            contactInfo.put(ContactInfo::getCeeaGender, getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY, contactInfo.get(ContactInfo::getCeeaGender)));
            contactInfo.put(ContactInfo::getCeeaDefaultContact, YesOrNo.YES.getName().equals(contactInfo.get(ContactInfo::getCeeaDefaultContact)) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(contactInfo));
        }
    }

    private void buildBankInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record bankInfo = Record.of(row);
            bankInfo.put(BankInfo::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, bankInfo.getString("companyName")));
            PurchaseCurrency curreny = getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.CURRENY_KEY, bankInfo.get(BankInfo::getCurrencyName), PurchaseCurrency.class);
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
            userInfo.put(User::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, userInfo.getString("companyName")));
            userInfo.put("mainType", YesOrNo.YES.getName().equals((String) userInfo.get("mainType")) ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());

            //获取初始化密码
            String password = null;
            if (systemConfigureDTO != null && Enable.Y == systemConfigureDTO.getParamStatus() && StringUtils.isNotBlank(systemConfigureDTO.getParamValue())) {
                password = DigestUtils.sha1Hex(systemConfigureDTO.getParamValue());
            }
            userInfo.put(User::getPassword, password);
            User user = BeanCopyUtil.convertWithExtensions(userInfo, User.class);
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(user, false));
        }
    }

    private void buildOrgCateInfo(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        //List<Record> records = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record orgCategory = Record.of(row);
            orgCategory.put(OrgCategory::getCompanyId, getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, orgCategory.getString("companyName")));
            Organization organization = getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.ORG_ORG_KEY, orgCategory.get(OrgCategory::getOrgName), Organization.class);
            PurchaseCategory purchaseCategory = getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.ORG_CATE_KEY, orgCategory.get(OrgCategory::getCategoryCode), PurchaseCategory.class);

            orgCategory.put(OrgCategory::getOrgId, organization.getOrganizationId());
            orgCategory.put(OrgCategory::getOrgName, organization.getOrganizationName());
            orgCategory.put(OrgCategory::getOrgCode, organization.getOrganizationCode());
            orgCategory.put(OrgCategory::getCategoryId, purchaseCategory.getCategoryId());
            orgCategory.put(OrgCategory::getCategoryCode, purchaseCategory.getCategoryCode());
            orgCategory.put(OrgCategory::getCategoryName, purchaseCategory.getCategoryName());
            orgCategory.put(OrgCategory::getCompanyStatus, CompanyStatusEnum.QUALIFIED.name());
            orgCategory.put(OrgCategory::getWarningStatus, WarningStatusEnum.GREEN.name());
            orgCategory.put(OrgCategory::getServiceStatus, CategoryStatus.QUALIFIED.name());
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(orgCategory));
        }
    }

    private void buildCompanySize(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        for (Map<String, Object> row : data) {
            Record companySize = Record.of(row);
            companySize.put("companyId", getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, companySize.getString("companyName")));
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(companySize));
        }
    }

    private void buildServiceCustom(String vaildatorTaskId, List<SiesData> data, SiesImportResult importResult) {
        Map<String, String> categoryMap = new HashMap<>(16);
        List<Record> headerList = new ArrayList<>();
        List<Record> detailList = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Record serviceCustom = Record.of(row);
            Long companyId = getRedisData(vaildatorTaskId + COMPANY_NAME_ID_KEY, serviceCustom.getString("companyName"), Long.class);
            PurchaseCategory purchaseCategory = getRedisData(vaildatorTaskId + PjCompanyInfoImportValidator.SERVICE_CUSTOM_CATE_KEY, serviceCustom.getString("categoryCode"), PurchaseCategory.class);
            serviceCustom.put("categoryJournalId", IdGenrator.generate());
            serviceCustom.put("companyId", companyId);

            if (!categoryMap.containsKey(purchaseCategory.getCategoryCode())) {
                serviceCustom.put("vendorId", companyId);
                serviceCustom.put("formId", companyId);
                serviceCustom.put("categoryId", purchaseCategory.getCategoryId());
                serviceCustom.put("categoryCode", purchaseCategory.getCategoryCode());
                serviceCustom.put("categoryName", purchaseCategory.getCategoryName());
                if(StringUtils.isNotEmpty(purchaseCategory.getCategoryFullName())){
                    String[] split = purchaseCategory.getCategoryFullName().split("-");
                    if(split.length > 1){
                        serviceCustom.put("categoryFullName", split[0]+"-"+split[1]);
                    }
                }
                categoryMap.put(purchaseCategory.getCategoryCode(), purchaseCategory.getCategoryCode());
                headerList.add(serviceCustom);
            }
            detailList.add(serviceCustom);
            // 不自动保存
            importResult.addSuccessRow(new SiesImportResult.SuccessRow(serviceCustom, false));
        }
        qlService.create("CateJournalCompany", headerList);
        qlService.create("npmSerciceCustom", detailList);
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
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.COMPANY_NAME_KEY);
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.DICT_KEY);
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.CURRENY_KEY);
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.PROVINCE_KEY);
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.CITY_KEY);
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.ORG_ORG_KEY);
        redisTemplate.delete(vaildatorTaskId + PjCompanyInfoImportValidator.ORG_CATE_KEY);
        redisTemplate.delete(vaildatorTaskId + COMPANY_NAME_ID_KEY);
    }
}
