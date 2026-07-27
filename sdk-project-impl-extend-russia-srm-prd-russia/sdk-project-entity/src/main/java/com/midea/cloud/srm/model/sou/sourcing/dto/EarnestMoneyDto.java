package com.midea.cloud.srm.model.sou.sourcing.dto;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/26
 */

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: for srm
 *意向金报表实体类
 * @author srm
 * @date 2024-08-08
 */
@Data
public class EarnestMoneyDto {
    @ApiModelProperty("寻源单号")
    @TableField("REQ_HEAD_NO")
    String reqHeadNo;
    @ApiModelProperty("板块")
    @TableField("ORG_BU_NAME")
    String orgBuName;
    @ApiModelProperty("公司")
    @TableField("ORG_NAME")
    String orgName;
    @ApiModelProperty("项目名称")
    @TableField("PROJECT_NAME")
    String projectName;
    @ApiModelProperty("公示截止时间")
    @TableField("PUBLIC_END_TIME")
    Date publicEndTime;
    @ApiModelProperty("应收意向金(元)")
    //@TableField("DEPOSIT_AMOUNT")
    Double intentionReceivable;
    @ApiModelProperty("交款金额")
    //@TableField("DEPOSIT_AMOUNT")
    Double paymentAmount;
    @ApiModelProperty("供应商负责人")
    @TableField("RESPONSIBILITY_USER_NAME")
    String responsibilityUserName;
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    String vendorCode;
    /**
     * 这里的入参是aVendorName，代表查询的是APPLY表
     */
    @ApiModelProperty("报名供应商名称")
    String aVendorName;
    @ApiModelProperty("付款方名称")
    //@TableField("VENDOR_BANK_ACCOUNT_NAME")
    String payerName;
    @ApiModelProperty("退款供应商名称")
    String refundSupplierName ;
    @ApiModelProperty("交款日期")
    @TableField("TRANS_TIME")
    Date transTime;
    @ApiModelProperty("交款银行")
    @TableField("VENDOR_BANK_NAME")
    String vendorBankName;
    @ApiModelProperty("交款联行号")
    @TableField("VENDOR_BANK_NUMBER")
    String vendorBankNumber;
    @ApiModelProperty("交款账号")
    @TableField("VENDOR_BANK_ACCOUNT")
    String vendorBankAccount;
    @ApiModelProperty("报名处理方式")
    @TableField("APPLY_HANDLE_TYPE")
    String applyHandleType;
    @ApiModelProperty("报名处理原因")
    @TableField("APPLY_HANDLE_REASON")
    String applyHandleReason;
    /**
     * 这里的入参是IVendorName，代表查询的是INVOICE表
     */
    @ApiModelProperty("开票供应商名称")
    String iVendorName;
    @ApiModelProperty("开票金额")
    @TableField("PRICE")
    Double price;
    @ApiModelProperty("申请开票时间")
    @TableField("CREATION_DATE")
    Date creationDate;
    /**
     * 根据status字段转化为是/否
     * INVOICED,INVOICING 转化为是
     * BE_INVOICED 转化为否
     * 值为'是' 传入Y
     * 值为'否' 传入N
     * invoice表
     */
    @ApiModelProperty("是否开票")
  //  @TableField("STATUS")
    String iStatus;
    @ApiModelProperty("退款银行联行号")
    @TableField("REFUND_BANK_NUMBER")
    String refundBankNumber;
    @ApiModelProperty("退款银行")
    @TableField("REFUND_BANK_NAME")
    String refundBankName;
    @ApiModelProperty("退款账号")
    @TableField("REFUND_BANK_ACCOUNT")
    String refundBankAccount;
    @ApiModelProperty("退款金额")
    @TableField("REFUND_AMOUNT")
    Double refundAmount;
    @ApiModelProperty("退款时间")
    @TableField("REFUND_TIME")
    Date refundTime;
    @ApiModelProperty("退款原因")
    @TableField("REFUND_REASON")
    String refundReason;
    @ApiModelProperty("意向金退款状态")
    @TableField("DEPOSIT_REFUND_STATUS")
    String depositRefundStatus;
    /**
     * 是否发起退款 是否退款成功由意向金退款状态字段 转化而成
     * 意向金退款状态在apply表
     */
    @ApiModelProperty("是否发起退款")
    String refund;
    @ApiModelProperty("是否退款成功")
    String refundSuccess;
    /**
     * Head表
     */
    @ApiModelProperty("是否取消")
   // @TableField("STATUS")
    String hStatus;
}
