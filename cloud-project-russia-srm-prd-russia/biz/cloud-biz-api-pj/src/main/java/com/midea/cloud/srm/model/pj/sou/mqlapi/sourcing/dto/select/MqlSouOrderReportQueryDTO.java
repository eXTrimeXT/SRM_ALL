package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 寻源核心 MQL - 获取报价报表信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MqlSouOrderReportQueryDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("寻源类型")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
