package com.midea.cloud.srm.model.sou.fixprice.dto;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 长城定价单 - 定价明细审批通过/未通过参数
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceLinePassDTO extends BaseObjectX {

    /** @see ExtFixPriceHead#getFixPriceHeadId */
    @ApiModelProperty("定价单ID")
    private Long fixPriceHeadId;

    @ApiModelProperty("定价明细通过状态")
    private Map<Long, Enable> passInfo;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (fixPriceHeadId == null) {
            throw new IllegalArgumentException("缺少fixPriceHeadId参数");
        }
        if (passInfo == null || passInfo.isEmpty()) {
            throw new IllegalArgumentException("缺少passInfo数据");
        }
        passInfo.entrySet().removeIf(k -> k.getKey() == null || k.getValue() == null);
        if (passInfo.isEmpty()) {
            throw new IllegalArgumentException("缺少passInfo数据");
        }
    }

}
