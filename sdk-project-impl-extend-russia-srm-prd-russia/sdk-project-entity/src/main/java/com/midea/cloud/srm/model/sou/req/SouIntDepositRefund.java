package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源单意向金退款表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@ApiModel(description = "寻源单意向金退款表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_sou_intention_deposit_refund")
public class SouIntDepositRefund extends BaseEntity {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long refundId;

    /**
     * 寻源单ID
     */
    @ApiModelProperty("寻源单ID")
    private Long reqHeadId;

    /**
     * 寻源需求单报名ID
     */
    @ApiModelProperty("寻源需求单报名ID")
    private Long applyId;

    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    private Long vendorId;

    /**
     * 供应商编码/企业标识
     */
    @ApiModelProperty("供应商编码/企业标识")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * 退款账户
     */
    @ApiModelProperty("退款账户")
    private String refundBankAccount;

    /**
     * 退款户名
     */
    @ApiModelProperty("退款户名")
    private String refundBankAccountName;

    /**
     * 退款银行
     */
    @ApiModelProperty("退款银行")
    private String refundBankName;

    /**
     * 退款银行联行号
     */
    @ApiModelProperty("退款银行联行号")
    private String refundBankNumber;

    /**
     * 退款金额
     */
    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    @ApiModelProperty("退款原因")
    private String refundReason;

    /**
     * 退款备注
     */
    @ApiModelProperty("退款备注")
    private String refundRemark;

    /**
     * 退款附件ID
     */
    @ApiModelProperty("退款附件ID")
    private Long refundFileId;

    /**
     * 退款附件名称
     */
    @ApiModelProperty("退款附件名称")
    private String refundFileName;

    /**
     * 退款时间
     */
    @ApiModelProperty("退款时间")
    private Date refundTime;

    @ApiModelProperty("期望退款时间")
    private Date expectRefundTime;

}
