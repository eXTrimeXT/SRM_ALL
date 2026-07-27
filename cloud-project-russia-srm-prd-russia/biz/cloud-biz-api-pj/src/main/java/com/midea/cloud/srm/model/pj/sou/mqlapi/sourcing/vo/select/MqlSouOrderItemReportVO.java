package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select.MqlSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源报价物料报表
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderItemReportVO extends BaseObjectX {

    /** 物料需求信息 */
    private SouItem souItem;
    /** 每个中标供应商对该物料的最终报价 */
    private List<MqlSouSelectQueryVO> orderInfos;
    /** 最新的历史未税单价 */
    private BigDecimal latestNotaxPrice;
    /** 最新历史含税单价 */
    private BigDecimal latestTaxPrice;

}
