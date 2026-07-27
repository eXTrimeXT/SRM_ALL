package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * MQL - 撤回报价
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@ApiModel(description = "撤回报价")
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderWithdrawDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("撤回原因")
    private String withdrawReason;
    @ApiModelProperty("寻源类型")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        withdrawReason = StringUtils.trimToNull(withdrawReason);
        int length = 300;
        if (withdrawReason != null && withdrawReason.length() > length) {
            throw new IllegalArgumentException("撤回原因输入长度不能超过300");
        }
    }

}