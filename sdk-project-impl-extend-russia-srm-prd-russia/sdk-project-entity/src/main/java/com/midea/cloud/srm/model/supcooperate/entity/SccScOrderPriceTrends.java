package com.midea.cloud.srm.model.supcooperate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_sc_order_price_trends")
@EqualsAndHashCode(callSuper = true)
public class SccScOrderPriceTrends extends BaseEntity<SccScOrderPriceTrends> {

    @ApiModelProperty("价格趋势id")
    @TableId("PRICE_TRENDS_ID")
    private Long priceTrendsId;

    @ApiModelProperty("物料id")
    @TableField("MATERIAL_ID")
    private Long materialId;

    @ApiModelProperty("物料编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;

    @ApiModelProperty("物料名称")
    @TableField("MATERIAL_NAME")
    private String materialName;

    @ApiModelProperty("物料描述")
    @TableField("MATERIAL_DESCRIBE")
    private String materialDescribe;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("区域")
    @TableField("AREA_CODE")
    private String areaCode;

    @ApiModelProperty("物料区域")
    @TableField(exist = false)
    private String materialArea;

    @ApiModelProperty("未税单价")
    @TableField(exist = false)
    private BigDecimal noTaxPrice;

    @ApiModelProperty("最低价")
    @TableField("MIN_PRICE")
    private BigDecimal minPrice;

    @ApiModelProperty("当前月份")
    @TableField("CURRENT_MONTH_PRICE")
    private BigDecimal currentMonthPrice;

    @ApiModelProperty("减一最低价")
    @TableField("ONE_PRICE")
    private BigDecimal onePrice;

    @ApiModelProperty("减二最低价")
    @TableField("TWO_PRICE")
    private BigDecimal twoPrice;

    @ApiModelProperty("减三最低价")
    @TableField("THREE_PRICE")
    private BigDecimal threePrice;

    @ApiModelProperty("减四最低价")
    @TableField("FOUR_PRICE")
    private BigDecimal fourPrice;

    @ApiModelProperty("减五最低价")
    @TableField("FIVE_PRICE")
    private BigDecimal fivePrice;

    @ApiModelProperty("减六最低价")
    @TableField("SIX_PRICE")
    private BigDecimal sixPrice;

    @ApiModelProperty("减七最低价")
    @TableField("SEVEN_PRICE")
    private BigDecimal sevenPrice;

    @ApiModelProperty("减八最低价")
    @TableField("EIGHT_PRICE")
    private BigDecimal eightPrice;

    @ApiModelProperty("减九最低价")
    @TableField("NINE_PRICE")
    private BigDecimal ninePrice;

    @ApiModelProperty("减十最低价")
    @TableField("TEN_PRICE")
    private BigDecimal tenPrice;

    @ApiModelProperty("减十一最低价")
    @TableField("ELEVEN_PRICE")
    private BigDecimal elevenPrice;

}
