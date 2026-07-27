package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init.MqlBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select.MqlBidSouSelectQueryVO;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源报价物料报表
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouOrderItemReportVO extends BaseObjectX {

    /** 物料需求信息 */
    private MqlBidSouItemVO souItem;
    /** 每个中标供应商对该物料的最终报价 */
    private List<MqlBidSouSelectQueryVO> orderInfos;
    /** 最新的历史未税单价 */
    private BigDecimal latestNotaxPrice;
    /** 最新历史含税单价 */
    private BigDecimal latestTaxPrice;

}
