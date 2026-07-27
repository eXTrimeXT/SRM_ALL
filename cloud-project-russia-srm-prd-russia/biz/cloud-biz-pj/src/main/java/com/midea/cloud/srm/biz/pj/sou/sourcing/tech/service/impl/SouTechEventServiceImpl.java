package com.midea.cloud.srm.biz.pj.sou.sourcing.tech.service.impl;

import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessNodeDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.ApiSouTechEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.ApiSouTechJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.score.ApiSouTechScoreHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.score.SouTechEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.service.SouTechEventService;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechScoreDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessNode;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 寻源核心 - 技术标事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouTechEventServiceImpl implements SouTechEventService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouTechScoreHeadDAOImpl souTechScoreHeadDao;
    @Autowired
    private SouTechScoreLineDAOImpl souTechScoreLineDao;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;

    /**
     * 技术评分/代理评分
     * @param param 评分信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void techScore(ApiSouTechScoreDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        long groupId = SouActiveBeanUtils.getActiveBean(souType, ApiSouTechJudgeHandler.class)
                .judgeTechScoreAuth(param.getProjectId(), param.getGroupId(), param.getVendorId(), param.getCurrentUserId());
        param.setGroupId(groupId);
        // 3: 参数校验+数据转换
        SouTechEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouTechScoreHandler.class).formatValidateAndConvert(param);
        // 4: 保存数据
        souTechScoreHeadDao.saveOrUpdate(po.getScoreHead());
        souTechScoreLineDao.saveOrUpdateForceNull(po.getScoreHead().getTechScoreHeadId(), po.getScoreLineList(),
                SouTechScoreLine::getTechScoreHeadId);
        // 5: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouTechEventHandler.class).doHandlerAfterTechScore(param, souType);
    }

    /**
     * 技术开标
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void openTech(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouTechJudgeHandler.class).judgeOpenTechAuth(projectId, souType);
        // 2: 技术开标
        souProjectDao.lambdaUpdate()
                .set(SouProject::getTechOpen, Enable.Y)
                .set(SouProject::getTechOpenTime, new Date())
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.TECH_EVAL)
                .eq(SouProject::getProjectId, projectId)
                .eq(SouProject::getTechOpen, Enable.N)
                .update();
        // 3: 更新节点信息
        souProcessNodeDao.lambdaUpdate()
                .set(SouProcessNode::getNodeStatus, Enable.Y)
                .eq(SouProcessNode::getProjectId, projectId)
                .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.techManagement)
                .update();
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouTechEventHandler.class).doHandlerAfterOpenTech(projectId, souType);
    }

}
