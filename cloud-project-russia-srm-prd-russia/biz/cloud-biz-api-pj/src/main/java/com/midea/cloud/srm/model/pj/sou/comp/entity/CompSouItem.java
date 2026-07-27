package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.formula.entity.MaterialFormulaRelate;
import com.midea.cloud.srm.model.base.formula.entity.PricingFormulaHeader;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_sou_item")
@ApiModel("竞价.物料需求")
public class CompSouItem extends ExtCompSouItem {

    @TableId("SOU_ITEM_ID")
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("ORDER_CURRENCY")
    @ApiModelProperty("报价币种(由采购商指定)")
    private String orderCurrency;

    @TableField("TAX_KEY")
    @ApiModelProperty("税率编码(由采购商指定)")
    private String taxKey;

    @TableField("TAX_RATE")
    @ApiModelProperty("税率(由采购商指定)")
    private BigDecimal taxRate;

    @TableField("START_ORDER_NOTAX_PRICE")
    @ApiModelProperty("起拍价(原币未税)")
    private BigDecimal startOrderNotaxPrice;

    @TableField("START_ORDER_TAX_PRICE")
    @ApiModelProperty("起拍价(原币含税)")
    private BigDecimal startOrderTaxPrice;

    @TableField("START_STANDARD_NOTAX_PRICE")
    @ApiModelProperty("起拍价(本币未税)")
    private BigDecimal startStandardNotaxPrice;

    @TableField("START_STANDARD_TAX_PRICE")
    @ApiModelProperty("起拍价(本币未税)")
    private BigDecimal startStandardTaxPrice;

    @TableField("START_STANDARD_GR_NOTAX_PRICE")
    @ApiModelProperty("起拍价(本币未税-组合)")
    private BigDecimal startStandardGroupNotaxPrice;

    @TableField("START_STANDARD_GR_TAX_PRICE")
    @ApiModelProperty("起拍价(本币含税-组合)")
    private BigDecimal startStandardGroupTaxPrice;

    @TableField("ROW_TYPE")
    @ApiModelProperty("行类型")
    private String rowType;

    @TableField("DELIVERY_PLACE")
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    @TableField("PRICE_TYPE")
    @ApiModelProperty("价格类型(DMAND_LINE_TYPE)")
    private String priceType;

    @TableField("PURCHASE_TYPE")
    @ApiModelProperty("采购类型(PURCHASE_TYPE)")
    private String purchaseType;

    @TableField("TRADE_TERM")
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    @TableField("WARRANTY_PERIOD")
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    // ------------------------------------------------- 公式报价相关参数 -------------------------------------------------
    /** @see MaterialFormulaRelate#getRelateId */
    @TableField("MATERIAL_FORMULA_RELATE_ID")
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /**
     * @see MaterialFormulaRelate#getFormulaId
     * @see PricingFormulaHeader#getPricingFormulaHeaderId
     */
    @TableField("FORMULA_ID")
    @ApiModelProperty("公式id")
    private Long formulaId;

    /**
     * @see MaterialFormulaRelate#getFormulaName
     * @see PricingFormulaHeader#getPricingFormulaName
     */
    @TableField("FORMULA_NAME")
    @ApiModelProperty("公式名称")
    private String formulaName;

    /**
     * @see MaterialFormulaRelate#getFormulaValue
     * @see PricingFormulaHeader#getPricingFormulaValue
     */
    @TableField("FORMULA_VALUE")
    @ApiModelProperty("公式值")
    private String formulaValue;

}
