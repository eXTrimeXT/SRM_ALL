package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源报价供应商报表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderVendorReportVO extends BaseObjectX {

    /** @see SouOrder#getOrderId */
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /**
     * 供应商信息
     * @see SouVendor#getVendorId
     * @see SouOrderItem#getVendorId
     */
    private Long vendorId;
    private String vendorCode;
    private String vendorName;

    /** @see SouOrder#getStandardNotaxTotalPrice */
    @ApiModelProperty("报价总金额(本币未税)")
    private BigDecimal standardNotaxTotalPrice;

    /** @see SouOrder#getStandardTaxTotalPrice */
    @ApiModelProperty("报价总金额(本币含税)")
    private BigDecimal standardTaxTotalPrice;

    @ApiModelProperty("中标总金额(本币未税--中标物料单价 * 需求数量)")
    private BigDecimal winNotaxTotalPrice;

    @ApiModelProperty("中标总金额(本币含税--中标物料单价 * 需求数量)")
    private BigDecimal winTaxTotalPrice;

}
