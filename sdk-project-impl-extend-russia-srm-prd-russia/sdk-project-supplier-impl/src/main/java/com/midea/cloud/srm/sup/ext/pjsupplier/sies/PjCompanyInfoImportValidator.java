package com.midea.cloud.srm.sup.ext.pjsupplier.sies;

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
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.StringUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlDispatcher;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.base.region.dto.AreaDTO;
import com.midea.cloud.srm.model.base.region.dto.AreaPramDTO;
import com.midea.cloud.srm.model.base.region.entity.Region;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.common.enums.SupplierTypeEnum;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.info.dto.*;
import com.midea.cloud.srm.model.supplier.info.entity.BankInfo;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.FinanceInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.sies.pojo.*;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
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

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 绿色通道导入*
 *
 * @author luxc18
 * @description 绿色通道导入校验-公司
 * @date 2023/10/10 10:21
 */
@Component
@Slf4j
public class PjCompanyInfoImportValidator extends AbstractImportValidator {
    @Autowired
    private BaseClient baseClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ICompanyInfoService companyInfoService;

    @Autowired
    private RbacClient rbacClient;

    public static final String COMPANY_NAME_KEY = "-companyName";
    public static final String DICT_KEY = "-dict";
    public static final String CURRENY_KEY = "-curreny";
    public static final String PROVINCE_KEY = "-province";
    public static final String CITY_KEY = "-city";
    public static final String ORG_ORG_KEY = "-orgCateOrg";
    public static final String ORG_CATE_KEY = "-orgCateCate";
    public static final String SERVICE_CUSTOM_CATE_KEY = "-serviceCustomCate";
    public static final long EXPIRE_TIME = 600L;

    private final int TWO = 2;
    private final int THREE = 3;
    private final int FOUR = 4;
    private final int FIVE = 5;
    private final int SIX = 6;

    /**
     * 需要按sheetNo来判断走方法*
     *
     * @param iesTaskId
     * @param param
     * @param curMediator
     * @param sheetNo
     * @param sheetName
     * @param batchNo
     * @param data
     * @return
     */
    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        List<ImportResultModel.ErrorRow> errorRows = new ArrayList<>();
        // 1.非空校验
        if (sheetNo == 0) {
            // 基础数据校验准备,存redis数据传递
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
            // 公司规模
            checkCompanySize(iesTaskId, data, errorRows);
        } else if (sheetNo == SIX) {
            // 服务范围
            checkServiceCustom(iesTaskId, data, errorRows);
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
                //国家
                "country",
                //二开-供应商状态
                "PJ_COMPANY_STATUS",
                //供应商类型
                "SUPPLIER_TYPE"
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
        Map<String, String> companyNameMap = new HashMap<>(16);
        Map<String, String> lcCodeMap = new HashMap<>(16);
        for (int i = 0; i < data.size(); i++) {
            SiesData siesData = data.get(i);
            // 1. 非空校验
            StringBuilder errorMsg = new StringBuilder();
            CompanyInfoModelDTO entity = BeanUtil.mapToBean(siesData, CompanyInfoModelDTO.class, true, null);
            if (StringUtils.isNotEmpty(entity.getOverseasRelation()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getOverseasRelation())) {
                errorMsg.append("系统不存在该境内外关系字典码;");
            }
            if (StringUtils.isNotEmpty(entity.getRegistCurrencyName()) && !checkRedisDataExist(iesTaskId + CURRENY_KEY, entity.getRegistCurrencyName())) {
                errorMsg.append("系统不存在该币种;");
            }
            if (StringUtils.isNotEmpty(entity.getCompanyCreationDate())) {
                try {
                    Date date = DateUtil.parseDate(entity.getCompanyCreationDate());
                } catch (Exception e) {
                    errorMsg.append("成立日期转换格式有误;");
                }
            }

            if (StringUtils.isNotEmpty(entity.getBusinessStartDate())) {
                try {
                    Date date = DateUtil.parseDate(entity.getBusinessStartDate());
                } catch (Exception e) {
                    errorMsg.append("营业日期从格式转换有误;");
                }
            }
            if (StringUtils.isNotEmpty(entity.getBusinessEndDate())) {
                try {
                    Date date = DateUtil.parseDate(entity.getBusinessEndDate());
                } catch (Exception e) {
                    errorMsg.append("营业结束日期格式转换有误;");
                }
            }
            // 2.db校验
            if (StringUtils.isNotEmpty(entity.getCeeaBusinessModel()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getCeeaBusinessModel())) {
                errorMsg.append("系统不存在该商业模式字典码;");
            }
            if (StringUtils.isNotEmpty(entity.getCeeaSupBusinessType()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getCeeaSupBusinessType())) {
                errorMsg.append("系统不存在该供应商业务类型字典码;");
            }
            if (StringUtils.isNotEmpty(entity.getCompanyCountry()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getCompanyCountry())) {
                errorMsg.append("系统不存在国家字典码;");
            }
            if (StringUtils.isNotEmpty((String) siesData.get("groupCountry")) && !checkRedisDataExist(iesTaskId + DICT_KEY, (String) siesData.get("groupCountry"))) {
                errorMsg.append("系统不存在集团所属国家字典码;");
            }
            if (StringUtils.isNotEmpty((String) siesData.get("pjCompanyStatus")) && !checkRedisDataExist(iesTaskId + DICT_KEY, (String) siesData.get("pjCompanyStatus"))) {
                errorMsg.append("系统不存在供应商状态字典码;");
            }
            if (StringUtils.isNotEmpty(entity.getCompanyCode())) {
                if (companyCodeMap.containsKey(entity.getCompanyCode())) {
                    errorMsg.append("供应商编码excel中已重复,请检查;");
                } else {
                    companyCodeMap.put(entity.getCompanyCode(), entity.getCompanyCode());
                }
            }
            if (companyNameMap.containsKey(entity.getCompanyName())) {
                errorMsg.append("供应商名称excel中已重复,请检查;");
            } else if (StringUtils.isNotEmpty(entity.getCompanyName())) {
                companyNameMap.put(entity.getCompanyName(), entity.getCompanyName());
            }
            if (lcCodeMap.containsKey(entity.getLcCode())) {
                errorMsg.append("统一社会信用代码excel中已重复,请检查;");
            } else {
                lcCodeMap.put(entity.getLcCode(), entity.getLcCode());
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        // 3.批量校验供应商编码,名称,社会统一编码 DB中是否存在
        Map<String, String> dbCompanyCodeMap = new HashMap<>(16);
        Map<String, String> dbCompanyNameMap = new HashMap<>(16);
        Map<String, String> dbLcCodeMap = new HashMap<>(16);
        if (!companyCodeMap.isEmpty()) {
            List<CompanyInfo> dbCompanyList = companyInfoService.list(Wrappers.lambdaQuery(CompanyInfo.class)
                    .select(CompanyInfo::getCompanyCode)
                    .in(CompanyInfo::getCompanyCode, companyCodeMap.values()));
            dbCompanyCodeMap = dbCompanyList.stream().collect(Collectors.toMap(CompanyInfo::getCompanyCode, CompanyInfo::getCompanyCode));
        }

        if (!lcCodeMap.isEmpty()) {
            List<CompanyInfo> dbLcCodeCompanyList = companyInfoService.list(Wrappers.lambdaQuery(CompanyInfo.class)
                    .select(CompanyInfo::getLcCode)
                    .in(CompanyInfo::getLcCode, lcCodeMap.values()));
            dbLcCodeMap = dbLcCodeCompanyList.stream().collect(Collectors.toMap(CompanyInfo::getLcCode, CompanyInfo::getLcCode));
        }

        if (!companyNameMap.isEmpty()) {
            List<CompanyInfo> dbCompanyNameCompanyList = companyInfoService.list(Wrappers.lambdaQuery(CompanyInfo.class)
                    .select(CompanyInfo::getCompanyName)
                    .in(CompanyInfo::getCompanyName, companyNameMap.values()));
            dbCompanyNameMap = dbCompanyNameCompanyList.stream().collect(Collectors.toMap(CompanyInfo::getCompanyName, CompanyInfo::getCompanyName));
        }

        // 有重复db数据,返回错误信息
        if (!dbCompanyCodeMap.isEmpty() || !dbCompanyNameMap.isEmpty() || !dbLcCodeMap.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                // 1. 非空校验
                StringBuilder errorMsg = new StringBuilder();
                CompanyInfoModelDTO entity = BeanUtil.mapToBean(data.get(i), CompanyInfoModelDTO.class, true, null);
                if (dbCompanyNameMap.containsKey(entity.getCompanyName())) {
                    errorMsg.append("供应商名称数据库中已存在,请检查;");
                }
                if (dbCompanyCodeMap.containsKey(entity.getCompanyCode())) {
                    errorMsg.append("供应商编码数据库中已存在,请检查;");
                }
                if (dbLcCodeMap.containsKey(entity.getLcCode())) {
                    errorMsg.append("统一社会信用代码数据库中已存在,请检查;");
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
        if (!companyNameMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(iesTaskId + COMPANY_NAME_KEY, companyNameMap);
            redisTemplate.opsForHash().getOperations().expire(iesTaskId + COMPANY_NAME_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }


    private void checkContactInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            ContactInfoModelDTO entity = BeanUtil.mapToBean(data.get(i), ContactInfoModelDTO.class, true, null);
            if (!checkRedisDataExist(iesTaskId + COMPANY_NAME_KEY, entity.getCompanyName())) {
                errorMsg.append("该供应商不存在;");
            }
            if (StringUtils.isNotEmpty(entity.getCeeaGender()) && !checkRedisDataExist(iesTaskId + DICT_KEY, entity.getCeeaGender())) {
                errorMsg.append("性别不存在字典编码;");
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
    }

    private void checkBankInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            BankInfoModelDTO entity = BeanUtil.mapToBean(data.get(i), BankInfoModelDTO.class, true, null);
            if (!checkRedisDataExist(iesTaskId + COMPANY_NAME_KEY, entity.getCompanyName())) {
                errorMsg.append("该供应商不存在;");
            }
            if (StringUtils.isNotEmpty(entity.getCurrencyName()) && !checkRedisDataExist(iesTaskId + CURRENY_KEY, entity.getCurrencyName())) {
                errorMsg.append("该币种在系统中不存在;");
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
    }

    private void checkUserInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        Set<String> userNames = new HashSet<>();
        Map<String, User> userInfoMap = new HashMap<>(16);
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            UserModelDto entity = BeanUtil.mapToBean(data.get(i), UserModelDto.class, true, null);

            if (!checkRedisDataExist(iesTaskId + COMPANY_NAME_KEY, entity.getCompanyName())) {
                errorMsg.append("该供应商不存在;");
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
                    errorMsg.append("用户名重复;");
                }
                if (errorMsg.length() != 0) {
                    errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
                }
            }
        }
    }

    private void checkOrgCateInfo(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        // todo 不能重复db和excel
        List<String> orgNameList = new ArrayList<>();
        List<String> categoryCodeList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            OrgCategoryModelDTO entity = BeanUtil.mapToBean(data.get(i), OrgCategoryModelDTO.class, true, null);
            if (!checkRedisDataExist(iesTaskId + COMPANY_NAME_KEY, entity.getCompanyName())) {
                errorMsg.append("该供应商不存在;");
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

    private void checkCompanySize(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            String companyName = (String) data.get(i).get("companyName");
            if (!checkRedisDataExist(iesTaskId + COMPANY_NAME_KEY, companyName)) {
                errorMsg.append("该供应商不存在;");
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
    }

    private void checkServiceCustom(String iesTaskId, List<SiesData> data, List<ImportResultModel.ErrorRow> errorRows) {
        List<String> categoryCodeList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            String companyName = (String) data.get(i).get("companyName");
            String categoryCode = (String) data.get(i).get("categoryCode");
            categoryCodeList.add(categoryCode);
            if (!checkRedisDataExist(iesTaskId + COMPANY_NAME_KEY, companyName)) {
                errorMsg.append("该供应商不存在;");
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        Map<String, PurchaseCategory> categoryCodeMap = baseClient.getCategoryByCodes(categoryCodeList);
        for (int i = 0; i < data.size(); i++) {
            StringBuilder errorMsg = new StringBuilder();
            String categoryCode = (String) data.get(i).get("categoryCode");
            if (!categoryCodeMap.containsKey(categoryCode)) {
                errorMsg.append("该品类编码不存在;");
            }
            if (errorMsg.length() != 0) {
                errorRows.add(new ImportResultModel.ErrorRow(i, errorMsg.toString()));
            }
        }
        if (!errorRows.isEmpty()) {
            return;
        }
        redisTemplate.opsForHash().putAll(iesTaskId + SERVICE_CUSTOM_CATE_KEY, categoryCodeMap);
        redisTemplate.opsForHash().getOperations().expire(iesTaskId + SERVICE_CUSTOM_CATE_KEY, EXPIRE_TIME, TimeUnit.SECONDS);
    }
}
