package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.control;

import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItemHis;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.control.MqlAuctSouControlOrderVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouProjectVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.init.MqlAuctSouRoundVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order.MqlAuctSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control.MqlSouControlOrderVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.control.MqlSouControlVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 竞价 MQL - 投标控制查询
 * PS: {@link MqlSouControlVO}
 *
 * @author ex_nongtb@partner.midea.com
 * @since 2023/07/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlAuctSouControlVO extends BaseObjectX {

    @ApiModelProperty("项目信息")
    private MqlAuctSouProjectVO project;
    @ApiModelProperty("当前轮次信息")
    private MqlAuctSouRoundVO currentRound;
    @ApiModelProperty("供应商报价信息")
    private List<MqlAuctSouControlOrderVO> orderInfos;
    @ApiModelProperty("供应商报价详情")
    private List<MqlAuctSouOrderItemVO> orderItemList;
    /**    souItemId */
    @ApiModelProperty("当前轮次的物料报价记录信息")
    private Map<Long, AuctSouItemHis> currentRoundSouItemHisMap;

}
