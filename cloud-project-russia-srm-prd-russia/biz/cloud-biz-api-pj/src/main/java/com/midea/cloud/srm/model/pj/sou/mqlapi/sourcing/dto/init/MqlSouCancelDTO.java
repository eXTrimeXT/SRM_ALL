package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * MQL - 作废寻源
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/08
 */
@Data
@ApiModel(description = "作废寻源参数")
@EqualsAndHashCode(callSuper = true)
public class MqlSouCancelDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("寻源类型")
    private String souType;
    @ApiModelProperty("作废原因(长度限制300)")
    private String cancelReason;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        cancelReason = StringUtils.trimToNull(cancelReason);
        int length = 300;
        if (cancelReason != null && cancelReason.length() > length) {
            throw new IllegalArgumentException("作废原因长度不能超过300");
        }
    }

}
