package com.midea.cloud.srm.model.sou.agreement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采协议阶梯价")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_tiered_pricing")
public class SccSouTieredPricing extends BaseEntity<SccSouTieredPricing> {

    @ApiModelProperty("阶梯价id")
    @TableId("TIERED_PRICING_ID")
    private Long tieredPricingId;

    @ApiModelProperty("协议信息id")
    @TableField("AGREEMENT_INFO_ID")
    private Long agreementInfoId;

    @ApiModelProperty("行号")
    @TableField(exist = false)
    private Integer lineNum;

    @ApiModelProperty("数量从")
    @TableField("MORE_NUM")
    private Integer moreNum;

    @ApiModelProperty("数量到")
    @TableField("LESS_NUM")
    private Integer lessNum;

    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;

    @ApiModelProperty("未税单价")
    @TableField("PRICE_TAX")
    private BigDecimal priceTax;

    @ApiModelProperty("含税单价")
    @TableField("RATE_PRICE")
    private BigDecimal ratePrice;

    @ApiModelProperty("参考价")
    @TableField("REFER_PRICE")
    private BigDecimal referPrice;
}
