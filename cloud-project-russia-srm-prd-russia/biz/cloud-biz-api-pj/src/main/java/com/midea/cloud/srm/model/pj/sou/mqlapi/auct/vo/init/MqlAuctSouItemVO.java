package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.mqlapi.inq.dto.init.MqlInqSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.init.MqlSouItemVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价MQL - 物料需求信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouItemVO extends MqlSouItemVO {

    @ApiModelProperty("竞价物料需求")
    private AuctSouItem auctSouItem;

    @ApiModelProperty("付款条款")
    private List<AuctSouItemPayment> paymentList;

}
