package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * MQL - 修改中标数量
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouChangeWinAmountDTO extends BaseObjectX {
/**    orderItemId  winAmount */
    @ApiModelProperty("中标数量信息")
    private Map<Long, BigDecimal> winAmounts;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (winAmounts == null || winAmounts.isEmpty()) {
            throw new IllegalArgumentException("缺少winAmounts数据");
        }
    }

}
