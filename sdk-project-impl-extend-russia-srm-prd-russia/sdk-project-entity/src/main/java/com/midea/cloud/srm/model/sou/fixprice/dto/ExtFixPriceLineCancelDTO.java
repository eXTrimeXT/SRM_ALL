package com.midea.cloud.srm.model.sou.fixprice.dto;

import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceLineCancelDTO extends BaseObjectX {

    /** @see ExtFixPriceLine#getFixPriceLineId */
    @ApiModelProperty("定价明细ID集合")
    private Set<Long> fixPriceLineIds;

    /** @see ExtFixPriceLine#getCancelReason */
    @ApiModelProperty("取消原因")
    private String cancelReason;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (CollectionUtils.isEmpty(fixPriceLineIds)) {
            throw new IllegalArgumentException("缺少数据");
        }
        cancelReason = StringUtils.trimToNull(cancelReason);
    }

}
