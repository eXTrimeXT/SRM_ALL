package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 - 物料需求付款条款
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_item_payment")
@ApiModel("竞价.物料需求付款条款")
public class CompSouItemPayment extends ExtCompSouItemPayment {

    @TableId("SOU_ITEM_PAYMENT_ID")
    @ApiModelProperty("ID")
    private Long souItemPaymentId;

    /** @see SouOrderItem#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouOrderItem#getSouItemId */
    @ApiModelProperty("物料需求ID")
    @TableField("SOU_ITEM_ID")
    private Long souItemId;

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
