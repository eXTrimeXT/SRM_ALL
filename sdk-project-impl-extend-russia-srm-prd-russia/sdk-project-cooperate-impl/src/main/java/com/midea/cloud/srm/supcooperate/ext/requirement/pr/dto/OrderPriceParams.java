package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Accessors(chain = true)
@Data
public class OrderPriceParams implements Serializable {

    private Long vendorId;
    private String vendorCode;
    private String vendorName;
    private String currencyName;
    private BigDecimal taxPrice;
    private BigDecimal noTaxPrice;
    private BigDecimal taxRate;
    private Integer leadTime;
    private String invoiceType;
    private Integer warrantyPeriod;

    @ApiModelProperty("付款方式")
    private String paymentMethod;

    @ApiModelProperty("付款条款")
    private String paymentTerm;

    /**
     * 协议单
     */
    @ApiModelProperty("供应区域")
    private String supplyArea;
    @ApiModelProperty("物料id")
    private Long materialId;
    @ApiModelProperty("品牌")
    private String brand;
    @ApiModelProperty("协议行id")
    private Long agreementInfoId;
    @ApiModelProperty("协议性质")
    private String agreementType;


    /**
     * 定价单
     */
    private Long requirementLienId;

    public String getPriceKey() {
        return getVendorId() + "-" + getCurrencyName() + "-" + getPaymentTerm() + "-" + getPaymentMethod();
    }

}
