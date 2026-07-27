package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MQL - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderItemDTO extends SouOrderItem {

    @ApiModelProperty("阶梯报价")
    private List<SouOrderItemHis> ladderPriceList;

}
