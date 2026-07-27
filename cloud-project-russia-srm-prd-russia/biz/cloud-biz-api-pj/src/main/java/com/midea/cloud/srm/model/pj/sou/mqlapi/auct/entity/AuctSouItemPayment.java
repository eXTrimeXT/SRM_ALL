package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.cm.template.entity.PayType;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 MQL - 付款条款
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/25
 */
@Data
@TableName("scc_sou_auct_sou_item_payment")
@EqualsAndHashCode(callSuper = true)
public class AuctSouItemPayment extends ExtAuctSouItemPayment {

    @TableId("SOU_ITEM_PAYMENT_ID")
    @ApiModelProperty("ID")
    private Long souItemPaymentId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @ApiModelProperty("物料需求行ID")
    @TableField("SOU_ITEM_ID")
    private Long souItemId;

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
