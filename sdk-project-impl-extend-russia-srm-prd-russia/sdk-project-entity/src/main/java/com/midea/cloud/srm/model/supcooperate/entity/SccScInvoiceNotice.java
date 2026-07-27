package com.midea.cloud.srm.model.supcooperate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/13
 */
@Data
@TableName("scc_sc_invoice_notice")
public class SccScInvoiceNotice {

    @TableId("INVOICE_NOTICE_ID")
    @ApiModelProperty("开票通知ID")
    private Long invoiceNoticeId;

    @TableField("ORG_NAME")
    @ApiModelProperty("业务实体名称")
    private String orgName;

    @TableField("INVOICE_NOTICE_NUMBER")
    @ApiModelProperty("对账单号")
    private String invoiceNoticeNumber;

    @TableField("INVOICE_NOTICE_STATUS")
    @ApiModelProperty("单据状态")
    private String invoiceNoticeStatus;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @TableField("CEEA_RECEIVE_START_DATE")
    @ApiModelProperty("对账期间从")
    private Data ceeaReceiveStartDate;

    @TableField("CEEA_RECEIVE_END_DATE")
    @ApiModelProperty("对账期间到")
    private Data ceeaReceiveEndDate;

    @TableField("CEEA_NO_TAX_TOTAL_AMOUNT")
    @ApiModelProperty("未税总额")
    private Double ceeaNoTaxTotalAmount;

    @TableField("CEEA_TOTAL_TAX")
    @ApiModelProperty("总税额")
    private Double ceeaTotalTax;

    @TableField("CEEA_TAX_TOTAL_AMOUNT")
    @ApiModelProperty("含税总额")
    private Double ceeaTaxTotalAmount;

    @TableField("CURRENCY_CODE")
    @ApiModelProperty("币种")
    private String currencyCode;



}
