package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.process;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 流程节点
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MqlSouProcessNodeVO extends SouProcessNode {

    @ApiModelProperty("节点是否启用")
    private Enable enabled;

    public static List<MqlSouProcessNodeVO> convertSouVO(SouProcessConfig processConfig, List<SouProcessNode> processNodeList) {
        List<MqlSouProcessNodeVO> nodeList = new ArrayList<>(processNodeList.size());
        Map<String/* nodeName */, SouProcessNode> nodeMap = processNodeList.stream()
                .collect(Collectors.toMap(SouProcessNode::getProcessNode, Function.identity()));
        // 1: 项目信息
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.projectInfo, processConfig.getProjectInfo(), nodeMap.get(SouProcessNodeEnum.projectInfo.name())));
        // 2: 需求信息
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.requireInfo, processConfig.getRequireInfo(), nodeMap.get(SouProcessNodeEnum.requireInfo.name())));
        // 3: 邀请供应商
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.inviteVendor, processConfig.getInviteVendor(), nodeMap.get(SouProcessNodeEnum.inviteVendor.name())));
        // 4: 评分规则
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.scoreRule, processConfig.getScoreRule(), nodeMap.get(SouProcessNodeEnum.scoreRule.name())));
        // 5: 立项审批
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.createApproval, processConfig.getCreateApproval(), nodeMap.get(SouProcessNodeEnum.createApproval.name())));
        // 6: 报名管理
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.signUpManagement, processConfig.getSignUpManagement(), nodeMap.get(SouProcessNodeEnum.signUpManagement.name())));
        // 7: 投标控制
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.bidingControl, processConfig.getBidingControl(), nodeMap.get(SouProcessNodeEnum.bidingControl.name())));
        // 8: 技术标管理
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.techManagement, processConfig.getTechManagement(), nodeMap.get(SouProcessNodeEnum.techManagement.name())));
        // 9: 商务标管理
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.businessManagement, processConfig.getBusinessManagement(), nodeMap.get(SouProcessNodeEnum.businessManagement.name())));
        // 10: 评选
        nodeList.add(doConvertSouVO(SouProcessNodeEnum.evaluation, processConfig.getEvaluation(), nodeMap.get(SouProcessNodeEnum.evaluation.name())));
        // 11: 其他节点
        nodeMap.forEach((nodeName, node) -> {
            try {
                SouProcessNodeEnum.valueOf(nodeName);
            } catch (IllegalArgumentException e) {
                MqlSouProcessNodeVO vo = new MqlSouProcessNodeVO(); {
                    vo.setProcessConfigId(processConfig.getProcessConfigId());
                    vo.setProcessNodeId(node.getProcessNodeId());
                    vo.setProcessNode(nodeName);
                    vo.setNodeStatus(node.getNodeStatus());
                }
                nodeList.add(vo);
            }
        });

        return nodeList;
    }

    private static MqlSouProcessNodeVO doConvertSouVO(SouProcessNodeEnum node, Enable enabled, SouProcessNode processNode) {
        MqlSouProcessNodeVO vo = new MqlSouProcessNodeVO();
        vo.setProcessConfigId(processNode.getProcessConfigId());
        vo.setProcessNodeId(processNode.getProcessNodeId());
        vo.setProcessNode(node.name());
        vo.setEnabled(enabled);
        vo.setNodeStatus(processNode.getNodeStatus());
        return vo;
    }

}
