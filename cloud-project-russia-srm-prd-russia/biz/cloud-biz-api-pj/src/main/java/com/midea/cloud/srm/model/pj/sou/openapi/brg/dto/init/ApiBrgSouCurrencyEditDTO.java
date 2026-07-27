package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init;

import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouCurrency;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCurrencyEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 项目式询价openAPI - 报价币种
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouCurrencyEditDTO extends ApiSouCurrencyEditDTO {

    /** @see BrgSouCurrency#getPriceTax */
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

}
