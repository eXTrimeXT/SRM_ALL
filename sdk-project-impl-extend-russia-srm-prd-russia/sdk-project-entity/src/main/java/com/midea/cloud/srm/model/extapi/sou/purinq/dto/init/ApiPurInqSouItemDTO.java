package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
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
public class ApiPurInqSouItemDTO extends ApiSouItemDTO {

    /** @see ExtPurInqSouItem#getArea */
    @ApiModelProperty("供货范围")
    private String area;

    /** @see ExtPurInqSouItem#getModel */
    @ApiModelProperty("规格型号")
    private String model;

    /** @see ExtPurInqSouItem#getBrand */
    @ApiModelProperty("品牌")
    private String brand;

    /** @see ExtPurInqSouItem#getDesignVendorId */
    @ApiModelProperty("历史供应商ID")
    private Long designVendorId;

    /** @see ExtPurInqSouItem#getDesignVendorCode */
    @ApiModelProperty("历史供应商编码")
    private String designVendorCode;

    /** @see ExtPurInqSouItem#getDesignVendorName */
    @ApiModelProperty("历史供应商名称")
    private String designVendorName;

    /** @see ExtPurInqSouItem#getDesignNotaxPrice */
    @ApiModelProperty("历史未税价格")
    private BigDecimal designNotaxPrice;

}
