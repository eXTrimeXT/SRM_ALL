package com.midea.cloud.srm.model.supcooperate.excel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/11
 */
@Data
@TableName("scc_sc_invoice_notice_detail")
public class InvoiceNoticeDetailVo {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键，对账明细ID")
    @TableId("INVOICE_DETAIL_ID")
    private Long invoiceDetailId;
    @ApiModelProperty("对账单ID")
    @TableField("INVOICE_NOTICE_ID")
    private Long invoiceNoticeId;
    @ApiModelProperty("对账明细行号")
    @TableField("INVOICE_DETAIL_NUM")
    private Integer invoiceDetailNum;
    @ApiModelProperty("入库/退货明细id")
    @TableField("WAREHOUSING_RETURN_DETAIL_ID")
    private Long warehousingReturnDetailId;
    @ApiModelProperty("事务处理类型")
    @TableField(
            value = "TYPE",
            keepGlobalFormat = true
    )
    private String type;
    @ApiModelProperty("接收单号")
    @TableField("RECEIVE_ORDER_NO")
    private String receiveOrderNo;
    @ApiModelProperty("接收行号")
    @TableField("RECEIVE_ORDER_LINE_NO")
    private Integer receiveOrderLineNo;
    @ApiModelProperty("业务实体ID")
    @TableField("ORG_ID")
    private Long orgId;
    @ApiModelProperty("业务实体编码")
    @TableField("ORG_CODE")
    private String orgCode;
    @ApiModelProperty("业务实体名称")
    @TableField("ORG_NAME")
    private String orgName;
    @ApiModelProperty("库存组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;
    @ApiModelProperty("库存组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;
    @ApiModelProperty("库存组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;
    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;
    @ApiModelProperty("物料小类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;
    @ApiModelProperty("接收日期")
    @TableField("RECEIVE_DATE")
    private Date receiveDate;
    @ApiModelProperty("物料小类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;
    @ApiModelProperty("物料小类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;
    @ApiModelProperty("物料ID")
    @TableField("ITEM_ID")
    private Long itemId;
    @ApiModelProperty("物料编码")
    @TableField("ITEM_CODE")
    private String itemCode;
    @ApiModelProperty("物料名称")
    @TableField("ITEM_NAME")
    private String itemName;
    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;
    @ApiModelProperty("单价（含税）")
    @TableField("UNIT_PRICE_CONTAINING_TAX")
    private BigDecimal unitPriceContainingTax;
    @ApiModelProperty("单价（不含税）")
    @TableField("UNIT_PRICE_EXCLUDING_TAX")
    private BigDecimal unitPriceExcludingTax;
    @ApiModelProperty("接收数量")
    @TableField("RECEIVE_NUM")
    private BigDecimal receiveNum;
    @TableField("INVOICE_QUANTITY")
    private BigDecimal invoiceQuantity;
    @TableField("NOT_INVOICE_QUANTITY")
    private BigDecimal notInvoiceQuantity;
    @ApiModelProperty("采购订单号")
    @TableField("ORDER_NUMBER")
    private String orderNumber;
    @ApiModelProperty("订单行号")
    @TableField("LINE_NUM")
    private Integer lineNum;
    @ApiModelProperty("合同编号")
    @TableField("CONTRACT_NO")
    private String contractNo;
    @ApiModelProperty("合同头信息ID")
    @TableField("CONTRACT_HEAD_ID")
    private Long contractHeadId;
    @ApiModelProperty("合同编号")
    @TableField("CONTRACT_CODE")
    private String contractCode;
    @ApiModelProperty("净价金额")
    @TableField("NO_TAX_AMOUNT")
    private BigDecimal noTaxAmount;
    @ApiModelProperty("税率编码")
    @TableField("TAX_KEY")
    private String taxKey;
    @ApiModelProperty("税率计算值")
    @TableField("TAX_RATE")
    private BigDecimal taxRate;
    @ApiModelProperty("税额")
    @TableField("TAX")
    private BigDecimal tax;
    @ApiModelProperty("含税金额")
    @TableField("TAX_AMOUNT")
    private BigDecimal taxAmount;
    @ApiModelProperty("项目编号")
    @TableField("PROJECT_NUM")
    private String projectNum;
    @ApiModelProperty("项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;
    @ApiModelProperty("任务编号")
    @TableField("TASK_NUM")
    private String taskNum;
    @ApiModelProperty("任务名称")
    @TableField("TASK_NAME")
    private String taskName;
    @ApiModelProperty("生效日期(YYYY-MM-DD)")
    @TableField("START_DATE")
    private LocalDate startDate;
    @ApiModelProperty("失效日期(YYYY-MM-DD)")
    @TableField("END_DATE")
    private LocalDate endDate;
    @ApiModelProperty("采购发运行号")
    @TableField("SHIP_LINE_NUM")
    private Long shipLineNum;
    @ApiModelProperty("事务处理类型id")
    @TableField("TXN_ID")
    private Long txnId;
    @ApiModelProperty("事务处理类型id(父事务处理类型id)")
    @TableField("PARENT_TXN_ID")
    private Long parentTxnId;
    @TableField("CURRENCY_ID")
    private Long currencyId;
    @TableField("CURRENCY_NAME")
    private String currencyName;
    @TableField("CURRENCY_CODE")
    private String currencyCode;
    @TableField(
            exist = false
    )
    private String settlementOrderNumber;

    @ApiModelProperty("规格")
    @TableField("EXT_MATERIAL_MODEL")
    private String extMaterialModel;



}

