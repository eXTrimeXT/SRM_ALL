package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.init;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouCurrencyDTO extends SouCurrency {

    @ApiModelProperty("招投标拓展表")
    private BidSouCurrency bidSouCurrency;

}
