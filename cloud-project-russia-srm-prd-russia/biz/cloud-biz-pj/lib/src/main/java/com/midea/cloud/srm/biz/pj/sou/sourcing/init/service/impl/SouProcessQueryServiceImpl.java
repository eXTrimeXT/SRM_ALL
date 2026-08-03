package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessNodeDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process.ApiSouProcessJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.process.ApiSouProcessQueryHandler;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.process.ApiSouProcessConfigVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 寻源 - 流程控制 - 信息查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouProcessQueryServiceImpl implements SouProcessQueryService {

    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;

    /**
     * 流程配置列表查询
     */
    @Override
    public List<ApiSouProcessConfigVO> listProcessConfigs(ApiSouProcessConfigQueryDTO queryParam) {
        // 1: 入参格式化
        queryParam.formatParams();
        // 2: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<SouProcessConfig> processConfigList = souProcessConfigDao.lambdaQuery()
                .like(queryParam.getProcessConfigName() != null, SouProcessConfig::getProcessConfigName, queryParam.getProcessConfigName())
                .eq(queryParam.getSouType() != null, SouProcessConfig::getSouType, queryParam.getSouType())
                .eq(queryParam.getStatus() != null, SouProcessConfig::getProcessStatus, queryParam.getStatus())
                .eq(queryParam.getPublishScope() != null, SouProcessConfig::getPublishScope, queryParam.getPublishScope())
                .eq(queryParam.getScoreRuleType() != null, SouProcessConfig::getScoreRuleType, queryParam.getScoreRuleType())
                .orderByDesc(SouProcessConfig::getCreationDate)
                .list();
        List<ApiSouProcessConfigVO> voList = ApiSouProcessConfigVO.convertApiVO(processConfigList);
        // 3: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(queryParam.getSouType() != null ? queryParam.getSouType() : SouTypeEnum.DEFAULT.name(), ApiSouProcessQueryHandler.class)
                .doHandlerAfterListProcessConfigs(queryParam, voList);
    }

    /**
     * 查询指定流程配置信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public SouProcessConfig getProcessConfig(long processConfigId, @Nullable Long vendorId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(souType), "souType不能为DEFAULT");
        SouProcessConfig processConfig = SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class)
                .judgeGetProcessConfigAuth(processConfigId, vendorId);
        // 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessQueryHandler.class)
                .doHandlerAfterGetProcessConfig(processConfigId, vendorId, souType, processConfig);
    }

    /**
     * 根据寻源单ID查询流程节点信息
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouProcessNodeVO> listProcessNodes(long projectId, String souType) {
        souType = StringUtils.trimToNull(souType);
        AssertUtils.notNull(souType, "缺少souType参数");
        AssertUtils.isFalse(SouTypeEnum.DEFAULT.name().equals(souType), "souType不能为DEFAULT");
        // 1: 校验操作条件/权限
        Long processConfigId = SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessJudgeHandler.class)
                .judgeListProcessNodesAuth(projectId, souType);
        // 2: 查询数据
        if (processConfigId != null) {
            SouProcessConfig processConfig = souProcessConfigDao.getById(processConfigId);
            List<SouProcessNode> processNodeList = souProcessNodeDao.lambdaQuery()
                    .eq(SouProcessNode::getProcessConfigId, processConfigId)
                    .eq(SouProcessNode::getProjectId, projectId)
                    .list();
            List<ApiSouProcessNodeVO> voList = ApiSouProcessNodeVO.convertSouVO(processConfig, processNodeList);
            // 3: 行业包额外处理(后置)
            return SouActiveBeanUtils.getActiveBean(souType, ApiSouProcessQueryHandler.class).doHandlerAfterListProcessNodes(projectId, souType, voList);
        } else {
            return Collections.emptyList();
        }
    }

}
