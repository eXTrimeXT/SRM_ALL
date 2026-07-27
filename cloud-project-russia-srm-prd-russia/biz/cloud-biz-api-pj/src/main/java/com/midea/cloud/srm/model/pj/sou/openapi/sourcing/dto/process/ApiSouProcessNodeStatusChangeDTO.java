package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * 寻源openAPI - 流程节点状态更新
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiSouProcessNodeStatusChangeDTO extends BaseObjectX {

    /** @see SouProcessNode#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("节点信息")
    private Set<NodeDetail> nodeList;

    @Data
    @EqualsAndHashCode(exclude = "status")
    public static class NodeDetail extends BaseObjectX {
        /** @see SouProcessNode#getProcessNode */
        @ApiModelProperty("流程节点名称")
        private String nodeName;

        /** @see SouProcessNode#getNodeStatus */
        @ApiModelProperty("节点状态")
        private Enable status;
    }

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(nodeList)) {
            throw new IllegalArgumentException("缺少nodeList数据");
        } else {
            nodeList.forEach(node -> {
                node.nodeName = StringUtils.trimToNull(node.nodeName);
                if (node.nodeName == null) {
                    throw new IllegalArgumentException("缺少nodeList.nodeName参数");
                }
                if (node.status == null) {
                    throw new IllegalArgumentException("缺少nodeList.status");
                }
            });
        }
    }

}
