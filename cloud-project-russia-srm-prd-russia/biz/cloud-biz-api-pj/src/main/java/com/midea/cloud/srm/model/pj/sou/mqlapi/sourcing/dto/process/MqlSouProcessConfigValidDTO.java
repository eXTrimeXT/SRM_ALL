package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.process;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源核心 MQL - 流程配置生效/失效
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouProcessConfigValidDTO extends BaseObjectX {

    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    @ApiModelProperty("true-生效/false-失效")
    private Boolean valid;

    @ApiModelProperty("寻源场景")
    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (processConfigId == null) {
            throw new IllegalArgumentException("缺少processConfigId参数");
        }
    }

}
