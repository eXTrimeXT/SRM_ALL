package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItemHis;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouOrder;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order.MqlAuctSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 竞价MQL - 报价单
 * PS: 参考{@link MqlSouOrderVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouOrderVO extends SouOrder {

    @ApiModelProperty("立项信息")
    private MqlAuctSouProjectVO souInitInfo;
    @ApiModelProperty("竞价报价单")
    private AuctSouOrder auctSouOrder;
    @ApiModelProperty("报价明细")
    private List<MqlAuctSouOrderItemVO> orderItemList;
    @ApiModelProperty("物料需求轮次报价汇总信息")
    private List<AuctSouItemHis> auctSouItemHisList;
    @ApiModelProperty("报价附件")
    private List<MqlSouOrderFileVO> orderFileList;

}
