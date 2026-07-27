package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@TableName("scc_sou_item")
@Data
@Slf4j
public class ExtSouItem extends SouItem {

    /**
     * 标段
     */
    private String extBidSection;
    /**
     * 区域
     */
    private String extRegion;
    /**
     * 品牌
     */
    private String extBrand;
    /**
     * 固定未税单价（万元）
     */
    private BigDecimal extFixedPriceNoTax;
    /**
     * 暂定未税总价（万元）
     */
    private BigDecimal extProvPriceSumNoTax;
    /**
     * 固定含税单价（万元）
     */
    private BigDecimal extFixedPriceTax;
    /**
     * 暂定含税总价（万元）
     */
    private BigDecimal extProvPriceSumTax;
    /**
     * 项目特征
     */
    private String extFeature;
    /**
     * 施工内容
     */
    private String extConstructionItem;
    /**
     * 数量/工程量
     */
    private BigDecimal extQuantity;
    /**
     * 未税单价（万元）
     */
    private BigDecimal extPriceNoTax;
    /**
     * 未税总价（万元）
     */
    private BigDecimal extPriceSumNoTax;
    /**
     * 含税单价（万元）
     */
    private BigDecimal extPriceTax;
    /**
     * 含税总价（万元）
     */
    private BigDecimal extPriceSumTax;
    /**
     * 发票类型
     */
    private String extInvoiceType;
    /**
     * 税率（%）
     */
    private BigDecimal extTaxRate;
    /**
     * 币种
     */
    private String extCurrency;
    /**
     * 供应商备注
     */
    private String extSubitem;

    /**
     * 包名
     */
    @ApiModelProperty("包名")
    private String extPackageName;

    public void cleanupVendorFiled(List<ExtSouPriceTemplate> vendorTemplateList) {

        Map<String, Field> fieldMap = Arrays.stream(ExtSouItem.class.getDeclaredFields()).collect(Collectors.toMap(f -> f.getName(), f -> f));
        List<String> fieldNameList = vendorTemplateList.stream().map(ExtSouPriceTemplate::getColumnCode).collect(Collectors.toList());
        fieldNameList.stream().forEach(fieldName -> {
            try {
                Field field = fieldMap.get(fieldName);
                field.setAccessible(true);
                field.set(this, null);
            } catch (Exception e) {
                log.info("cleanupVendorFiled error: " + e.getMessage());
            }
        });

    }

}
