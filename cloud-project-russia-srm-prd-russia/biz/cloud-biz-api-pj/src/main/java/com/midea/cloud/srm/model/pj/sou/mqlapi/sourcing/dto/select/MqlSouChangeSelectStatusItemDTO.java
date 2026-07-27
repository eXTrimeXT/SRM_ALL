package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * MQL - 中标/落标明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MqlSouChangeSelectStatusItemDTO extends BaseObjectX {

    @ApiModelProperty("报价行ID")
    private Long orderItemId;

    @ApiModelProperty("中标数量")
    private BigDecimal winAmount;

}
