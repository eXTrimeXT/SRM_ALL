package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init.MqlSouItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价MQL - 物料需求行信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouItemDTO extends MqlSouItemDTO {

    @ApiModelProperty("竞价拓展数据")
    private AuctSouItem auctSouItem;

    @ApiModelProperty("付款条款")
    private List<AuctSouItemPayment> paymentList;

}
