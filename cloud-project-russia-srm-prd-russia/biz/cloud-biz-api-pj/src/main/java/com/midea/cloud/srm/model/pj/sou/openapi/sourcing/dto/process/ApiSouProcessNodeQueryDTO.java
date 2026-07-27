package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 寻源单流程节点信息查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessNodeQueryDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("是包含未启用的节点")
    private Boolean containsNotUseNodes;

}
