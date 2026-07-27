package com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
public class ApiSouProcessNodeVO extends BaseObjectX {
    /** @see SouProcessConfig#getProcessConfigId */
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    /** @see SouProcessNode#getProcessNodeId */
    @ApiModelProperty("流程节点ID")
    private Long processNodeId;

    /** @see SouProcessNode#getProcessNode */
    @ApiModelProperty("节点名称")
    private String processNode;

    @ApiModelProperty("节点是否启用")
    private Enable enabled;

    /** @see SouProcessNode#getNodeStatus */
    @ApiModelProperty("节点状态")
    private Enable nodeStatus;

    public static List<SouProcessNodeEnum> defalutNodeList = Arrays.asList(
            // 1: 项目信息
            SouProcessNodeEnum.projectInfo,
            // 2: 需求信息
            SouProcessNodeEnum.requireInfo,
            // 3: 邀请供应商
            SouProcessNodeEnum.inviteVendor,
            // 4: 评分规则
            SouProcessNodeEnum.scoreRule,
            // 5: 立项审批
            SouProcessNodeEnum.createApproval,
            // 6: 报名管理
            SouProcessNodeEnum.signUpManagement,
            // 7: 投标控制
            SouProcessNodeEnum.bidingControl,
            // 8: 技术标管理
            SouProcessNodeEnum.techManagement,
            // 9: 商务标管理
            SouProcessNodeEnum.businessManagement,
            // 10: 评选
            SouProcessNodeEnum.evaluation,
            // 11: 编制定标结果
            SouProcessNodeEnum.bidReuslt,
            // 12: 中/落标通知
            SouProcessNodeEnum.bidWinOrLoss,
            // 13: 归档
            SouProcessNodeEnum.bidArchive
    );

    public static List<SouProcessNodeEnum> bidNodeList = Arrays.asList(
            // 1: 项目信息
            SouProcessNodeEnum.projectInfo,
            // 2: 需求信息
            SouProcessNodeEnum.requireInfo,
            // 3: 评分规则
            SouProcessNodeEnum.scoreRule,
            // 4: 邀请供应商
            SouProcessNodeEnum.inviteVendor,
            // 5: 立项审批
            SouProcessNodeEnum.createApproval,
            //6: 保证金管理
            SouProcessNodeEnum.bondManagement,
            // 7: 投标控制
            SouProcessNodeEnum.bidingControl,
            // 8: 技术标管理
            SouProcessNodeEnum.techManagement,
            // 9: 商务标管理
            SouProcessNodeEnum.businessManagement,
            // 10: 编制定标结果
            SouProcessNodeEnum.bidReuslt,
            // 11: 中/落标通知
            SouProcessNodeEnum.bidWinOrLoss,
            // 12: 归档
            SouProcessNodeEnum.bidArchive
    );

    public static Enable configEnable(Map processConfig, String nodeEnum) {
        if(processConfig.containsKey(nodeEnum)) {
            return MapUtils.getString(processConfig, nodeEnum).equals(Enable.Y.name())?Enable.Y:Enable.N;
        }
        return Enable.N;
    }


    public static List<ApiSouProcessNodeVO> convertSouVO(SouProcessConfig processConfig, List<SouProcessNode> processNodeList) {
        List<ApiSouProcessNodeVO> nodeList = new ArrayList<>(processNodeList.size());
        Map<String/* nodeName */, SouProcessNode> nodeMap = processNodeList.stream()
                .collect(Collectors.toMap(SouProcessNode::getProcessNode, Function.identity()));
        List<SouProcessNodeEnum> processNodeEnumList = defalutNodeList;

        if(SouTypeEnum.bid.name().equals(processConfig.getSouType())) {
            //招标
            processNodeEnumList = bidNodeList;
        }

        Map configMap = JSON.parseObject(JSON.toJSONString(processConfig));

        processNodeEnumList.stream().forEach(nodeEnum -> {
            addNode(nodeList, nodeEnum ,configEnable(configMap, nodeEnum.name()) , nodeMap.get(nodeEnum.name()));
        });

        // 11: 其他节点
        nodeMap.forEach((nodeName, node) -> {
            try {
                SouProcessNodeEnum.valueOf(nodeName);
            } catch (IllegalArgumentException e) {
                nodeList.add(new ApiSouProcessNodeVO(processConfig.getProcessConfigId(), node.getProcessNodeId(), nodeName, null, node.getNodeStatus()));
            }
        });

        return nodeList;
    }

    private static void addNode(List<ApiSouProcessNodeVO> nodeList, SouProcessNodeEnum node, Enable enabled, SouProcessNode processNode) {
        if(ObjectUtils.allNotNull(node, enabled, processNode)) {
            nodeList.add(doConvertSouVO(node, enabled, processNode));
        }
    }

    private static ApiSouProcessNodeVO doConvertSouVO(SouProcessNodeEnum node, Enable enabled, SouProcessNode processNode) {
        ApiSouProcessNodeVO vo = new ApiSouProcessNodeVO();
        vo.setProcessConfigId(processNode.getProcessConfigId());
        vo.setProcessNodeId(processNode.getProcessNodeId());
        vo.setProcessNode(node.name());
        vo.setEnabled(enabled);
        vo.setNodeStatus(processNode.getNodeStatus());
        return vo;
    }
}
