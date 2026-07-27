package com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.pj.sou.model.entity.ExtPriceApprovalItemPayment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 价格审批单 - 中标行付款条款
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/18
 */
@Data
@ApiModel("价格审批单-中标行付款条款")
@TableName("scc_price_approval_item_pay")
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalItemPayment extends ExtPriceApprovalItemPayment {

    @TableId("APPROVAL_ITEM_PAYMENT_ID")
    @ApiModelProperty("ID")
    private Long approvalItemPaymentId;

    /** @see PriceApprovalItem#getApprovalItemId */
    @TableField("APPROVAL_ITEM_ID")
    @ApiModelProperty("中标行ID")
    private Long approvalItemId;

    /** @see PriceApprovalItem#getApprovalId */
    @TableField("APPROVAL_ID")
    @ApiModelProperty("价格审批单ID")
    private Long approvalId;

    /**
     * 付款信息
     */
    @TableField("PAYMENT_PERIOD")
    @ApiModelProperty("付款账期[字典值: PAYMENT_PERIOD]")
    private String paymentPeriod;

    @TableField("PAYMENT_CONDITION_ID")
    @ApiModelProperty("付款条件ID")
    private Long paymentConditionId;

    @TableField("PAYMENT_CONDITION")
    @ApiModelProperty("付款条件名称")
    private String paymentCondition;

    @TableField("PAYMENT_MODE")
    @ApiModelProperty("付款方式[字典值: PAYMENT_MODE]")
    private String paymentMode;

    @TableField("PAYMENT_PROPORTION")
    @ApiModelProperty("付款比例%")
    private BigDecimal paymentProportion;

    @TableField("PAYMENT_PHASE")
    @ApiModelProperty("付款阶段[字典值: PAYMENT_STAGE]")
    private String paymentPhase;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
