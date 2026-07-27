package com.midea.cloud.srm.model.pj.sou.bid.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.bid.entity.ExtBidSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author huangbf3
 * 招投标.供应商可选付款条款
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_bid_order_item_payment")
@ApiModel("付款条款")
public class BidSouOrderItemPayment extends ExtBidSouOrderItemPayment {
    private static final long serialVersionUID = 1L;

    @TableId("ORDER_ITEM_PAYMENT_ID")
    @ApiModelProperty("ID")
    private Long orderItemPaymentId;

    /** @see SouOrderItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouOrderItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    /** @see SouOrderItem#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouOrderItem#getOrderItemId */
    @TableField("ORDER_ITEM_ID")
    @ApiModelProperty("报价行ID")
    private Long orderItemId;

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
