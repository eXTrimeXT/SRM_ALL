package com.midea.cloud.srm.model.sou.sourcing.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.Date;

/**
 * @author srm
 * @Description: 保证金收款及退款表
 * @date 2024/6/24
 */
@Data
public class SecurityDepositDto {
     @ApiModelProperty("板块")
     @TableField("EXT_ORG_BU_NAME")
     String extOrgBuName;
     @ApiModelProperty("公司")
     @TableField("EXT_ORG_OU_NAME")
     String extOrgOuName;
    @ApiModelProperty("项目名称")
    @TableField("SOU_NAME")
    String souName;
    @ApiModelProperty("招标编号")
    @TableField("EXT_PROJECT_NO")
    String extProjectNo;
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    String vendorCode;
    @ApiModelProperty("发标供应商名称")
    @TableField("VENDOR_NAME")
    String mVendorName;

    @ApiModelProperty("退款供应商名称")
    String refundVendor;
    @ApiModelProperty("扣款供应商名称")
    String chargeVendor;
    @ApiModelProperty("交款供应商名称")
    @TableField("PAY_NAME")
    String payName;
    @ApiModelProperty("科室")
    @TableField("EXT_APPLICANT_DEPART")
    String extApplicantDepart;
    @ApiModelProperty("招标负责人")
    @TableField("CREATED_FULL_NAME")
    String createdFullName;
    @ApiModelProperty("应收金额(万元)/交款金额(万元)")
    @TableField("EXT_EARNEST_AMOUNT")
    Double extEarnestAmount;
    @ApiModelProperty("应收金额(万元)")
    @TableField("receivableAmount")
    Double receivableAmount;
    @ApiModelProperty("交款金额(万元)")
    @TableField("paymentAmount")
    Double paymentAmount;
    @ApiModelProperty("发标时间")
    @TableField("PUBLISH_TIME")
    Date publishTime ;
    @ApiModelProperty("收标时间")
    @TableField("BUS_END_TIME")
    Date busEndTime;
    @ApiModelProperty("交款银行")
    @TableField("PAY_BANK")
    String payBank;
    @ApiModelProperty("交款联行号")
    @TableField("BANK_LINE")
    String bankLine;
    @ApiModelProperty("交款账号")
    @TableField("PAY_ACCOUNT")
    String payAccount;
    @ApiModelProperty("是否确认收款(Y/N转换为中文)")
    @TableField("MARGIN_STATUS")
    String marginStatus;
    @ApiModelProperty("交款时间")
    @TableField("TRANS_TIME")
    Date transTime;
    @ApiModelProperty("未交款原因")
    @TableField("CAUSE_DESC")
    String causeDesc;
    /**
     * CHARGE 扣款  REFUND 退款
     */
    @ApiModelProperty("扣款/退款类型")
    @TableField("TYPE")
    String type;
    @ApiModelProperty("退款金额/扣款金额")
    @TableField("AMOUNT")
    Double amount;
    @ApiModelProperty("扣款金额")
    Double chargeAmount;
    @ApiModelProperty("退款金额")
    Double refundAmount;
    @ApiModelProperty("退款银行")
    @TableField("REFUND_BANK")
    String refundBank;
    @ApiModelProperty("退款联行号")
    @TableField("REFUND_BANK_NUM")
    String refundBankNum;
    @ApiModelProperty("退款账号")
    @TableField("REFUND_ACCOUNT")
    String refundAccount;
    @ApiModelProperty("是否发起退款/是否退款成功(根据英文状态字典转化)")
    @TableField("REFUND_STATUS")
    String refundStatus;
    @ApiModelProperty("是否发起退款")
    String refund;
    @ApiModelProperty("是否退款成功")
    String refundSuccess;
    @ApiModelProperty("退款时间")
    @TableField("REFUND_PAYMENT_DATE")
    Date refundPaymentDate;
    @ApiModelProperty("扣款时间")
    @TableField("CREATION_DATE")
    Date creationDate ;
    @ApiModelProperty("扣款原因")

    String chargeDescription;
    @ApiModelProperty("退款原因")

    String refundDescription;
    @ApiModelProperty("处理方式")
    @TableField("HANDER_MODE")
    String handerMode;
}
