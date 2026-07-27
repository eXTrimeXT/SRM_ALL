package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源openAPI - 报价行阶梯报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderLadderEditDTO extends SouOrderItemHis {

    /** @see SouOrderItemHis#getOrderItemLadderId */
    @ApiModelProperty("ID")
    private Long orderItemLadderId;

    /** @see SouOrderItemHis#getSouItemLadderId */
    @ApiModelProperty("阶梯价模板ID")
    private Long souItemLadderId;

    /** @see SouOrderItemHis#getOrderNotaxPrice */
    @ApiModelProperty("原币未税单价")
    private BigDecimal orderNotaxPrice;

    /** @see SouOrderItemHis#getOrderTaxPrice */
    @ApiModelProperty("原币含税单价")
    private BigDecimal orderTaxPrice;

}
