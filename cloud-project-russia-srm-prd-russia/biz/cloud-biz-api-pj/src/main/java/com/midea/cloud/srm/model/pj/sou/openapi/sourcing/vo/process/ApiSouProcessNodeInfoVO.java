package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 寻源单流程节点信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouProcessNodeInfoVO extends SouProcessNode {

    @ApiModelProperty("节点是否启用")
    private Enable enabled;

}
