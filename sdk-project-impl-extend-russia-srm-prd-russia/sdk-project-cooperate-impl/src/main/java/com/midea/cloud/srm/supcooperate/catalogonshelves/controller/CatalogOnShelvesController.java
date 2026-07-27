package com.midea.cloud.srm.supcooperate.catalogonshelves.controller;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.ExtCatalogOnShelvesStatusEnum;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Api(value = "catalogOnShelves/ext/", tags = {"采购目录上下架二开"})
@RestController
@RequestMapping("catalogOnShelves/ext/")
public class CatalogOnShelvesController {

    @Autowired
    private QlService qlService;
    @Autowired
    private ExternalMaterialService externalMaterialService;

    @Autowired
    private BaseClient baseClient;

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "List<Record>", name = "records", value = "")
    })
    @ApiOperation(value = "新增采购目录上下架商品", notes = "新增采购目录上下架商品", httpMethod = "POST")
    @PostMapping("/create")
    public List<Serializable> create(@RequestBody List<Record> records) {
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        this.handleData(records);
        return qlService.create("CatalogOnShelves", records);
    }

    @ApiOperation(value = "修改采购目录上下架商品", notes = "修改采购目录上下架商品", httpMethod = "POST")
    @PostMapping("/update")
    public List<Serializable> update(@RequestBody List<Record> records) {
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        this.handleData(records);
        return qlService.save("CatalogOnShelves", records);
    }

    private void handleData(List<Record> records) {

        List<Long> categoryIdList = records.stream().map(c -> c.get(CatalogOnShelves::getCategoryId)).distinct().collect(Collectors.toList());

        List<PurchaseCategory> purchaseCategories = baseClient.listCategoryByIds(categoryIdList);
        Map<Long, PurchaseCategory> purchaseCategoryMap = purchaseCategories.stream().collect(Collectors.toMap(k -> k.getCategoryId(), Function.identity(), (k1, k2) -> k2));

        for (Record record : records) {

            //补充品类结构
            PurchaseCategory purchaseCategory = purchaseCategoryMap.get(record.get(CatalogOnShelves::getCategoryId));
            if(ObjectUtils.allNotNull(purchaseCategory)) {
                record.put(LambdaUtil.getFieldName(CatalogOnShelves::getStruct), purchaseCategory.getStruct());
            }

            if (record.get("extPriceLibraryStatus").equals(AgreementStatusEnums.EXPIRED.getCode())) {
                record.put(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.OFF_SHELVES.toString());
                record.put(CatalogOnShelves::getOffShelvesReason, "协议已失效，商品自动下架");
            } else {
                record.put(CatalogOnShelves::getStatus, record.get("extPriceLibraryStatus").equals(AgreementStatusEnums.EXECUTE.getCode())
                        ? ExtCatalogOnShelvesStatusEnum.UNTIMED_SHELVES.toString()
                        : ExtCatalogOnShelvesStatusEnum.TO_BE_ON_SHELVES.toString());
            }
            record.put("extGoodsName", record.get(CatalogOnShelves::getMaterialName));
            record.set(CatalogOnShelves::getOffShelvesDate,
                    ObjectUtil.isEmpty(record.get(CatalogOnShelves::getOffShelvesDate)) && ObjectUtil.isNotEmpty(record.get(CatalogOnShelves::getExpirationDate))
                            ? record.get(CatalogOnShelves::getExpirationDate)
                            : record.get(CatalogOnShelves::getOffShelvesDate));
            //判断是否是关联外部商品
            ExternalMaterial externalMaterial = externalMaterialService.getOne(new LambdaQueryWrapper<ExternalMaterial>()
                    .eq(ExternalMaterial::getMaterialCode, record.get(CatalogOnShelves::getMaterialCode)), false);
            record.put("extMaterialType", externalMaterial == null ? MallTypeEnum.CC.getCode() : externalMaterial.getMaterialType());
        }
    }
}
