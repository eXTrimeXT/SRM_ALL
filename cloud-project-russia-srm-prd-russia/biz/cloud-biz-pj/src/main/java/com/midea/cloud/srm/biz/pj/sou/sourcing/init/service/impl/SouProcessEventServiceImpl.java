package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessNodeDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process.ApiSouProcessEditHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process.ApiSouProcessEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process.ApiSouProcessJudgeHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessNodeStatusChangeDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessConfigStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 寻源 - 流程信息 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouProcessEventServiceImpl implements SouProcessEventService {

    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;
    @Autowired
    private SouProjectDAOImpl souProjectDao;

    /**
     * 编辑/提交流程配置
     *
     * @param param      编辑信息
     * @param isTempSave true-暂存/false-提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public long/* processConfigId */ editProcessConfig(SouProcessConfig param, boolean isTempSave) {
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouProcessJudgeHandler.class).judgeEditProcessConfigAuth(param.getProcessConfigId());
        /* 2: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouProcessEventHandler.class).doHandlerBeforeEditProcessConfig(param, isTempSave);
        /* 3: 入参校验与数据转换 */
        SouProcessConfig entity = SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouProcessEditHandler.class)
                .formatValidateAndConvert(param, isTempSave);
        /* 4: 保存数据 */
        souProcessConfigDao.saveOrUpdate(entity);
        /* 5: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouProcessEventHandler.class).doHandlerAfterEditProcessConfig(param, isTempSave);
        /* 6: 返回数据 */
        return entity.getProcessConfigId();
    }

    /**
     * 生效流程配置
     *
     * @param processConfigId 流程配置ID
     * @param souType         寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void validProcessConfig(long processConfigId, String souType) {
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class).judgeValidProcessConfigAuth(processConfigId);
        /* 2: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerBeforeValidProcessConfig(processConfigId, souType);
        /* 3: 更新状态 */
        souProcessConfigDao.lambdaUpdate()
                .set(SouProcessConfig::getProcessStatus, SouProcessConfigStatusEnum.VALID)
                .eq(SouProcessConfig::getProcessConfigId, processConfigId)
                .update();
        /* 4: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerAfterValidProcessConfig(processConfigId, souType);
    }

    /**
     * 失效流程配置
     *
     * @param processConfigId 流程配置ID
     * @param souType         寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void invalidProcessConfig(long processConfigId, String souType) {
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class).judgeInvalidProcessConfigAuth(processConfigId);
        /* 2: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerBeforeInvalidProcessConfig(processConfigId, souType);
        /* 3: 更新状态 */
        souProcessConfigDao.lambdaUpdate()
                .set(SouProcessConfig::getProcessStatus, SouProcessConfigStatusEnum.INVALID)
                .eq(SouProcessConfig::getProcessConfigId, processConfigId)
                .update();
        /* 4: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerAfterInvalidProcessConfig(processConfigId, souType);
    }

    /**
     * 删除流程配置
     *
     * @param processConfigId 流程配置ID
     * @param souType         寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouProcessConfig removeProcessConfig(long processConfigId, String souType) {
        /* 1: 校验操作条件/权限 */
        SouProcessConfig processConfig = SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class)
                .judgeRemoveProcessConfigAuth(processConfigId);
        /* 2: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerBeforeRemoveProcessConfig(processConfigId, souType);
        /* 3: 更新状态 */
        souProcessConfigDao.lambdaUpdate()
                .eq(SouProcessConfig::getProcessConfigId, processConfigId)
                .eq(SouProcessConfig::getProcessStatus, SouProcessConfigStatusEnum.DRAFT)
                .remove();
        /* 4: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerAfterRemoveProcessConfig(processConfigId, souType, processConfig);
    }

    /**
     * 创建寻源单关联的流程节点
     *
     * @param processConfigId 寻源配置ID
     * @param projectId       寻源单ID
     * @param souType         寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void createProcessNodes(long processConfigId, long projectId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(souType), "souType不能为DEFAULT");
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class).judgeCreateProcessNodesAuth(processConfigId, projectId);
        /* 2: 构建数据 */
        List<SouProcessNode> nodeList = new ArrayList<>(SouProcessNodeEnum.values().length);
        for (SouProcessNodeEnum nodeEnum : SouProcessNodeEnum.values()) {
            SouProcessNode node = new SouProcessNode();
            nodeList.add(node);
            /* ID */
            node.setProcessNodeId(IdGenrator.generate());
            /* 流程配置ID */
            node.setProcessConfigId(processConfigId);
            /* 寻源单ID */
            node.setProjectId(projectId);
            /* 流程节点 */
            node.setProcessNode(nodeEnum.name());
            /* 状态 */
            node.setNodeStatus(Enable.N);
        }
        /* 3: 保存数据 */
        souProcessNodeDao.saveBatch(nodeList);
        /* 4: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessEventHandler.class).doHandlerAfterCreateProcessNodes(processConfigId, projectId, souType);
    }

    /**
     * 采购商端: 更新寻源单关联的流程节点状态(仅更新指定的节点)
     *
     * @param param   节点信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateProcessNodeStatus(ApiSouProcessNodeStatusChangeDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class).judgeUpdateProcessNodeStatusAuth(param, souType);
        /* 2: 获取需要更新的数据 */
        List<SouProcessNode> nodeList;
        {
            nodeList = souProcessNodeDao.lambdaQuery()
                    .eq(SouProcessNode::getProjectId, param.getProjectId())
                    .list();
            Map<String/* nodeName */, Enable> nodeStatusMap = param.getNodeList().stream()
                    .collect(Collectors.toMap(ApiSouProcessNodeStatusChangeDTO.NodeDetail::getNodeName, ApiSouProcessNodeStatusChangeDTO.NodeDetail::getStatus));
            Iterator<SouProcessNode> iterator = nodeList.iterator();
            while (iterator.hasNext()) {
                SouProcessNode node = iterator.next();
                Enable status = nodeStatusMap.get(node.getProcessNode());
                if (status == null) {
                    iterator.remove();
                    continue;
                }
                node.setNodeStatus(status);
            }
        }
        /* 3: 更新节点状态 */
        if (!nodeList.isEmpty()) {
            souProcessNodeDao.updateBatchById(nodeList);
        }
    }

    /**
     * 采购商端: 更新寻源单关联的流程节点状态
     *
     * @param projectId 寻源单ID
     * @param nodeName  需要更新状态的节点{@link SouProcessNodeEnum}
     * @param flag      更新状态
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public void updateProcessNodeStatus(long projectId, String nodeName, Enable flag, String souType) {
        nodeName = StringUtils.trimToNull(nodeName);
        AssertUtils.notNull(nodeName, "缺少nodeName参数");
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(souType), "souType不能为DEFAULT");

        ApiSouProcessNodeStatusChangeDTO dto = new ApiSouProcessNodeStatusChangeDTO();
        {
            dto.setProjectId(projectId);
            dto.setNodeList(new HashSet<>(2));
            ApiSouProcessNodeStatusChangeDTO.NodeDetail node = new ApiSouProcessNodeStatusChangeDTO.NodeDetail();
            node.setNodeName(nodeName);
            node.setStatus(flag);
            dto.getNodeList().add(node);
        }

        this.updateProcessNodeStatus(dto, souType);
    }

    /**
     * 采购商端: 专用于寻源立项阶段的节点更新
     * PS: 例如要更新'项目信息'节点，则'物料需求'、'邀请供应商'、'评分规则'等节点均置为灰色
     *
     * @param projectId 寻源单ID
     * @param nodeName  需要更新状态的节点{@link SouProcessNodeEnum}
     * @param flag      更新状态
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public void updateProcessNodeStatusForInit(long projectId, String nodeName, Enable flag, String souType) {
        nodeName = StringUtils.trimToNull(nodeName);
        AssertUtils.notNull(nodeName, "缺少nodeName参数");
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(souType), "souType不能为DEFAULT");

        boolean hasInviteVendorNode;
        {
            SouProject project = souProjectDao.getById(projectId);
            AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), projectId);
            if (project.getProcessConfigId() == null) {
                return;
            }
            SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
            hasInviteVendorNode = Enable.Y.equals(processConfig.getInviteVendor());
        }

        Map<String/* nodeName */, ApiSouProcessNodeStatusChangeDTO.NodeDetail> nodeMap = new HashMap<>(8);
        {
            ApiSouProcessNodeStatusChangeDTO.NodeDetail node = new ApiSouProcessNodeStatusChangeDTO.NodeDetail();
            node.setNodeName(SouProcessNodeEnum.projectInfo.name());
            node.setStatus(Enable.N);
            nodeMap.put(node.getNodeName(), node);
            node = new ApiSouProcessNodeStatusChangeDTO.NodeDetail();
            node.setNodeName(SouProcessNodeEnum.requireInfo.name());
            node.setStatus(Enable.N);
            nodeMap.put(node.getNodeName(), node);
            node = new ApiSouProcessNodeStatusChangeDTO.NodeDetail();
            node.setNodeName(SouProcessNodeEnum.inviteVendor.name());
            node.setStatus(Enable.N);
            nodeMap.put(node.getNodeName(), node);
            node = new ApiSouProcessNodeStatusChangeDTO.NodeDetail();
            node.setNodeName(SouProcessNodeEnum.scoreRule.name());
            node.setStatus(Enable.N);
            nodeMap.put(node.getNodeName(), node);
        }
        switch (nodeName) {
//            项目信息
            case "projectInfo":
                nodeMap.get(SouProcessNodeEnum.projectInfo.name()).setStatus(flag);
                nodeMap.get(SouProcessNodeEnum.requireInfo.name()).setStatus(Enable.N);
                nodeMap.get(SouProcessNodeEnum.inviteVendor.name()).setStatus(Enable.N);
                nodeMap.get(SouProcessNodeEnum.scoreRule.name()).setStatus(Enable.N);
                break;
//            物料需求
            case "requireInfo":
                nodeMap.get(SouProcessNodeEnum.projectInfo.name()).setStatus(Enable.Y);
                nodeMap.get(SouProcessNodeEnum.requireInfo.name()).setStatus(flag);
                nodeMap.get(SouProcessNodeEnum.inviteVendor.name()).setStatus(Enable.N);
                nodeMap.get(SouProcessNodeEnum.scoreRule.name()).setStatus(Enable.N);
                break;
//            邀请供应商
            case "inviteVendor":
                AssertUtils.isTrue(hasInviteVendorNode, "当前单据无邀请供应商节点，不能更新节点信息");
                nodeMap.get(SouProcessNodeEnum.projectInfo.name()).setStatus(Enable.Y);
                nodeMap.get(SouProcessNodeEnum.requireInfo.name()).setStatus(Enable.Y);
                nodeMap.get(SouProcessNodeEnum.inviteVendor.name()).setStatus(flag);
                nodeMap.get(SouProcessNodeEnum.scoreRule.name()).setStatus(Enable.N);
                break;
//            评分规则
            case "scoreRule":
                nodeMap.get(SouProcessNodeEnum.projectInfo.name()).setStatus(Enable.Y);
                nodeMap.get(SouProcessNodeEnum.requireInfo.name()).setStatus(Enable.Y);
                if (hasInviteVendorNode) {
                    nodeMap.get(SouProcessNodeEnum.inviteVendor.name()).setStatus(Enable.Y);
                }
                nodeMap.get(SouProcessNodeEnum.scoreRule.name()).setStatus(flag);
                break;
            default:
                throw new IllegalArgumentException("错误的方法调用");
        }

        this.updateProcessNodeStatus(new ApiSouProcessNodeStatusChangeDTO(projectId, new HashSet<>(nodeMap.values())), souType);
    }

}
