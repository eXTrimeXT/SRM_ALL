package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源openAPI - 作废报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderCancelDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("作废原因")
    private String cancelReason;
    /** @see SouTypeEnum */

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        cancelReason = StringUtils.trimToNull(cancelReason);
        int length = 300;
        if (cancelReason != null && cancelReason.length() > length) {
            throw new IllegalArgumentException("作废原因输入长度不能超过300");
        }
    }

}
