package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.order;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 招投标openAPI - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouOrderItemDTO extends ApiSouOrderItemDTO {

    /** @see BidSouOrderItem#getTransportType */
    @ApiModelProperty("运输方式(字典值: TRANSF_TYPE)")
    private String transportType;

    /** @see BidSouOrderItem#getMqo */
    @ApiModelProperty("最小订单量")
    private String mqo;

    /** @see BidSouOrderItem#getLeadTime */
    @ApiModelProperty("供货周期")
    private String leadTime;

    /** @see BidSouOrderItem#getWarrantyPeriod */
    @ApiModelProperty("保修期")
    private Integer warrantyPeriod;

    /** @see BidSouOrderItem#getDeliverDate */
    @ApiModelProperty("承诺交货期")
    private Date deliverDate;

    /** @see BidSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    @ApiModelProperty("账期")
    private List<BidSouOrderItemPayment> paymentList;

}
