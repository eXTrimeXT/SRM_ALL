package com.midea.cloud.srm.model.pj.sou.inq.dto.webapi.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2022/09/05
 */
@Data
@ApiModel("用于批量查询阶梯价/账期信息")
public class InqSouOrderLadderAndFormulaQueryWebDTO {

    /** @see SouItem#getSouItemId */
    @ApiModelProperty(value = "物料需求行ID", required = true)
    private Long souItemId;

    /** @see SouOrderItem#getOrderItemId */
    @Nullable
    @ApiModelProperty(value = "报价行ID(可为空)")
    private Long orderItemId;

    public static void formatParams(List<InqSouOrderLadderAndFormulaQueryWebDTO> params) {
        if (params.isEmpty()) {
            throw new IllegalArgumentException("缺少参数");
        }
        params.forEach(e -> {
            if (e.getSouItemId() == null) {
                throw new IllegalArgumentException("缺少souItemId参数");
            }
        });
    }

}
