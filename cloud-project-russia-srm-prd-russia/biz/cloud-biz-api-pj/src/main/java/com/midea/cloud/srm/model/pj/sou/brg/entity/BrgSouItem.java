package com.midea.cloud.srm.model.pj.sou.brg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.formula.entity.MaterialFormulaRelate;
import com.midea.cloud.srm.model.base.formula.entity.PricingFormulaHeader;
import com.midea.cloud.srm.model.extapi.sou.brg.entity.ExtBrgSouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

/**
 * 项目式询价 - 询价需求明细
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@ApiModel("项目需求")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "scc_sou_brg_item", autoResultMap = true)
public class BrgSouItem extends ExtBrgSouItem {
    private static final long serialVersionUID = 1L;

    /** @see SouItem#getSouItemId */
    @ApiModelProperty("ID")
    @TableId("SOU_ITEM_ID")
    private Long souItemId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源模块基础表ID,该表与基础表为一对一关系")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouProject#getOrderType */
    @ApiModelProperty("报价方式(冗余字段)")
    @TableField("ORDER_TYPE")
    private SouOrderTypeEnum orderType;

    @TableField("TARGET_PRICE")
    @ApiModelProperty("拦标价")
    private BigDecimal targetPrice;

    @TableField("DELIVERY_PLACE")
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    @TableField("PRICE_TYPE")
    @ApiModelProperty("价格类型[字典值: PRICE_TYPE]")
    private String priceType;

    @TableField("PURCHASE_TYPE")
    @ApiModelProperty("采购类型")
    private String purchaseType;

    @TableField("TRADE_TERM")
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    @TableField("TRANSPORT_TYPE")
    @ApiModelProperty("运输方式[字典值: TRANSF_TYPE]")
    private String transportType;

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
