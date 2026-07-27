package com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select;

import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.select.ApiInqSouOrderItemReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderPriceNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderVendorReportVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * 寻源报价报表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderReportVO extends BaseObjectX {

    @ApiModelProperty("物料需求维度信息(以及中标供应商对该物料的最终报价)")
    private List<ApiInqSouOrderItemReportVO> souItemInfos;

    @ApiModelProperty("中标供应商的总报价信息")
    private List<ApiSouOrderVendorReportVO> vendorOrderInfos;

    /** itemId vendorName */
    @ApiModelProperty("物料历史价格信息(每个物料所属列表，按时间升序排列)")
    private Map<String, Map<String, List<ApiSouOrderPriceNodeVO>>> priceNodes;

}
