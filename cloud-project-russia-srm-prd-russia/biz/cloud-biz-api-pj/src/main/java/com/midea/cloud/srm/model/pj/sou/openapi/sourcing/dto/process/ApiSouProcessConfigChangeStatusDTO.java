package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 生效/失效流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/21
 */
@Data
@ApiModel(description = "生效/失效流程配置")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessConfigChangeStatusDTO extends BaseObjectX {

    private Long processConfigId;

    @ApiModelProperty("true-生效/false-失效")
    private Boolean toValid;

}
