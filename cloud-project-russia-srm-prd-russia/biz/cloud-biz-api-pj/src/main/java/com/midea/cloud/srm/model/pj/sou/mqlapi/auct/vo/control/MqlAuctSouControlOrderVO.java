package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouOrder;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control.MqlSouControlOrderVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 MQL - 报价管理
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouControlOrderVO extends MqlSouControlOrderVO {

    @ApiModelProperty("竞价报价单")
    private AuctSouOrder auctSouOrder;

    @ApiModelProperty("排名")
    private Integer auctRanking;

}
