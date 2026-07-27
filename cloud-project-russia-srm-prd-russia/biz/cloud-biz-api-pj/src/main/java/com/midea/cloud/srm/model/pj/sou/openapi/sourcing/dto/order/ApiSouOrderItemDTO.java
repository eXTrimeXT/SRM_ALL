package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderLadderEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 寻源openAPI - 报价行信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderItemDTO extends ApiSouOrderItemEditDTO {

    @ApiModelProperty("供应商的阶梯报价")
    protected List<ApiSouOrderLadderEditDTO> ladderPriceList;
    /** attrId fieldId */
    @ApiModelProperty("模板报价数据")
    protected Map<Long, List<Map<String, Object>>> quoteData;

}
