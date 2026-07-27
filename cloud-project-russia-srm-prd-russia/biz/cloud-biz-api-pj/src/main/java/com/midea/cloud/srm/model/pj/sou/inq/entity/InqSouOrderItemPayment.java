package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.cm.template.entity.PayType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源.核心表 - 阶梯价模板
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_inq_order_item_payment")
@ApiModel(description = "寻源-简易询价-报价账期表")
public class InqSouOrderItemPayment extends ExtInqSouOrderItemPayment {

    /**
     * 简易询价-报价账期表ID
     */
    @TableId("ORDER_ITEM_PAYMENT_ID")
    @ApiModelProperty("简易询价-报价账期表ID")
    private Long orderItemPaymentId;

    /**
     * 寻源核心-供应商报价行ID
     *
     * @see SouOrderItem#getOrderItemId
     */
    @TableField("ORDER_ITEM_ID")
    @ApiModelProperty("报价行ID")
    private Long orderItemId;

    /**
     * 寻源核心-询价单ID
     *
     * @see SouOrderItem#getProjectId
     */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源核心-询价单ID")
    private Long projectId;

    /**
     * 寻源核心-物料需求行ID
     *
     * @see SouOrderItem#getSouItemId
     */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("寻源核心-物料需求行ID")
    private Long souItemId;

    /** @see SouOrderItem#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("寻源核心-供应商报价头ID")
    private Long orderId;

    @TableField("PAYMENT_PERIOD")
    @ApiModelProperty("付款账期[字典值: PAYMENT_PERIOD]")
    private String paymentPeriod;

    /** @see PayType#getPayTypeId */
    @TableField("PAYMENT_CONDITION_ID")
    @ApiModelProperty("付款条件ID")
    private Long paymentConditionId;

    /** @see PayType#getPayExplain */
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
