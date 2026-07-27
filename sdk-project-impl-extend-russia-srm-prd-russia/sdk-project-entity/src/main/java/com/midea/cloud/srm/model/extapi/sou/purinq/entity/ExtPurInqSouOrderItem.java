package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouOrderItemInvoiceTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
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
@TableName("scc_npm_sou_purinq_order_item")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouOrderItem extends BaseEntity<ExtPurInqSouOrderItem> {

    /** @see SouOrderItem#getOrderItemId */
    @TableId("ORDER_ITEM_ID")
    @ApiModelProperty("寻源核心-供应商报价行ID")
    private Long orderItemId;

    /** @see SouOrderItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("询价单ID")
    private Long projectId;

    /** @see SouOrderItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    /** @see SouOrderItem#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see ExtPurInqSouOrderItemInvoiceTypeEnum */
    @TableField("INVOICE_TYPE")
    @ApiModelProperty("发票类型(EXT_SOU_PURINQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    @TableField("PRICE_TAX_TOTAL")
    @ApiModelProperty("价税合计")
    private BigDecimal priceTaxTotal;

    @TableField("EXT_LEAD_TIME")
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    @TableField("EXT_WARRANTY_PERIOD")
    @ApiModelProperty("保修期(保质期)")
    private Integer extWarrantyPeriod;

    @TableField("EXT_WIN_REASON")
    @ApiModelProperty("中标原因")
    private String extWinReason;

    @TableField("HAS_FIX_PRICE")
    @ApiModelProperty("是否已定价")
    private Enable hasFixPrice;

    /** @see ExtFixPriceHead#getFixPriceHeadId */
    @TableField("EXT_FIX_PRICE_HEAD_ID")
    @ApiModelProperty("定价单ID")
    private Long extFixPriceHeadId;

    /** @see ExtFixPriceHead#getFixPriceNo */
    @TableField("EXT_FIX_PRICE_NO")
    @ApiModelProperty("定价单号")
    private String extFixPriceNo;

    /** @see ExtFixPriceLine#getFixPriceLineId */
    @TableField("EXT_FIX_PRICE_LINE_ID")
    @ApiModelProperty("定价单行ID")
    private Long extFixPriceLineId;

    @TableField("LATEST_PRICE_TAG")
    @ApiModelProperty("是否最新报价")
    private Enable latestPriceTag;

}
