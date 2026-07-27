package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * MQL - 作废报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderCancelDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    /**    vendorId  cancelReason */
    @ApiModelProperty("供应商作废信息(vendorId - cancelReason)")
    private Map<Long, String> vendorMap;
    /** @see SouTypeEnum */
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorMap == null || vendorMap.isEmpty()) {
            throw new IllegalArgumentException("缺少vendorMap参数");
        }
        for (Map.Entry<Long, String> entry : vendorMap.entrySet()) {
            String cancelReason = StringUtils.trimToNull(entry.getValue());

            if (cancelReason != null && cancelReason.length() > 300) {
                throw new IllegalArgumentException("作废原因输入长度不能超过300");
            }
            entry.setValue(cancelReason);
        }
    }

}