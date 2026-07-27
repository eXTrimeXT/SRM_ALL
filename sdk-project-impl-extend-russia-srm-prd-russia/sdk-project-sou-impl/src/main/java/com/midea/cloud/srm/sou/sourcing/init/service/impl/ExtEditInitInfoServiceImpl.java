package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.google.common.collect.Lists;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.*;
import com.midea.cloud.srm.sou.inq.ext.plugin.event.init.editrequire.ExtInqSouRequireEditHandler;
import com.midea.cloud.srm.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtEditInitInfoService;
import com.midea.cloud.srm.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiSouInitEventHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiSouInitJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.ApiSouProjectEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.SouProjectEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.SouRequireEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.ApiSouVendorEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/3/20 15:34
 *  修改内容:
 * </pre>
 */
@Slf4j
@Service
public class ExtEditInitInfoServiceImpl implements IExtEditInitInfoService {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouCurrencyDAO souCurrencyDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private SouItemLadderDAO souItemLadderDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private SouVendorAuthDAO souVendorAuthDAO;
    @Autowired
    private SouRoundDAO souRoundDAO;
    @Autowired
    private SouProcessConfigDAO souProcessConfigDAO;
    @Autowired
    private SouFileDAO souFileDAO;
    @Autowired
    private SouProcessEventService souProcessEventService;
    @Autowired
    private SouProcessNodeDAO souProcessNodeDAO;
    @Autowired
    private SouFileConfigDAO souFileConfigDAO;
    @Autowired
    private SouGroupDAO souGroupDAO;

    private final static int NUM2=2;
    private final static int NUM3=3;
    private final static int NUM4=4;
    /**
     * 立项信息整体暂存/提交
     *
     * @param param   立项信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editInitInfo(ApiSouInitDTO param, String souType) {
        AssertUtils.notNull(param.getCreateStep(), "缺少createStep参数");

        // 1: 保存立项数据
        long b1 = System.currentTimeMillis();
        if (param.getCreateStep().getIndex() >= 1 && param.getProjectInfo() != null) {
            param.getProjectInfo().setTempSave(param.getCreateStep().getIndex() <= 1 && param.isTempSave());
            this.editProject(param.getProjectInfo(), param.isCopy(), souType);
            param.setProjectId(param.getProjectInfo().getProject().getProjectId());
        }
        long e1 = System.currentTimeMillis();
        long s1 = b1 - e1;
        log.info("editInitInfo方法s1执行时间为：{} 毫秒", s1);
        // 2: 保存项目需求
        if (param.isCopy() || (param.getCreateStep().getIndex() >= NUM2)) {
            if (param.getRequireInfo() != null) {
                param.getRequireInfo().setProjectId(param.getProjectId());
                param.getRequireInfo().setTempSave(param.getCreateStep().getIndex() <= 2 && param.isTempSave());
                if (param.getRequireInfo().getQuoteTempId() == null && param.getProjectInfo() != null && param.getProjectInfo().getProject() != null) {
                    param.getRequireInfo().setQuoteTempId(param.getProjectInfo().getProject().getQuoteTempId());
                    param.getRequireInfo().setQuoteTempName(param.getProjectInfo().getProject().getQuoteTempName());
                }
                this.editRequires(param.getRequireInfo(), param.isCopy(), param.getCurrentUserId(), souType);
            }
        }
        long e2 = System.currentTimeMillis();
        long s2 = e2 - e1;
        log.info("editInitInfo方法s2执行时间为：{} 毫秒", s2);
        // 3: 保存邀请供应商
        if (param.isCopy() || (param.getCreateStep().getIndex() >= NUM3)) {
            if (param.getVendorInfo() != null) {
                SouProject souProject = souProjectDAO.getById(param.getProjectId());
                if (SouPublishScopeEnum.INVITE_TENDER.equals(souProject.getPublishScope())) {
                    param.getVendorInfo().setProjectId(param.getProjectId());
                    param.getVendorInfo().setTempSave(param.getCreateStep().getIndex() <= 3 && param.isTempSave());
                    this.editVendors(param.getVendorInfo(), param.isCopy(), souType);
                }
            }
        }
        long e3 = System.currentTimeMillis();
        long s3 = e3 - e2;
        log.info("editInitInfo方法s3执行时间为：{} 毫秒", s3);
        // 4: 评分规则
        if (param.isCopy() || (param.getCreateStep().getIndex() >= NUM4 && param.getScoreInfo() != null)) {
            ApiSouInitScoreInfoDTO dto = new ApiSouInitScoreInfoDTO();
            {
                dto.setProjectId(param.getProjectId());
                dto.setScoreRuleType(param.getScoreInfo().getScoreRuleType());
                dto.setScoreTemplateId(param.getScoreInfo().getScoreTemplateId());
                dto.setTempSave(param.isTempSave());
            }
            this.editScoreRule(dto, param.isCopy(), souType);
        }
        long e4 = System.currentTimeMillis();
        long s4 = e4 - e3;
        log.info("editInitInfo方法s4执行时间为：{} 毫秒", s4);
    }

    /**
     * 编辑/提交寻源基本信息
     *
     * @param param   寻源基本信息
     * @param isCopy  true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        if (param.getProject().getProjectId() != null) {
            SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeEditProjectAuth(param.getProject().getProjectId(), souType);
        }
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditProject(param, isCopy, souType);
        // 4: 入参校验+转换处理
        SouProjectEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouProjectEditHandler.class).formatValidateAndConvert(param, isCopy, souType);
        // 5: 保存数据
        try {
            souProjectDAO.saveOrUpdate(po.getSouProject());
        } catch (DuplicateKeyException e) {
            log.error("询价单标题重复！", e);
            throw new BaseException("询价单标题重复！");
        }
        if (!CollectionUtils.isEmpty(po.getGroupList())) {
            souGroupDAO.saveOrUpdateForceNull(po.getSouProject().getProjectId(), po.getGroupList(), SouGroup::getProjectId);
        }
        if (!CollectionUtils.isEmpty(po.getCurrencyList())) {
            souCurrencyDAO.saveOrUpdate(po.getSouProject().getProjectId(), po.getCurrencyList(), SouCurrency::getProjectId);
        }
        List<SouFile> souFileList = po.getSouFileList();
        // 处理附件保存
        souFileDAO.lambdaUpdate().eq(SouFile::getProjectId, po.getSouProject().getProjectId()).remove();
        if (!CollectionUtils.isEmpty(souFileList)) {
            souFileDAO.saveBatch(souFileList);
        }
        souFileConfigDAO.saveOrUpdate(po.getSouProject().getProjectId(), po.getFileConfigList(), SouFileConfig::getProjectId);
        // 6: 创建流程节点信息
        if (po.getSouProject().getProcessConfigId() != null) {
            long existCount = souProcessNodeDAO.lambdaQuery().eq(SouProcessNode::getProjectId, po.getSouProject().getProjectId()).count();
            Long processConfigId = po.getSouProject().getProcessConfigId();
            if (existCount <= 0 && Objects.nonNull(processConfigId)) {
                souProcessEventService.createProcessNodes(processConfigId, po.getSouProject().getProjectId(), souType);
            }
        }
        // 7: 更新节点信息
        if (!isCopy) {
            souProcessEventService.updateProcessNodeStatusForInit(po.getSouProject().getProjectId(), SouProcessNodeEnum.projectInfo.name(),
                    param.isTempSave() ? Enable.N : Enable.Y, souType);
        }
        // 8: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterEditProject(param, isCopy, souType, po);
    }

    /**
     * 编辑/提交寻源需求信息
     *
     * @param param   物料需求信息
     * @param isCopy  true-单据复制场景
     * @param userId  用户ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType) {
        // 1: 入参格式化
        long e1 = System.currentTimeMillis();
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeEditRequireAuth(param.getProjectId(), isCopy, souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditRequires(param, isCopy, userId, souType);
        long e3 = System.currentTimeMillis();
        long s3 = e3 - e1;
        log.info("editRequires方法s3执行时间为：{} 毫秒", s3);
        // 4: 入参校验+转换处理
        SouRequireEditPO po = SouActiveBeanUtils.getActiveBean(souType, ExtInqSouRequireEditHandler.class)
                .formatValidateAndConvert(param, isCopy, userId, souType);
        long e4 = System.currentTimeMillis();
        long s4 = e4 - e3;
        log.info("editRequires方法s4执行时间为：{} 毫秒", s4);
        // 5: 保存数据
        souProjectDAO.forceUpdateBatchById(Lists.newArrayList(po.getProject()));
        souItemDAO.saveOrUpdate(param.getProjectId(), po.getSouItemList(), SouItem::getProjectId);
        souItemLadderDAO.saveOrUpdate(param.getProjectId(), po.getLadderList(), SouItemLadder::getProjectId);
        long e5 = System.currentTimeMillis();
        long s5 = e5 - e4;
        log.info("editRequires方法s5执行时间为：{} 毫秒", s5);
        // 6: 更新流程节点信息
        if (!isCopy) {
            souProcessEventService.updateProcessNodeStatusForInit(param.getProjectId(), SouProcessNodeEnum.requireInfo.name(),
                    param.isTempSave() ? Enable.N : Enable.Y, souType);
        }
        long e6 = System.currentTimeMillis();
        long s6 = e6 - e5;
        log.info("editRequires方法s6执行时间为：{} 毫秒", s6);
        // 7: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterEditRequires(param, isCopy, userId, souType, po);
        long e7 = System.currentTimeMillis();
        long s7 = e7 - e6;
        log.info("editRequires方法s7执行时间为：{} 毫秒", s7);
        // 8: 自动审批通过
        if (!isCopy) {
            this.autoSubmitPass(po.getProject().getProjectId(), SouProcessNodeEnum.requireInfo, souType);
        }
        long e8 = System.currentTimeMillis();
        long s8 = e8 - e7;
        log.info("editRequires方法s8执行时间为：{} 毫秒", s8);
    }

    /**
     * 编辑/提交邀请供应商信息
     *
     * @param param   供应商信息
     * @param isCopy  true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeEditVendorsAuth(param.getProjectId(), isCopy, souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditVendors(param, isCopy, souType);
        // 4: 入参校验+转换处理
        SouVendorEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorEditHandler.class).formatValidateAndConvert(param, isCopy, souType);
        // 5: 保存数据
        souVendorDAO.saveOrUpdate(param.getProjectId(), po.getVendorList(), SouVendor::getProjectId);
        souVendorAuthDAO.saveOrUpdate(param.getProjectId(), po.getAuthList(), SouVendorAuth::getProjectId);
        // 6: 更新节点信息
        if (!isCopy) {
            souProcessEventService.updateProcessNodeStatusForInit(param.getProjectId(), SouProcessNodeEnum.inviteVendor.name(),
                    param.isTempSave() ? Enable.N : Enable.Y, souType);
        }
        // 7: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterEditVendors(param, isCopy, souType, po);
        // 8: 自动审批通过
        if (!isCopy) {
            this.autoSubmitPass(param.getProjectId(), SouProcessNodeEnum.inviteVendor, souType);
        }
    }

    /**
     * 选定评分规则
     *
     * @param param   评分规则信息
     * @param isCopy  true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editScoreRule(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeEditScoreRuleAuth(param, isCopy, souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditScoreRule(param, isCopy, souType);
        // 4: 更新寻源单信息
        souProjectDAO.lambdaUpdate()
                .set(SouProject::getScoreRuleType, param.getScoreRuleType())
                .set(SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(param.getScoreRuleType()), SouProject::getScoreTemplateId, param.getScoreTemplateId())
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        // 5: 更新节点信息
        if (!isCopy) {
            souProcessEventService.updateProcessNodeStatusForInit(param.getProjectId(), SouProcessNodeEnum.scoreRule.name(),
                    param.isTempSave() ? Enable.N : Enable.Y, souType);
        }
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterEditScoreRule(param, isCopy, souType);
        // 7: 自动审批通过
        if (!isCopy) {
            this.autoSubmitPass(param.getProjectId(), SouProcessNodeEnum.scoreRule, souType);
        }
    }

    /**
     * 未开启立项审批时，自动提交审批通过
     *
     * @param projectId   寻源单ID{@link SouProject#getProjectId}
     * @param processNode 流程节点，说明当前处于哪个节点步骤中
     * @param souType     寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void autoSubmitPass(long projectId, SouProcessNodeEnum processNode, String souType) {
        // 1: 校验操作条件/权限
        SouProject project = souProjectDAO.getById(projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("非法的操作，寻源类型[{0}]与寻源单不符"), souType);
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "非拟定状态，不能操作");
        SouProcessConfig processConfig = souProcessConfigDAO.getById(project.getProcessConfigId());
        if (processConfig == null) {
            return;
        }
        if (Enable.Y.equals(processConfig.getCreateApproval())) {
            return;
        }
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeAutoSubmitPass(projectId, processNode, souType);
        // 3: 流程节点状态变更
        Map<String/* processNode */, Enable> processNodeMap = souProcessNodeDAO.list(SouProcessNode::getProjectId, projectId)
                .stream().collect(Collectors.toMap(SouProcessNode::getProcessNode, SouProcessNode::getNodeStatus));
        boolean canDone;
        switch (processNode) {
            // 项目信息
            case projectInfo:
                canDone = false;
                break;
            // 项目需求(已完成该节点，且无"邀请供应商"、"评分规则")
            case requireInfo:
                canDone = Enable.Y.equals(processNodeMap.get(SouProcessNodeEnum.requireInfo.name()))
                        && Enable.N.equals(processConfig.getInviteVendor())
                        && Enable.N.equals(processConfig.getScoreRule());
                break;
            // 邀请供应商(已完成该节点，且无"评分规则")
            case inviteVendor:
                canDone = Enable.Y.equals(processNodeMap.get(SouProcessNodeEnum.inviteVendor.name()))
                        && Enable.N.equals(processConfig.getScoreRule());
                break;
            // 评分规则(已完成该节点)
            case scoreRule:
                canDone = Enable.Y.equals(processNodeMap.get(SouProcessNodeEnum.scoreRule.name()));
                break;
            default:
                throw new IllegalArgumentException(MessageFormat.format(LocaleHandler.getLocaleMsg("错误的方法调用{0}"), processNode.name()));
        }
        if (!canDone) {
            return;
        }
        // 4: 提交审批
        this.callbackAfterApprovalSubmit(projectId, souType);
        // 5: 审批通过
        this.callbackAfterApprovalPass(projectId, souType);
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterAutoSubmitPass(projectId, processNode, souType);
    }

    /**
     * 立项审批提交后的回调处理
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalSubmit(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeCallbackAfterApprovalSubmitAuth(projectId, souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeApprovalSubmit(projectId, souType);
        // 3: 更新数据
        souProjectDAO.lambdaUpdate()
                .set(SouProject::getCurrentRound, 1)
                .set(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.SUBMITTED)
                .eq(SouProject::getProjectId, projectId)
                .eq(SouProject::getProjectStatus, SouProjectStatusEnum.DRAFT)
                .in(SouProject::getCreateApprovalStatus,
                        SouApprovalStatusEnum.DRAFT,
                        SouApprovalStatusEnum.ABANDONED,
                        SouApprovalStatusEnum.REJECTED,
                        SouApprovalStatusEnum.WITHDRAW)
                .update();
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterApprovalSubmit(projectId, souType);
    }

    /**
     * 立项审批通过后的回调处理
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalPass(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeCallbackAfterApprovalPassAuth(projectId, souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeApprovalPass(projectId, souType);
        // 3: 更新寻源单状态等信息
        boolean hasSignUpNode = Enable.Y.equals(souProcessConfigDAO.getById(project.getProcessConfigId()).getSignUpManagement());
        Date now = new Date();
        souProjectDAO.lambdaUpdate()
                // 报名开始时间
                .set(hasSignUpNode, SouProject::getSignUpStartTime, now)
                // 寻源状态
                .set(SouProject::getProjectStatus, hasSignUpNode ?
                        (project.getSignUpEndTime().after(now) ? SouProjectStatusEnum.ACCEPT_SIGN_UP : SouProjectStatusEnum.SIGN_UP_END)
                        :
                        (project.getOrderStartTime().after(now) ? SouProjectStatusEnum.ORDER_NOT_START :
                                project.getOrderEndTime().after(now) ? SouProjectStatusEnum.ACCEPT_ORDER : SouProjectStatusEnum.ORDER_END))
                // 发布时间
                .set(SouProject::getPublishTime, now)
                // 立项审批状态
                .set(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.APPROVED)
                .eq(SouProject::getProjectId, projectId)
                .eq(SouProject::getProjectStatus, SouProjectStatusEnum.DRAFT)
                .eq(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.SUBMITTED)
                .update();
        // 4: 新增轮次
        long roundCount = souRoundDAO.lambdaQuery()
                .eq(SouRound::getProjectId, projectId)
                .count();
        if (roundCount <= 0) {
            SouRound round = new SouRound();
            round.setRoundId(IdGenrator.generate());
            round.setProjectId(projectId);
            round.setRound(1);
            round.setOrderStartTime(project.getOrderStartTime());
            round.setOrderEndTime(project.getOrderEndTime());
            round.setHasPublishResult(Enable.N);
            round.setPublishResultTime(null);
            // 本轮应报价供应商数量
            if (SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope())) {
                round.setInviteCount(0);
            } else {
                round.setInviteCount(Math.toIntExact(souVendorDAO.lambdaQuery()
                        .eq(SouVendor::getProjectId, projectId)
                        .count()));
            }
            // 本轮已报价供应商数量
            round.setOrderCount(0);
            // 商务开标信息
            round.setBusinessOpen(Enable.N);
            round.setBusinessOpen(null);
            // 报价解密信息
            round.setPriceDecrypt(Enable.N);
            round.setPriceDecryptTime(null);
            souRoundDAO.save(round);
        }
        // 5: 更新流程节点状态
        souProcessEventService.updateProcessNodeStatus(projectId, SouProcessNodeEnum.createApproval.name(), Enable.Y, souType);
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterApprovalPass(projectId, souType);
    }
}
