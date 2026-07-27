package com.midea.cloud.srm.model.sou.fixprice.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPricePaymentMethodEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExtFixPriceLineExportVO {

    @ExcelProperty("物料编码")
    @ApiModelProperty("物料编码")
    private String itemCode;

    @ExcelProperty("物料名称")
    @ApiModelProperty("物料名称")
    private String itemDesc;

    @ExcelProperty("规格型号")
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    @ExcelProperty("计量单位")
    @ApiModelProperty("单位")
    private String unit;

    @ExcelProperty("数量")
    @ApiModelProperty("数量")
    private BigDecimal quantity;

    @ExcelProperty("供应商名称")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ExcelProperty("未税单价")
    @ApiModelProperty("未税单价")
    private BigDecimal notaxPrice;

    @ExcelProperty("税率(%)")
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    @ExcelProperty("未税总价")
    @ApiModelProperty("未税总价")
    private BigDecimal notaxTotalPrice;

    @ExcelProperty("发票类型")
    @ApiModelProperty("发票类型(EXT_SOU_INQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    /**
     * 采购需求那边的最低价供应商
     */
    @ExcelProperty("近期最低价格(未税)")
    @ApiModelProperty("近期最低价格(未税)")
    private BigDecimal latestMinNotaxPrice;


    /**
     * 最低价跟当前价的比较
     */
    @ExcelProperty("浮动比例(%)")
    @ApiModelProperty("浮动比例")
    private BigDecimal priceFloatScale;

    @ExcelProperty("近期最低价供应商")
    @ApiModelProperty("近期最低价供应商名称")
    private String latestMinVendorName;

    /**
     * 采购需求那边的最低价的品牌
     */
    @ExcelProperty("近期最低价品牌")
    @ApiModelProperty("近期最低价品牌")
    private String latestMinBrand;

    /** @see ExtInqSouOrderItem#getExtWinReason */
    @ExcelProperty("中标原因")
    @ApiModelProperty("中标原因")
    private String extWinReason;

    /**  询价才有 @see SouOrderItem#getOrderRemark */
    @ExcelProperty("供应商备注")
    @ApiModelProperty("供应商备注")
    private String orderRemark;

    @ExcelProperty("预付款说明")
    @ApiModelProperty("预付款说明")
    private String advancePaymentRemark;

    /** 供货周期 -- 询价 */
    @ExcelProperty("到货周期(自然日)")
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;


    /** 采购员昵称 -- 询价 */
    @ExcelProperty("采购员")
    @ApiModelProperty("采购员昵称")
    private String buyerNickname;

    /** RequirementHead#ceeaPrType (application_form_type) */
    @ExcelProperty("申请类型")
    @ApiModelProperty("申请类型")
    private String applyType;

    /** 质保期 -- 询价 */
    @ExcelProperty("质保期(自然日)")
    @ApiModelProperty("质保期")
    private Integer extWarrantyPeriod;

    @ExcelProperty("申请单位")
    @ApiModelProperty("业务实体名称(申请单位)")
    private String orgOuName;

    /** 预估单价 -- 采购需求 */
    @ExcelProperty("预估单价")
    @ApiModelProperty("预估单价")
    private BigDecimal extPredictPrice;

    /** 预估总金额 -- 采购需求 */
    @ExcelProperty("预估总价")
    @ApiModelProperty("预估总价")
    private BigDecimal extPredictAmount;

    @ExcelProperty("来源类型")
    @ApiModelProperty("来源类型")
    private String sourceFromType;

    /** 购买类型 -- 采购需求 */
    @ExcelProperty("购买类型")
    @ApiModelProperty("购买类型(PR_BUY_TYPE)")
    private String extBuyType;

    @ExcelProperty("询价单号")
    @ApiModelProperty("来源单据编号")
    private String sourceFromNo;

    /** 审批状态(通过/不通过) */
    @ExcelProperty("审批状态")
    @ApiModelProperty("审批状态")
    private String fixPriceLineStatus;

    @ExcelProperty("是否关闭")
    @ApiModelProperty("是否关闭")
    private String hasClosed;


    /** @see ExtFixPricePaymentMethodEnum */
    @ExcelProperty("付款方式")
    @ApiModelProperty("付款方式")
    private String paymentMethod;

    @ExcelProperty("付款条款")
    @ApiModelProperty("付款条款")
    private String paymentTerm;

    @ExcelProperty("是否签订合同")
    @ApiModelProperty("是否签订合同")
    private String hasSignedContract;
}
