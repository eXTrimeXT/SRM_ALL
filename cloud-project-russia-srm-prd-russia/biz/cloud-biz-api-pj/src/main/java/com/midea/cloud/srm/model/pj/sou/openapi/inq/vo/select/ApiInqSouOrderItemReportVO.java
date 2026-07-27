package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select;

import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select.ApiInqSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
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
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderItemReportVO extends BaseObjectX {

    /** 物料需求信息 */
    private ApiInqSouItemVO souItem;
    /** 每个中标供应商对该物料的最终报价 */
    private List<ApiInqSouSelectQueryVO> orderInfos;
    /** 最新的历史未税单价 */
    private BigDecimal latestNotaxPrice;
    /** 最新历史含税单价 */
    private BigDecimal latestTaxPrice;

}
