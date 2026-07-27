package com.midea.cloud.srm.sup.info.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.StringUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.base.region.dto.AreaDTO;
import com.midea.cloud.srm.model.base.region.dto.AreaPramDTO;
import com.midea.cloud.srm.model.base.region.entity.Region;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.info.dto.*;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 供应商导入（个人）Validator
 * @author 100014323
 */
@Component
@Slf4j
public class CompanyInfoPersonImportValidator extends AbstractImportValidator {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ICompanyInfoService companyInfoService;

    @Autowired
    private RbacClient rbacClient;

    public static final String ID_NUMBER_KEY = "-idNumber";
    public static final String DICT_KEY = "-dict";
    public static final String CURRENY_KEY = "-curreny";
    public static final String PROVINCE_KEY = "-province";
    public static final String CITY_KEY = "-city";
    public static final String SITE_ORG_KEY = "-siteOrg";
    public static final String ORG_ORG_KEY = "-orgCateOrg";
    public static final String ORG_CATE_KEY = "-orgCateCate";
    public static final String PURCHASE_CATEGORY_KEY = "-PurchaseCategory";
    public static final long EXPIRE_TIME = 600L;

    private final int TWO = 2;
    private final int THREE = 3;
    private final int FOUR = 4;
    private final int FIVE = 5;

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        List<ImportResultModel.ErrorRow> errorRows = new ArrayList<>();
        if (sheetNo == 0) {
            baseCommonEntity(iesTaskId);
            // 校验供应商主数据
            checkCompanyInfo(iesTaskId, data, errorRows);
            param.getExtData().put("vaildatorTaskId", iesTaskId);
        } else if (sheetNo == 1) {
            // 校验联系人
            checkContactInfo(iesTaskId, data, errorRows);
        } else if (sheetNo == TWO) {
            // 校验银行
            checkBankInfo(iesTaskId, data, errorRows);
        } else if (sheetNo == THREE) {
            // 校验账号
            checkUserInfo(iesTaskId, data, errorRows);
        } else if (sheetNo == FOUR) {
            // 校验合作信息
            checkOrgCateInfo(iesTaskId, data, errorRows);
        } else if (sheetNo == FIVE) {
            // 校验服务信息信息
            checkNpmSerciceCustomInfo(iesTaskId, data, errorRows);
        }
        return new SiesImportResult(errorRows);
    }


    /**
     * 组装基础信息存入redis,单个sheet不需要存*
     */
    private void baseCommonEntity(String iesTaskId) {
        //字典码
        //境内外关系
        List<String> dictCodes = Arrays.asList("RELATION",
                //企业性质
                "COMPANY_NATURE",
                //性别
                "GENDER",
                //商业模式
                "BIZ_MODEL",
                //供应商业务类型
                "SUP_BUSINESS_TYPE",
                //厂房性质
                "PLANT_TYPE",
                //国家
                "country",
                //财务信息-币种
                "BID_TENDER_CURRENCY",
                //财务信息-付款方式
                "PAYMENT_METHOD",
                //财务信息-账期
                "PAYMENT_TERMS",
                //供应商类型
                "SUPPLIER_TYPE",
                //供应商地点
                "VENDOR_SITE_CODE",
                //供应商状态
                "SUPPLIER_LIST_STATUS",
                //品类服务状态
                "CATEGORY_STATUS"
        );
        //字典条目
        List<DictItemDTO> dictItemDtos = baseClient.listByDictCode(dictCodes);
        Map<String, DictItemDTO> dictItemDtoMap = dictItemDtos.stream().collect(Collectors.toMap(DictItemDTO::getDictItemName, Function.identity(), (key1, key2) -> key2));
        //币种
        List<PurchaseCurrency> purchaseCurrencies = baseClient.listAllPurchaseCurrency();
        Map<String, PurchaseCurrency> purchaseCurrencyMap = purchaseCurrencies.stream().collect(Collectors.toMap(PurchaseCurrency::getCurrencyName, Function.identity(), (key1, key2) -> key2));

        //所有省份
        AreaPramDTO province = new AreaPramDTO();
        Map<String, Long> provinceMap = new HashMap<>(16);
        province.setQueryType("province");
        List<AreaDTO> provinces = baseClient.queryRegionById(province);
        if (CollectionUtils.isNotEmpty(provinces)) {
            provinces.forEach(p -> {
                provinceMap.put(p.getProvince(), p.getProvinceId());
            });
        }
        //所有城市
        Map<String, Long> cityMap = new HashMap<>(16);
        List<Region> cities = baseClient.listAllCity();
        if (CollectionUtils.isNotEmpty(cities)) {
            cities.forEach(c -> {
                cityMap.put(c.getAreaName(), c.getRegionId());
            });
        }

        redisTemplate.opsForHash().putAll(iesTaskId + DICT_KEY, dictItemDtoMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + DICT_KEY, EXPIRE_TIME, TimeUnit.SECONDS);

        redisTemplate.opsForHash().putAll(iesTaskId + CURRENY_KEY, purchaseCurrencyMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + CURRENY_KEY, EXPIRE_TIME, TimeUnit.SECONDS);

        redisTemplate.opsForHash().putAll(iesTaskId + PROVINCE_KEY, provinceMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + PROVINCE_KEY, EXPIRE_TIME, TimeUnit.SECONDS);

        redisTemplate.opsForHash().putAll(iesTaskId + CITY_KEY, cityMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + CITY_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    private Boolean checkRedisDataExist(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    private void checkCompanyInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        Map<String, String> companyCodeMap = new HashMap<>(16);
        Map<String, String> idNumberMap = new HashMap<>(16);
        extracted(iesTaskId, data, errorRows, companyCodeMap, idNumberMap);
        if (!errorRows.isEmpty()) {
            return;
        }
        // 3.批量校验供应商编码,名称,社会统一编码 DB中是否存在
        Map<String, String> dbCompanyCodeMap = new HashMap<>(16);
        Map<String, String> dbLcCodeMap = new HashMap<>(16);
        if (!companyCodeMap.isEmpty()) {
            List<CompanyInfo> dbCompanyList = companyInfoService.list(Wrappers.lambdaQuery(CompanyInfo.class)
                    .select(CompanyInfo::getCompanyCode)
                    .in(CompanyInfo::getCompanyCode, companyCodeMap.values()));
            if(CollectionUtils.isNotEmpty(dbCompanyList)){
                dbCompanyCodeMap = dbCompanyList.stream().collect(Collectors.toMap(CompanyInfo::getCompanyCode, CompanyInfo::getCompanyCode));
            }
        }

        if (!idNumberMap.isEmpty()) {
            List<CompanyInfo> dbLcCodeCompanyList = companyInfoService.list(Wrappers.lambdaQuery(CompanyInfo.class)
                    .select(CompanyInfo::getIdNumber)
                    .in(CompanyInfo::getIdNumber, idNumberMap.values()));
            if(CollectionUtils.isNotEmpty(dbLcCodeCompanyList)){
                dbLcCodeMap = dbLcCodeCompanyList.stream().collect(Collectors.toMap(CompanyInfo::getIdNumber, CompanyInfo::getIdNumber));
            }

        }

        // 有重复db数据,返回错误信息
        if (!dbCompanyCodeMap.isEmpty()  || !dbLcCodeMap.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                SiesData row=data.get(i);
                // 1. 非空校验
                StringBuilder errorMsg = new StringBuilder();
                CompanyInfoModelDTO entity = BeanUtil.mapToBean(data.get(i), CompanyInfoModelDTO.class, true, null);
                if(StringUtils.isNotBlank(entity.getCompanyCode())){
                    if (dbCompanyCodeMap.containsKey(entity.getCompanyCode())) {
                        errorMsg.append("供应商编码数据库中已存在,请检查;");
                    }
                }

                //
                if (dbLcCodeMap.containsKey(String.valueOf(row.get("idNumber")))) {
                    errorMsg.append("身份证号数据库中已存在,请检查;");
                }
                if (errorMsg.length() != 0) {
                    errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
                }
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        // 后续操作需要用到companyName
        if (!idNumberMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(iesTaskId + ID_NUMBER_KEY, idNumberMap);
            redisTemplate.opsForHash().getOperations().expire(iesTaskId + ID_NUMBER_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    /**
     * 判断
     * @param iesTaskId 参数
     * @param data 参数
     * @param errorRows 参数
     * @param companyCodeMap 参数
     * @param idNumberMap 参数
     */
    private void extracted(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows, Map<String, String> companyCodeMap, Map<String, String> idNumberMap) {
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            SiesData row= data.get(i);
            CompanyInfoModelDTO entity = BeanUtil.mapToBean(row, CompanyInfoModelDTO.class, true, null);

            String idNumber=String.valueOf(row.get("idNumber"));
            if (idNumberMap.containsKey(idNumber)) {
                errorMsg.append("身份证号码excel中已重复,请检查;");
            } else {
                idNumberMap.put(idNumber, idNumber);
            }


            Object extSex=row.get("extSex");
            if(extSex!=null){
               String sexStr= String.valueOf(extSex);
                if ( !checkRedisDataExist(iesTaskId + DICT_KEY, sexStr)) {
                    errorMsg.append("性别不存在字典编码;");
                }
            }

            Date businessStartDate=null;
            Date businessEndDate=null;
            if (StringUtils.isNotBlank(entity.getBusinessStartDate())) {
                try {
                     businessStartDate = DateUtil.parseDate(entity.getBusinessStartDate());
                } catch (Exception e){
                    errorMsg.append("身份证开始时间格式转换有误;");
                }
            }

            if (StringUtils.isNotBlank(entity.getBusinessEndDate())) {
                try {
                     businessEndDate = DateUtil.parseDate(entity.getBusinessEndDate());
                } catch (Exception e){
                    errorMsg.append("身份证结束时间格式转换有误;");
                }
            }

            if (StringUtils.isNotEmpty(entity.getCompanyCountry()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getCompanyCountry())) {
                errorMsg.append("系统不存在国家字典码;");
            }

            // 比较日期
            if (businessEndDate!=null&&businessStartDate!=null&&businessEndDate.before(businessStartDate)) {
                // 处理结束日期小于开始日期的情况
                // 可以抛出异常、设置默认值或按照需要进行处理
                errorMsg.append("身份证结束时间不能小于开始时间;");
            }

            if (StringUtils.isNotBlank(entity.getRegistCurrencyName())&&!checkRedisDataExist(iesTaskId + CURRENY_KEY, entity.getRegistCurrencyName())) {
                errorMsg.append("系统不存在该币种;");
            }
            if(StringUtils.isNotBlank( entity.getCompanyCode())){
                companyCodeMap.put(entity.getCompanyCode(), entity.getCompanyCode());
            }

            Object statusName=row.get("statusName");
            if(statusName!=null){
                String statusNameStr=String.valueOf(statusName);
                if (StringUtils.isNotEmpty(statusNameStr) && !checkRedisDataExist(iesTaskId + DICT_KEY, statusNameStr)) {
                    errorMsg.append("系统不存在该供应商状态字典码;");
                }
            }

            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
    }


    private void checkContactInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            SiesData row=data.get(i);
            ContactInfoModelDTO entity = BeanUtil.mapToBean(data.get(i), ContactInfoModelDTO.class, true, null);
            if (!checkRedisDataExist(iesTaskId + ID_NUMBER_KEY, String.valueOf(row.get("idNumber")))) {
                errorMsg.append("该身份证excel基础数据不存在;");
            }
            if (StringUtils.isNotEmpty(entity.getCeeaGender()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getCeeaGender())) {
                errorMsg.append("性别不存在字典编码;");
            }
            String ceeaDefaultContact=entity.getCeeaDefaultContact();
            if(StringUtils.isNotEmpty(ceeaDefaultContact)){
                if(!(YesOrNo.YES.getName().equals(ceeaDefaultContact)||YesOrNo.NO.getName().equals(ceeaDefaultContact))){
                    errorMsg.append("是否默认联系人只能填写是或否;");
                }
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
    }

    private void checkBankInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            SiesData row=data.get(i);
            BankInfoModelDTO entity = BeanUtil.mapToBean(data.get(i), BankInfoModelDTO.class, true, null);

             if (!checkRedisDataExist(iesTaskId + ID_NUMBER_KEY, String.valueOf(row.get("idNumber")))) {
                errorMsg.append("该身份证excel基础数据不存在;");
            }

            if (StringUtils.isNotEmpty(entity.getCurrencyName()) && !checkRedisDataExist(iesTaskId + CURRENY_KEY, entity.getCurrencyName())) {
                errorMsg.append("该币种在系统中不存在;");
            }

            String ceeaMainAccount=entity.getCeeaMainAccount();
            if(StringUtils.isNotEmpty(ceeaMainAccount)){
                if(!(YesOrNo.YES.getName().equals(ceeaMainAccount)||YesOrNo.NO.getName().equals(ceeaMainAccount))){
                    errorMsg.append("是否主账号只能填写是或否;");
                }
            }

            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
    }

    private void checkUserInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        Set<String> userNames = new HashSet<>();
        Map<String, User> userInfoMap = new HashMap<>(16);
        for (int i = 0; i < data.size(); i++) {
            SiesData row=data.get(i);
            StringBuilder errorMsg = new StringBuilder();
            UserModelDto entity = BeanUtil.mapToBean(data.get(i), UserModelDto.class, true, null);

            if (!checkRedisDataExist(iesTaskId + ID_NUMBER_KEY, String.valueOf(row.get("idNumber")))) {
                errorMsg.append("该身份证excel基础数据不存在;");
            }
            if(StringUtil.notEmpty(entity.getEmail())){
               String  email = entity.getEmail().trim();
                if(!email.matches("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.){1,10}[A-Za-z]+")){
                    errorMsg.append("邮箱格式错误; ");
                }
            }
            Object mainTypeObj=row.get("mainType");
            if(mainTypeObj!=null){
                String mainTypeStr=String.valueOf(mainTypeObj);
                if(!(YesOrNo.YES.getName().equals(mainTypeStr)||YesOrNo.NO.getName().equals(mainTypeStr))){
                    errorMsg.append("是否主账号只能填写是或否;");
                }
            }


            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
            userNames.add(StringUtil.StringValue(entity.getUsername()).trim());
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        userInfoMap = rbacClient.getUserMapByNames(userNames);
        if (!userInfoMap.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                StringBuilder errorMsg = new StringBuilder();
                UserModelDto entity = BeanUtil.mapToBean(data.get(i), UserModelDto.class, true, null);
                if (userInfoMap.containsKey(entity.getUsername())) {
                    errorMsg.append("用户账号重复;");
                }
                if (errorMsg.length() != 0) {
                    errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
                }
            }
        }
    }

    private void checkOrgCateInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        List<String> orgNameList = new ArrayList<>();
        List<String> categoryCodeList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            SiesData row=data.get(i);
            StringBuilder errorMsg = new StringBuilder();
            OrgCategoryModelDTO entity = BeanUtil.mapToBean(data.get(i), OrgCategoryModelDTO.class, true, null);
             if (!checkRedisDataExist(iesTaskId + ID_NUMBER_KEY, String.valueOf(row.get("idNumber")))) {
                 errorMsg.append("该身份证excel基础数据不存在;");
            }
             Object serviceStatusObj=row.get("serviceStatus");
             if(serviceStatusObj!=null){
                 if (StringUtils.isNotEmpty(String.valueOf(serviceStatusObj)) && !checkRedisDataExist(iesTaskId + DICT_KEY,String.valueOf(serviceStatusObj))) {
                     errorMsg.append("该供应商品类状态在系统字典中不存在;");
                 }
             }

            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
            orgNameList.add(entity.getOrgName());
            categoryCodeList.add(entity.getCategoryCode());
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        // 校验组织db
        Map<String, Organization> orgMap = baseClient.getOrganizationsByNames(orgNameList);
        Map<String, PurchaseCategory> categoryCodeMap = baseClient.getCategoryByCodes(categoryCodeList);
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            OrgCategoryModelDTO entity = BeanUtil.mapToBean(data.get(i), OrgCategoryModelDTO.class, true, null);
            if (!orgMap.containsKey(entity.getOrgName())) {
                errorMsg.append("系统不存在该组织名称;");
            }
            if (!categoryCodeMap.containsKey(entity.getCategoryCode())) {
                errorMsg.append("该品类编码不存在;");
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        redisTemplate.opsForHash().putAll(iesTaskId + ORG_ORG_KEY, orgMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + ORG_ORG_KEY, EXPIRE_TIME, TimeUnit.SECONDS);

        redisTemplate.opsForHash().putAll(iesTaskId + ORG_CATE_KEY, categoryCodeMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + ORG_CATE_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    private void checkNpmSerciceCustomInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        List<String> categoryCodeList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            SiesData row=data.get(i);
            Object idNumber=row.get("idNumber");
            if(idNumber!=null){
                if (!checkRedisDataExist(iesTaskId + ID_NUMBER_KEY,idNumber.toString() )) {
                    errorMsg.append("该身份证excel基础数据不存在;");
                }
            }
            Object categoryCode=row.get("categoryCode");
            if(categoryCode!=null){
                String categoryCodeStr=String.valueOf(categoryCode);
                categoryCodeList.add(categoryCodeStr);
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }

        }
        Map<String, PurchaseCategory> categoryCodeMap = baseClient.getCategoryByCodes(categoryCodeList);
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            SiesData row=data.get(i);
            Object categoryCode=row.get("categoryCode");
            Object categoryName=row.get("categoryName");
            if(categoryCode!=null){
                String categoryCodeStr=String.valueOf(categoryCode);
                if (!categoryCodeMap.containsKey(categoryCodeStr)) {
                    errorMsg.append("该品类编码不存在;");
                }else{
                    if(categoryName!=null){
                        String categoryNameStr=categoryCodeMap.get(categoryCodeStr).getCategoryName();
                        if(!String.valueOf(categoryName).equals(categoryNameStr)){
                            errorMsg.append("该品类名称信息不符;");
                        }
                    }
                }
            }

            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        redisTemplate.opsForHash().putAll(iesTaskId + PURCHASE_CATEGORY_KEY, categoryCodeMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + PURCHASE_CATEGORY_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
    }
}
