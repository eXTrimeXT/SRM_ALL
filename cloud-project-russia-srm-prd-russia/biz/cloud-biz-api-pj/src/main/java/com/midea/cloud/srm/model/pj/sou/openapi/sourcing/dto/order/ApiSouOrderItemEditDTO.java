package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源openAPI - 报价行
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderItemEditDTO extends BaseObjectX {

    /**
     * @see SouOrderItem#getOrderItemId
     */
    @ApiModelProperty("ID/供应商报价行ID")
    private Long orderItemId;

    /**
     * @see SouOrderItem#getSouItemId
     */
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    /**
     * @see SouOrderItem#getVendorId
     */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    // ------------------------------------------------------------- 报价信息 -----------------------------------------------------------
    /**
     * @see SouOrderItem#getOrderCurrency
     */
    @ApiModelProperty("报价币种")
    private String orderCurrency;

    /**
     * @see SouOrderItem#getTaxKey
     */
    @ApiModelProperty("税率编码")
    private String taxKey;

    /**
     * @see SouOrderItem#getTaxRate
     */
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    /**
     * @see SouOrderItem#getOrderNotaxPrice
     */
    @ApiModelProperty("原币未税单价")
    private BigDecimal orderNotaxPrice;

    /**
     * @see SouOrderItem#getOrderTaxPrice
     */
    @ApiModelProperty("原币含税单价")
    private BigDecimal orderTaxPrice;

    @ApiModelProperty("本次报价金额")
    private BigDecimal orderNowPrice;

    /**
     * @see SouOrderItem#getPriceStartTime
     */
    @ApiModelProperty("价格有效期从")
    private Date priceStartTime;

    /**
     * @see SouOrderItem#getPriceEndTime
     */
    @ApiModelProperty("价格有效期到")
    private Date priceEndTime;

    /**
     * @see SouOrderItem#getOrderRemark
     */
    @ApiModelProperty("备注")
    private String orderRemark;


    @ApiModelProperty("报价次数")
    private BigDecimal order_round;



}
