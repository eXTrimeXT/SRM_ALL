package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.select;

import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init.ApiBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.select.ApiBidSouSelectQueryVO;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源报价物料报表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouOrderItemReportVO extends BaseObjectX {

    /** 物料需求信息 */
    private ApiBidSouItemVO souItem;
    /** 每个中标供应商对该物料的最终报价 */
    private List<ApiBidSouSelectQueryVO> orderInfos;
    /** 最新的历史未税单价 */
    private BigDecimal latestNotaxPrice;
    /** 最新历史含税单价 */
    private BigDecimal latestTaxPrice;

}
