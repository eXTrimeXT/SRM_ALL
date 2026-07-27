package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("查看比价")
public class ApiExtCompareVendorPriceDto extends BaseObjectX {

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * 含税单价（万元）
     */
    @ApiModelProperty("含税单价（万元）")
    private BigDecimal extPriceTax;

    /**
     * 含税总价（万元）
     */
    @ApiModelProperty("含税总价（万元）")
    private BigDecimal extPriceSumTax;

    /**
     * 税率（%）
     */
    @ApiModelProperty("税率（%）")
    private BigDecimal extTaxRate;

    /**
     * 发票类型
     */
    @ApiModelProperty("发票类型")
    private String extInvoiceType;

    /**
     * 暂定未税总价（万元）
     */
    @ApiModelProperty("未税暂定总价")
    private BigDecimal extProvPriceSumNoTax;

    /**
     * 暂定含税总价（万元）
     */
    @ApiModelProperty("含税暂定总价")
    private BigDecimal extProvPriceSumTax;

    /**
     * 暂定未税总价（万元）
     */
    @ApiModelProperty("未税暂定总价-按包名小计")
    private BigDecimal extPacknameProvPriceSumNoTax;

    /**
     * 暂定含税总价（万元）
     */
    @ApiModelProperty("含税暂定总价-按包名小计")
    private BigDecimal extPacknameProvPriceSumTax;

    @ApiModelProperty("汇总含税暂定总价")
    private BigDecimal extTotalProvPriceSumTax;

    @ApiModelProperty("汇总未税暂定总价")
    private BigDecimal extTotalProvPriceSumNoTax;
}
