package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouRound;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 MQL - 轮次
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouRoundVO extends SouRound {

    @ApiModelProperty("竞价轮次")
    private AuctSouRound auctSouRound;

}
