package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.enums.SouBidProccessEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProcessNodeDAO;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProcessConfigMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class IExtSouProcessConfigServiceImpl extends ServiceImpl<ExtSouProcessConfigMapper, ExtSouProcessConfig> implements IExtSouProcessConfigService {

    @Autowired
    private SouProcessNodeDAO souProcessNodeDAO;

    @Autowired
    private IExtSouProjectService projectService;

    private static final String CONFIG_NAME = "DEFAULT_NPM_BID_PROCESS";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateDefaultProcessConfig(Long projectId) {

        ExtSouProject project = projectService.getById(projectId);
        AssertUtils.notNull(project, "招标信息不存在");

        if (!Objects.isNull(project.getProcessConfigId())) {
            return projectId;
        }

        //查询配置
        LambdaQueryWrapper<ExtSouProcessConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouProcessConfig::getProcessConfigName, CONFIG_NAME);
        List<ExtSouProcessConfig> processConfigList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(processConfigList)) {
            log.info("generateDefaultProcessConfig 无默认招标流程配置");
            return projectId;
        }

        ExtSouProcessConfig extSouProcessConfig = processConfigList.get(0);
        Map configMap = JSON.parseObject(JSON.toJSONString(extSouProcessConfig));

        project.setProcessConfigId(extSouProcessConfig.getProcessConfigId());

        projectService.updateById(project);


        LambdaQueryWrapper<SouProcessNode> nodeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        nodeLambdaQueryWrapper.eq(SouProcessNode::getProjectId, projectId);
        nodeLambdaQueryWrapper.in(SouProcessNode::getProcessNode, ApiSouProcessNodeVO.bidNodeList.stream().map(s -> s.name()).collect(Collectors.toList()));
        List<SouProcessNode> nodeList = souProcessNodeDAO.list(nodeLambdaQueryWrapper);
        Map<String, Long> nodeSet = nodeList.stream().collect(Collectors.toMap(s -> s.getProcessNode(), s -> s.getProcessNodeId(), (k1, k2) -> k2));

        List<SouProcessNode> saveList = new ArrayList<>();

        ApiSouProcessNodeVO.bidNodeList.stream().forEach(nodeEnum -> {
            SouProcessNode souProcessNode = new SouProcessNode();
            souProcessNode.setProcessConfigId(project.getProcessConfigId());
            souProcessNode.setProjectId(project.getProjectId());
            souProcessNode.setProcessNode(nodeEnum.name());

            if (nodeSet.containsKey(souProcessNode.getProcessNode())) {
                souProcessNode.setProcessNodeId(nodeSet.get(souProcessNode.getProcessNode()));
            } else {
                souProcessNode.setProcessNodeId(IdGenrator.generate());
                souProcessNode.setNodeStatus(Enable.N);
            }
            saveList.add(souProcessNode);
        });


        souProcessNodeDAO.saveOrUpdateBatch(saveList);

        return projectId;
    }

    @Override
    public Long updateNodeStatus(Long projectId, SouProcessNodeEnum nodeEnum, Enable nodeStatus, Boolean isTempSave) {
        if (isTempSave) {
            return projectId;
        }
        LambdaQueryWrapper<SouProcessNode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouProcessNode::getProjectId, projectId);
        queryWrapper.eq(SouProcessNode::getProcessNode, nodeEnum.name());
        List<SouProcessNode> souProcessNodeList = souProcessNodeDAO.list(queryWrapper);
        souProcessNodeList.stream().forEach(s -> s.setNodeStatus(ObjectUtils.defaultIfNull(nodeStatus, Enable.Y)));
        souProcessNodeDAO.updateBatchById(souProcessNodeList);
        return projectId;
    }

    @Override
    public ApiExtSouProcessConfigVo listSouProcessConfig(Long processConfigId) {
        ExtSouProcessConfig processConfig = this.getById(processConfigId);
        AssertUtils.notNull(processConfig, "流程配置不存在");
        ApiExtSouProcessConfigVo vo = new ApiExtSouProcessConfigVo();
        BeanCopyUtil.copyProperties(vo, processConfig);
        vo.initeExtField();
        return vo;
    }

    @Override
    public void fixNpmProcessAndNode(ExtSouProjectDto project) {
        //询比价招标不需要技术标、评分规则、技术标管理
        List<SouProcessNodeEnum> removeNodeList = new ArrayList<>();
        if(SouBidProccessEnum.INQUIRY.getCode().equals(project.getExtSouProcess()) && !Objects.isNull(project.getProcessConfig())) {
            removeNodeList.add(SouProcessNodeEnum.scoreRule);
            removeNodeList.add(SouProcessNodeEnum.techManagement);
        }
        //不需要保证金
        if(!YesOrNo.YES.getValue().equals(project.getExtEarnestFlag())) {
            removeNodeList.add(SouProcessNodeEnum.bondManagement);
        }

        removeNode(project.getProcessConfig(), project.getProcessNodeList(), removeNodeList);
    }

    private void removeNode(ApiExtSouProcessConfigVo processConfig, List<ApiSouProcessNodeVO> processNodeList, List<SouProcessNodeEnum> nodeEnums) {
        if(Objects.isNull(processConfig) || CollectionUtils.isEmpty(processNodeList)) {
            return;
        }
        //设置配置非必须
        nodeEnums.forEach(node -> {
            switch (node) {
                case scoreRule:
                    processConfig.setScoreRule(Enable.N);
                    break;
                case techManagement:
                    processConfig.setTechManagement(Enable.N);
                    break;
                case bondManagement:
                    processConfig.setBondManagement(Enable.N);
                    break;
                default:;
            }
        });

        List<String> nodeList = nodeEnums.stream().map(n -> n.name()).collect(Collectors.toList());

        //设置节点非显示
        Iterator<ApiSouProcessNodeVO> voIterator = processNodeList.iterator();
        while (voIterator.hasNext()) {
            ApiSouProcessNodeVO nodeVO = voIterator.next();
            if(nodeList.contains(nodeVO.getProcessNode())) {
                nodeVO.setEnabled(Enable.N);
                nodeVO.setNodeStatus(Enable.N);
            }
        }

    }
}
