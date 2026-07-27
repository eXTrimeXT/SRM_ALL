package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPjInqSouOrderItemInvoiceTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouOrderItem extends BaseEntity<ExtInqSouOrderItem> {

    /** @see ExtPjInqSouOrderItemInvoiceTypeEnum */
    @TableField("INVOICE_TYPE")
    @ApiModelProperty("发票类型(EXT_SOU_INQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    @TableField("PRICE_TAX_TOTAL")
    @ApiModelProperty("价税合计")
    private BigDecimal priceTaxTotal;

    @TableField("ADVANCE_PAYMENT_REMARK")
    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;

    @TableField("SPECIAL_PAYMENT_REMARK")
    @ApiModelProperty("特殊付款说明")
    private String specialPaymentRemark;

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
