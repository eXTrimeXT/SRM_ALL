package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouCurrency;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCurrencyEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 招投标openAPI - 报价币种
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouCurrencyEditDTO extends ApiSouCurrencyEditDTO {

    /** @see BidSouCurrency#getPriceTax */
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

}
