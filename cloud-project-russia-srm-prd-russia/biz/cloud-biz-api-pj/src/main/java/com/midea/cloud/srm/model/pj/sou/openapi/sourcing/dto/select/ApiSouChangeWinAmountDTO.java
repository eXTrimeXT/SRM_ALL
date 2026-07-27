package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源openAPI - 修改中标数量
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangeWinAmountDTO extends BaseObjectX {

    /** @see SouOrderItem#getOrderItemId */
    @ApiModelProperty("报价行ID")
    private Long orderItemId;

    /** @see SouOrderItem#getWinAmount */
    @Nullable
    @ApiModelProperty("中标数量")
    private BigDecimal winAmount;

    public static void formatParams(List<ApiSouChangeWinAmountDTO> params) {
        if (CollectionUtils.isEmpty(params)) {
            throw new IllegalArgumentException("缺少数据");
        }
        params.forEach(param -> {
            if (param.getOrderItemId() == null) {
                throw new IllegalArgumentException("缺少orderItemId参数");
            }
            if (param.getWinAmount() != null) {
                if (param.getWinAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("中标数量不能小于0");
                }
            }
        });
    }

}
