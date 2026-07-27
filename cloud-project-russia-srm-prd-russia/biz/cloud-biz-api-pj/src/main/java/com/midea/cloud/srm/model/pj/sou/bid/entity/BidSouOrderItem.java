package com.midea.cloud.srm.model.pj.sou.bid.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.bid.entity.ExtBidSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 招投标 - 供应商投标行信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_bid_order_item")
@ApiModel("招投标.供应商投标行信息")
public class BidSouOrderItem extends ExtBidSouOrderItem {
    private static final long serialVersionUID = -7770176322891148178L;

    /** @see SouOrderItem#getOrderItemId */
    @TableId("ORDER_ITEM_ID")
    @ApiModelProperty("报价行ID")
    private Long orderItemId;

    /** @see SouOrderItem#getOrderId */
    @ApiModelProperty("报价单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty("运输方式(字典值: TRANSF_TYPE)")
    @TableField("TRANSPORT_TYPE")
    private String transportType;

    @TableField("MQO")
    @ApiModelProperty("最小订单量")
    private String mqo;

    @TableField("LEAD_TIME")
    @ApiModelProperty("供货周期")
    private String leadTime;

    @TableField("WARRANTY_PERIOD")
    @ApiModelProperty("保修期")
    private Integer warrantyPeriod;

    @TableField("DELIVER_DATE")
    @ApiModelProperty("承诺交货期")
    private Date deliverDate;

    @TableField("FORMULA_RESULT")
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

}
