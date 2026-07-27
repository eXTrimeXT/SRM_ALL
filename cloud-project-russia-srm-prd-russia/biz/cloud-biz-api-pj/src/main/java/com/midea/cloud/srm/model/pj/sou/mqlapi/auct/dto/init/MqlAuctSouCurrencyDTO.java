package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价MQL - 可用币种
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouCurrencyDTO extends SouCurrency {

    @ApiModelProperty("竞价拓展表")
    private AuctSouCurrency auctSouCurrency;

}
