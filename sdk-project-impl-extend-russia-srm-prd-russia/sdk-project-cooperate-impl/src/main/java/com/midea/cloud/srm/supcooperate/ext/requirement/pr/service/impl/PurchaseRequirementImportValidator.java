package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.NumberUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.material.dto.MaterialItemQueryDto;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.Site;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.objectx.dto.ConditionDTO;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.mideacloud.common.orm.Condition;
import com.mideacloud.material.dto.request.MtpartUpdateRequestDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zenghx2
 */
@Component
public class PurchaseRequirementImportValidator extends AbstractImportValidator {

    @Autowired
    private BaseClient baseClient;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private QlOpenClient qlOpenClient;

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> data) {
        SiesImportResult importResult = new SiesImportResult();
        PurchaseRequirementHeadDTO head = param.getFileupload().getX("head");
        for (int i = 0; i < data.size(); i++) {
            SiesData row = data.get(i);
            StringBuilder sb = new StringBuilder();
            Object orderQty = row.get(LambdaUtil.getFieldName(RequirementLine::getRequirementQuantity));
            if (!NumberUtil.isNumber(orderQty)) {
                sb.append("需求数量需为数字类型;");
            }
            if (!DateUtil.isDate(row.get(LambdaUtil.getFieldName(RequirementLine::getRequirementDate)))) {
                sb.append("需求日期需为正确的日期格式;");
            }
            Object price = row.get(LambdaUtil.getFieldName(PurchaseRequirementLineDTO::getExtPredictPrice));
            if (null != price && !NumberUtil.isNumber(price)) {
                sb.append("预估含税单价需为数字类型;");
            }
            if (sb.length() > 0) {
                importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
            }
        }
        if (!importResult.getErrorRowList().isEmpty()) {
            return importResult;
        }

        // 物料信息
        Map<String, MaterialItem> materialMap = getMaterialMap(data);
        Map<String, MaterialItem> invalidMaterialMap = getInvalidMaterialMap(data);
        // 采购品类
        Map<String, PurchaseCategory> categoryMap = getCategoryMap(data);
        // 使用人员
        Map<String, String> userMap = getUserMap(data);
        // 使用部门
        Map<String, Organization> orgMap = getOrgMap(data);
        // 采购单位
        Map<String, String> unitMap = getUnitMap();

        for (int i = 0; i < data.size(); i++) {
            SiesData row = data.get(i);

            row.set(RequirementLine::getRequirementDate, formatDateString(row.getString(LambdaUtil.getFieldName(RequirementLine::getRequirementDate))));
            StringBuilder sb = new StringBuilder();

            // 物料
            String materialCode = row.get(PurchaseRequirementLineDTO::getMaterialCode);
            MaterialItem material = null;
            if (StringUtils.isNotBlank(materialCode)) {
                material = materialMap.get(materialCode);
                if (null == material) {
                    sb.append("物资编码不存在");
                    importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                    continue;
                }
                if (null != invalidMaterialMap.get(materialCode)) {
                    sb.append("物资编码已禁用，请在物料查询中选取生效编码");
                    importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                    continue;
                }
                row.put(PurchaseRequirementLineDTO::getMaterialId, material.getMaterialId());
                row.put(PurchaseRequirementLineDTO::getMaterialCode, material.getMaterialCode());
                row.put(PurchaseRequirementLineDTO::getMaterialName, material.getMaterialName());
                row.put(PurchaseRequirementLineDTO::getUnitCode, material.getUnit());
                row.put(PurchaseRequirementLineDTO::getUnit, material.getUnitName());
                row.put(PurchaseRequirementLineDTO::getCategoryId, material.getCategoryId());
                row.put(PurchaseRequirementLineDTO::getCategoryCode, material.getCategoryCode());
                row.put(PurchaseRequirementLineDTO::getCategoryName, material.getCategoryFullName());
                row.put(PurchaseRequirementLineDTO::getExtMaterialModel, material.getMaterialType());
                if (StringUtils.isBlank(row.getString(LambdaUtil.getFieldName(PurchaseRequirementLineDTO::getBrand)))) {
                    row.put(PurchaseRequirementLineDTO::getBrand, material.getBrand());
                }
                row.put(PurchaseRequirementLineDTO::getExtProductFlag, YesOrNo.NO.getValue());
            }

            // 品类
            if (material == null) {
                String categoryCode = row.get(PurchaseRequirementLineDTO::getCategoryCode);
                if (StringUtils.isBlank(categoryCode)) {
                    sb.append("采购品类编码不能为空");
                    importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                    continue;
                }

                PurchaseCategory category = categoryMap.get(categoryCode);
                if (null == category) {
                    sb.append("采购品类编码不存在");
                    importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                    continue;
                }

                row.put(PurchaseRequirementLineDTO::getCategoryId, category.getCategoryId());
                row.put(PurchaseRequirementLineDTO::getCategoryCode, category.getCategoryCode());
                row.put(PurchaseRequirementLineDTO::getCategoryName, category.getCategoryFullName());
                row.put(PurchaseRequirementLineDTO::getExtProductFlag, YesOrNo.NO.getValue());
            }

            // 使用部门
            String departCode = row.get(PurchaseRequirementLineDTO::getExtUseDepartmentCode);
            if (StringUtils.isBlank(departCode)) {
                sb.append("使用部门编码不能为空");
                importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                continue;
            }
            Organization organization = orgMap.get(departCode);
            if (null == organization) {
                sb.append("使用部门不存在");
                importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                continue;
            }
            row.put(PurchaseRequirementLineDTO::getExtUseDepartmentId, organization.getOrganizationId());
            row.put(PurchaseRequirementLineDTO::getExtUseDepartmentName, organization.getOrganizationName());

            // 使用人
            String userCode = row.get(PurchaseRequirementLineDTO::getExtUserCode);
            if (StringUtils.isBlank(userCode)) {
                sb.append("使用人工号不能为空");
                importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                continue;
            }
            String nickname = userMap.get(userCode);
            if (null == nickname) {
                sb.append("使用人工号不存在");
                importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                continue;
            }
            row.put(PurchaseRequirementLineDTO::getExtUserName, nickname);

            // 单位
            String unitName = row.get(PurchaseRequirementLineDTO::getUnit);
            if (StringUtils.isNotBlank(unitName)) {
                String unitCode = unitMap.get(unitName);
                if (unitCode == null) {
                    sb.append("单位不存在");
                    importResult.addErrorRow(new ImportResultModel.ErrorRow(i, sb.toString()));
                    continue;
                }
                row.put(PurchaseRequirementLineDTO::getUnitCode, unitCode);
            }

            importResult.addSuccessRow(new SiesImportResult.SuccessRow(row, false));
        }

        // 设置地址区域
        Map<Long, String> orgIdCodeMap = orgMap.values().stream().collect(Collectors.toMap(e -> e.getOrganizationId(), e -> e.getOrganizationCode()));
        Map<String, Record> orgAddrMap = new HashMap<>(15);
        orgIdCodeMap.keySet().forEach(e -> {
            List<Record> addrs = baseExtClient.getOrgAddress(e);
            if (CollectionUtils.isEmpty(addrs)) {
                return;
            }
            List<Record> defaults = addrs.stream().filter(e1 -> YesOrNo.YES.getValue().equals(e1.get("isDefault"))).collect(Collectors.toList());
            Record addr = CollectionUtils.isNotEmpty(defaults)?defaults.get(0):addrs.get(0);
            orgAddrMap.put(orgIdCodeMap.get(e), addr);
        });
        List<SiesData> successList = importResult.getSuccessRowList().stream().map(e -> (SiesData) e.getApplyData()).collect(Collectors.toList());
        successList.forEach(row -> {
            Record addr = orgAddrMap.get(row.get(PurchaseRequirementLineDTO::getExtUseDepartmentCode));
            if (addr != null) {
                row.put(PurchaseRequirementLineDTO::getExtAreaCode, addr.get("addressRegion"));
                row.put(PurchaseRequirementLineDTO::getExtReceiver, addr.get(Site::getReceiver));
                row.put(PurchaseRequirementLineDTO::getReceiveAddress, addr.get(Site::getSiteName));
                row.put(PurchaseRequirementLineDTO::getReceiveTelephone, addr.get(Site::getReceiverPhone));
            }
        });

        // 查询商品信息
        setProductInfo(head.getOrgId(), successList);

        // 设置预估价格
        successList.forEach(row -> {
            // 计算预估总价
            BigDecimal extPredictPrice = row.get(PurchaseRequirementLineDTO::getExtPredictPrice);
            BigDecimal reqQty = row.get(PurchaseRequirementLineDTO::getExtPredictAmount);
            if (row.get(PurchaseRequirementLineDTO::getExtPredictAmount) == null) {
                row.put(PurchaseRequirementLineDTO::getExtPredictAmount, BigDecimalUtil.mul(extPredictPrice, reqQty));
            }
        });

        return importResult;
    }

    private Map<String, MaterialItem> getMaterialMap(List<SiesData> data) {
        List<String> materialCodes = getSiesDataCode(data);
        if (CollectionUtils.isEmpty(materialCodes)) {
            return Collections.emptyMap();
        }
        List<MaterialItem> list = baseClient.listMaterialByCodeBatch(materialCodes);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(m -> m.getMaterialCode(), m -> m, (v1, v2) -> v1));
    }

    private Map<String, MaterialItem> getInvalidMaterialMap(List<SiesData> data) {
        List<String> materialCodes = getSiesDataCode(data);
        if (CollectionUtils.isEmpty(materialCodes)) {
            return Collections.emptyMap();
        }
        MaterialItemQueryDto materialItemQueryDto = new MaterialItemQueryDto();
        materialItemQueryDto.setPageNum(1);
        materialItemQueryDto.setPageSize(materialCodes.size());
        materialItemQueryDto.setItemStatus("N");
        List<ConditionDTO> conditionDTOS = new ArrayList<>();
        ConditionDTO conditionDTO  = new ConditionDTO();
        conditionDTO.setField("objectCode");
        conditionDTO.setOperator("in");
        conditionDTO.setValue(materialCodes);
        conditionDTOS.add(conditionDTO);
        materialItemQueryDto.setExtendConditions(conditionDTOS);

        List<MaterialItem> list = baseClient.listPageByCondition(materialItemQueryDto).getList();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(m -> m.getMaterialCode(), m -> m, (v1, v2) -> v1));
    }

    private List<String> getSiesDataCode(List<SiesData> data) {
        return data.stream().filter(e -> StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getMaterialCode)))
                    .map(e -> e.get(PurchaseRequirementLineDTO::getMaterialCode)).distinct().collect(Collectors.toList());
    }

    private void setProductInfo(Long orgId, List<SiesData> data) {
        if (CollectionUtils.isEmpty(data) || orgId == null) {
            return;
        }

        List<String> materialCodes = data.stream().filter(e -> StringUtils.isNotBlank(e.get(RequirementLine::getMaterialCode))
                && StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getExtAreaCode))).map(e -> e.get(RequirementLine::getMaterialCode)).distinct().collect(Collectors.toList());
        List<String> areaCodes = data.stream().filter(e -> StringUtils.isNotBlank(e.get(RequirementLine::getMaterialCode))
                && StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getExtAreaCode))).map(e -> e.get(PurchaseRequirementLineDTO::getExtAreaCode)).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(materialCodes) || CollectionUtils.isEmpty(areaCodes)) {
            return;
        }

        Map<String, List<Record>> productMap = qlService.queryByWrapper(QlWrappers.query("CatalogOnShelves")
                        .contains("extOrgIdList", orgId)
                        .eq(CatalogOnShelves::getStatus, "ON_SHELVES")
                        .in("extAreaCode", areaCodes)
                        .in(CatalogOnShelves::getMaterialCode, materialCodes), Record.class)
                .stream().collect(Collectors.groupingBy(e -> e.get(CatalogOnShelves::getMaterialCode) + "-" + e.get("extAreaCode")));
        if (MapUtils.isEmpty(productMap)) {
            return;
        }

        data.stream().filter(e -> StringUtils.isNotBlank(e.get(RequirementLine::getMaterialCode))
                && StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getExtAreaCode))).forEach(e -> {
            List<Record> records = productMap.get(e.get(PurchaseRequirementLineDTO::getMaterialCode) + "-" + e.get(PurchaseRequirementLineDTO::getExtAreaCode));
            // 存在多個就無效
            if (CollectionUtils.isNotEmpty(records) && records.size() == 1) {
                e.put(PurchaseRequirementLineDTO::getExtProductFlag, YesOrNo.YES.getValue());
                e.put(PurchaseRequirementLineDTO::getExtPredictPrice, records.get(0).get("extReferencePrice"));
            }
        });
    }

//    private Map<String, BigDecimal> getStockMap(Collection<String> materialCodes) {
//        if (CollectionUtils.isEmpty(materialCodes)) {
//            return Collections.emptyMap();
//        }
//
//        return qlService.queryByWrapper(QlWrappers.query("PrShareStock")
//                .select(PrShareStock::getMaterialCode)
//                .select(QlQueryFieldWrapper.sum(PrShareStock::getQty))
//                .in(PrShareStock::getMaterialCode, materialCodes).groupBy(PrShareStock::getMaterialCode), Record.class)
//                .stream().collect(Collectors.toMap(e -> e.get(PrShareStock::getMaterialCode), e -> e.get(PrShareStock::getQty), (v1, v2) -> v1));
//    }

    private Map<String, String> getUserMap(List<SiesData> data) {
        List<String> codes = data.stream().filter(e -> StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getExtUserCode)))
                .map(e -> e.get(PurchaseRequirementLineDTO::getExtUserCode)).distinct().collect(Collectors.toList());

        List<User> users = rbacClient.listUsersByUsersParamCode(codes);
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyMap();
        }
        return users.stream().collect(Collectors.toMap(e -> e.getCeeaEmpNo(), e -> e.getNickname()));
    }

    private Map<String, Organization> getOrgMap(List<SiesData> data) {
        List<String> codes = data.stream().filter(e -> StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getExtUseDepartmentCode)))
                .map(e -> e.get(PurchaseRequirementLineDTO::getExtUseDepartmentCode)).distinct().collect(Collectors.toList());

        Map<String, Organization> orgMap = baseClient.getOrganizationsByCodes(codes);
        if (MapUtils.isEmpty(orgMap)) {
            return Collections.emptyMap();
        }
        return orgMap;
    }

    private Map<String, PurchaseCategory> getCategoryMap(List<SiesData> data) {
        List<String> codes = data.stream().filter(e -> StringUtils.isNotBlank(e.get(PurchaseRequirementLineDTO::getCategoryCode))
                        && StringUtils.isBlank(e.get(PurchaseRequirementLineDTO::getMaterialCode)))
                .map(e -> e.get(PurchaseRequirementLineDTO::getCategoryCode)).distinct().collect(Collectors.toList());

        List<PurchaseCategory> purchaseCategories = baseClient.listPurchaseCategoryByCodes(codes);
        if (CollectionUtils.isEmpty(purchaseCategories)) {
            return Collections.emptyMap();
        }
        return purchaseCategories.stream().collect(Collectors.toMap(e -> e.getCategoryCode(), e -> e));
    }

    private Map<String, String> getUnitMap() {
        return qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(PurchaseUnit.class)
                        .eq(PurchaseUnit::getEnabled, YesOrNo.YES.getValue()), PurchaseUnit.class)
                .stream().collect(Collectors.toMap(e -> e.getUnitName(), e -> e.getUnitCode(), (v1, v2) -> v1));
    }

    private String formatDateString(String dateStr){
        try {
            return DateUtil.format(DateUtil.parseDate(dateStr, DateUtil.DATE_FORMAT_9_2_FORWARD_SLASH), DateUtil.DATE_FORMAT_10);
        } catch (ParseException e) {
            return dateStr;
        }
    }

}
