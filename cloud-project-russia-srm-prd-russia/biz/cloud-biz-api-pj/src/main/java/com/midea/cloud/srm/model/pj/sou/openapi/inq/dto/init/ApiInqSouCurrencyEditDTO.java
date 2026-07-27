package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCurrencyEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 简易询价openAPI - 报价币种
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouCurrencyEditDTO extends ApiSouCurrencyEditDTO {

    /** @see InqSouCurrency#getPriceTax */
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

}
