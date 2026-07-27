package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.process;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MqlSouProcessNodeStatusChangeDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("流程节点信息")
    private List<SouProcessNode> processNodeList;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(processNodeList)) {
            throw new IllegalArgumentException("缺少processNodeList数据");
        } else {
            processNodeList.forEach(node -> {
                node.setProcessNode(StringUtils.trimToNull(node.getProcessNode()));
                if (node.getProcessNode() == null) {
                    throw new IllegalArgumentException("缺少processNodeList.processNode参数");
                }
                if (node.getNodeStatus() == null) {
                    throw new IllegalArgumentException("缺少processNodeList.nodeStatus参数");
                }
            });
        }
    }

}
