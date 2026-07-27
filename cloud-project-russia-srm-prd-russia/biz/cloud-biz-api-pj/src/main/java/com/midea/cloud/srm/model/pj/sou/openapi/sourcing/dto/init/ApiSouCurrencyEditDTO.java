package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 报价币种
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSouCurrencyEditDTO extends BaseObjectX {

    /** @see SouCurrency#getSouCurrencyId */
    @ApiModelProperty("ID")
    private Long souCurrencyId;

    /** @see SouCurrency#getCurrencyCode */
    @ApiModelProperty(value = "币种编码(长度限制20)", required = true)
    private String currencyCode;

    /** @see SouCurrency#getPricePrecision */
    @ApiModelProperty(value = "供应商报价精度(0~8)", required = true)
    private Integer pricePrecision;

    /** @see SouCurrency#getSortIndex */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

}
