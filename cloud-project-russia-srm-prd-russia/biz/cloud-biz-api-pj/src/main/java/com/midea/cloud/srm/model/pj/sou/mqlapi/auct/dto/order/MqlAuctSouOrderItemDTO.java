package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order.MqlSouOrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 MQL - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderItemDTO extends MqlSouOrderItemDTO {

    @ApiModelProperty("竞价报价明细")
    private AuctSouOrderItem auctSouOrderItem;

}
