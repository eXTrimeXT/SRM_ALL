package com.midea.cloud.srm.model.extapi.sou.purinq.vo.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouOrderItemVO extends ApiSouOrderItemVO {

    /** @see ExtPurInqSouItem#getArea */
    @ApiModelProperty("供货范围")
    private String area;

    /** @see ExtPurInqSouItem#getModel */
    @ApiModelProperty("规格型号")
    private String model;

    /** @see ExtPurInqSouItem#getBrand */
    @ApiModelProperty("品牌")
    private String brand;

    /** @see ExtPurInqSouOrderItem#getInvoiceType */
    @ApiModelProperty("发票类型")
    private String invoiceType;

    /** @see ExtPurInqSouOrderItem#getPriceTaxTotal */
    @ApiModelProperty("价税合计")
    private BigDecimal priceTaxTotal;

    /** @see ExtPurInqSouOrderItem#getExtLeadTime */
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    /** @see ExtPurInqSouOrderItem#getExtWarrantyPeriod */
    @ApiModelProperty("保修期(保质期)")
    private Integer extWarrantyPeriod;

    /** @see ExtPurInqSouOrderItem#getExtWinReason */
    @ApiModelProperty("中标原因")
    private String extWinReason;

    /** @see ExtPurInqSouOrderItem#getHasFixPrice */
    @ApiModelProperty("是否已定价")
    private Enable hasFixPrice;

    /** @see ExtPurInqSouOrderItem#getExtFixPriceHeadId */
    @ApiModelProperty("定价单ID")
    private Long extFixPriceHeadId;

    /** @see ExtPurInqSouOrderItem#getExtFixPriceNo */
    @ApiModelProperty("定价单号")
    private String extFixPriceNo;

    /** @see ExtPurInqSouOrderItem#getExtFixPriceLineId */
    @ApiModelProperty("定价单行ID")
    private Long extFixPriceLineId;

    /** @see ExtPurInqSouOrderItem#getLatestPriceTag */
    @ApiModelProperty("是否最新报价")
    private Enable latestPriceTag;

}
