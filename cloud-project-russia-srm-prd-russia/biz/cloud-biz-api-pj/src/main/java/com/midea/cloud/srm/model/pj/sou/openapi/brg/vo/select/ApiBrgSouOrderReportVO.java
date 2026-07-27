package com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.select;

import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.select.ApiBrgSouOrderItemReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderPriceNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderVendorReportVO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 寻源报价报表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouOrderReportVO extends BaseObjectX {

    @ApiModelProperty("物料需求维度信息(以及中标供应商对该物料的最终报价)")
    private List<ApiBrgSouOrderItemReportVO> souItemInfos;

    @ApiModelProperty("中标供应商的总报价信息")
    private List<ApiSouOrderVendorReportVO> vendorOrderInfos;

    @ApiModelProperty("物料历史价格信息(每个物料所属列表，按时间升序排列)")
    private Map<String, Map<String, List<ApiSouOrderPriceNodeVO>>> priceNodes;

}
