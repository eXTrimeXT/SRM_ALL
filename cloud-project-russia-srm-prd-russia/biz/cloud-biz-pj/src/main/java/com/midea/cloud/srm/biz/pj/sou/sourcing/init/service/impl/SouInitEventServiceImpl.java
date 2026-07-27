package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.impl;

import com.google.common.collect.Lists;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.ApiSouInitEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.ApiSouInitJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editproject.ApiSouProjectEditHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editproject.SouProjectEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editrequrie.ApiSouRequireEditHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editrequrie.SouRequireEditPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editvendor.ApiSouVendorEditHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.cooperate.SccNpmPrRequireHead;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 寻源 - 立项 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/19
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouInitEventServiceImpl implements SouInitEventService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouCurrencyDAOImpl souCurrencyDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouItemLadderDAOImpl souItemLadderDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouVendorAuthDAOImpl souVendorAuthDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouFileDAOImpl souFileDao;
    @Autowired
    private SouProcessEventService souProcessEventService;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;
    @Autowired
    private SouFileConfigDAOImpl souFileConfigDao;
    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouInitQueryService souInitQueryService;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;

    @Autowired
    QlOpenClient qlOpenClient;

    /**
     * 编辑/提交寻源基本信息
     *
     * @param param   寻源基本信息
     * @param isCopy  true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
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
            souProjectDao.saveOrUpdate(po.getSouProject());
        } catch (DuplicateKeyException e) {
            log.error("询价单标题重复！", e);
            throw new BaseException("询价单标题重复！");
        }
        if (!Objects.isNull(po.getCompSouProject())) {
            compSouProjectDao.saveOrUpdate(po.getCompSouProject());
        }
        if (!CollectionUtils.isEmpty(po.getCurrencyList())) {
            souCurrencyDao.saveOrUpdate(po.getSouProject().getProjectId(), po.getCurrencyList(), SouCurrency::getProjectId);
        }
        List<SouFile> souFileList = po.getSouFileList();
        // 处理附件保存
        souFileDao.lambdaUpdate().eq(SouFile::getProjectId, po.getSouProject().getProjectId()).remove();
        if (!CollectionUtils.isEmpty(souFileList)) {
            souFileDao.saveBatch(souFileList);
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
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditRequires(param, isCopy, userId, souType);
        // 4: 入参校验+转换处理
        SouRequireEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouRequireEditHandler.class)
                .formatValidateAndConvert(param, isCopy, userId, souType);
        // 5: 保存数据
        souProjectDao.forceUpdateBatchById(Lists.newArrayList(po.getProject()));
        souItemDao.saveOrUpdateBatch(po.getSouItemList());
        souItemLadderDao.saveOrUpdate(param.getProjectId(), po.getLadderList(), SouItemLadder::getProjectId);
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterEditRequires(param, isCopy, userId, souType, po);
    }

    /**
     * 编辑/提交邀请供应商信息
     *
     * @param param   供应商信息
     * @param isCopy  true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditVendors(param, isCopy, souType);
        // 4: 入参校验+转换处理
        SouVendorEditPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorEditHandler.class).formatValidateAndConvert(param, isCopy, souType);
        souVendorDao.saveOrUpdateBatch(po.getVendorList());
        souVendorAuthDao.saveOrUpdateBatch(po.getAuthList());
        // 7: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterEditVendors(param, isCopy, souType, po);
    }

    /**
     * 选定评分规则
     *
     * @param param   评分规则信息
     * @param isCopy  true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editScoreRule(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeEditScoreRuleAuth(param, isCopy, souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeEditScoreRule(param, isCopy, souType);
        // 4: 更新寻源单信息
        souProjectDao.lambdaUpdate()
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
        if (param.getCreateStep().getIndex() >= 1 && param.getProjectInfo() != null) {
            param.getProjectInfo().setTempSave(param.getCreateStep().getIndex() <= 1 && param.isTempSave());
            this.editProject(param.getProjectInfo(), param.isCopy(), souType);
            param.setProjectId(param.getProjectInfo().getProject().getProjectId());
        }
        int twoNum = 2;
        int threeNum = 3;
        int fourNum = 4;
        // 2: 保存项目需求
        if (param.isCopy() || (param.getCreateStep().getIndex() >= twoNum)) {
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
        // 3: 保存邀请供应商
        if (param.isCopy() || (param.getCreateStep().getIndex() >= threeNum)) {
            if (param.getVendorInfo() != null) {
                SouProject souProject = souProjectDao.getById(param.getProjectId());
                if (SouPublishScopeEnum.INVITE_TENDER.equals(souProject.getPublishScope())) {
                    param.getVendorInfo().setProjectId(param.getProjectId());
                    param.getVendorInfo().setTempSave(param.getCreateStep().getIndex() <= 3 && param.isTempSave());
                    this.editVendors(param.getVendorInfo(), param.isCopy(), souType);
                }
            }
        }
        // 4: 评分规则
        if (param.isCopy() || (param.getCreateStep().getIndex() >= fourNum && param.getScoreInfo() != null)) {
            ApiSouInitScoreInfoDTO dto = new ApiSouInitScoreInfoDTO();
            {
                dto.setProjectId(param.getProjectId());
                dto.setScoreRuleType(param.getScoreInfo().getScoreRuleType());
                dto.setScoreTemplateId(param.getScoreInfo().getScoreTemplateId());
                dto.setTempSave(param.isTempSave());
            }
            this.editScoreRule(dto, param.isCopy(), souType);
        }
    }

    /**
     * 删除寻源单
     *
     * @param projectId 寻源单ID
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouProject removeSou(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class)
                .judgeRemoveSouAuth(projectId);
        ApiSouInitDetailVO initInfo = souInitQueryService.getSouInitInfo(projectId, souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeRemoveSou(projectId, souType);
        // 3: 删除
        souProjectDao.lambdaUpdate().eq(SouProject::getProjectId, projectId).remove();
        souCurrencyDao.lambdaUpdate().eq(SouCurrency::getProjectId, projectId).remove();
        souItemDao.lambdaUpdate().eq(SouItem::getProjectId, projectId).remove();
        souItemLadderDao.lambdaUpdate().eq(SouItemLadder::getProjectId, projectId).remove();
        souFileDao.lambdaUpdate().eq(SouFile::getProjectId, projectId).remove();
        souFileConfigDao.lambdaUpdate().eq(SouFileConfig::getProjectId, projectId).remove();
        souGroupDao.lambdaUpdate().eq(SouGroup::getProjectId, projectId).remove();
        souVendorDao.lambdaUpdate().eq(SouVendor::getProjectId, projectId).remove();
        souVendorAuthDao.lambdaUpdate().eq(SouVendorAuth::getProjectId, projectId).remove();
        souProcessNodeDao.lambdaUpdate().eq(SouProcessNode::getProjectId, projectId).remove();
        // 4: 行业包处理
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterRemoveSou(projectId, souType, initInfo);
        return initInfo.getProjectInfo();
    }

    /**
     * 采购商端: 作废寻源单
     *
     * @param param   作废信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouProject cancelSou(ApiSouCancelDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouProject souProject = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeCancelSouAuth(param.getProjectId());
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeCancelSou(param, souType);
        // 4: 作废
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.CANCEL)
                .set(StringUtils.isNotBlank(param.getCancelReason()), SouProject::getCancelReason, param.getCancelReason())
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        // 5: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterCancelSou(param, souType);

        // 需要将招标池中“是否已创建标书”改为否
        // 招标计划拓展表  scc_npm_pr_require_head
        // 是否已创建标书  HAS_CREATE_sou   hasCreateSou
        SccNpmPrRequireHead sccNpmPrRequireHead = qlOpenClient.read(ContextPath.SUP_CE,"ExtPrSouRequirementHead",souProject.getSourceFromId(), SccNpmPrRequireHead.class);

        QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("ExtPrSouRequirementHead")
                .set(SccNpmPrRequireHead::getHasCreateSou, "N")
                .eq(SccNpmPrRequireHead::getRequirementHeadId,sccNpmPrRequireHead.getRequirementHeadId());
        qlOpenClient.update(ContextPath.SUP_CE, wrapper);

        log.info("===================");
        return souProject;
    }

    /**
     * 复制寻源单
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public long/* projectId */ copySou(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeCopyAuth(projectId, souType);
        // 2: 复制保存立项基本信息
        ApiSouProjectInfoDTO projectInfoDTO = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class)
                .doHandlerForCopyProjectInfo(projectId, souType);
        this.editProject(projectInfoDTO, true, souType);
        long newProjectId = projectInfoDTO.getProject().getProjectId();
        // 3: 复制保存立项物料需求
        ApiSouRequireInfoDTO requireInfoDTO = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class)
                .doHandlerForCopyRequireInfo(newProjectId, projectId, souType);
        this.editRequires(requireInfoDTO, true, null, souType);
        // 4: 复制保存立项邀请供应商
        SouProject newProject = souProjectDao.getById(newProjectId);
        if (SouPublishScopeEnum.INVITE_TENDER.equals(newProject.getPublishScope())) {
            Map<Long/* oldSouItemList */, Long/* newSouItemId */> souItemIdMap = new HashMap<>(64);
            {
                List<SouItem> oldItemList = souItemDao.lambdaQuery().eq(SouItem::getProjectId, projectId).orderByAsc(SouItem::getSortIndex).list();
                for (int i = 0; i < oldItemList.size(); i++) {
                    SouItem oldItem = oldItemList.get(i);
                    ApiSouItemDTO newItem = requireInfoDTO.getItemList().get(i);
                    souItemIdMap.put(oldItem.getSouItemId(), newItem.getSouItemId());
                }
            }
            ApiSouVendorInfoDTO vendorInfoDTO = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class)
                    .doHandlerForCopyVendorInfo(projectId, souType, newProjectId, souItemIdMap);
            this.editVendors(vendorInfoDTO, true, souType);
        }
        // 5: 复制保存评分规则
        ApiSouInitScoreInfoDTO scoreInfoDTO = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class)
                .doHandlerForCopyScoreInfo(projectId, souType, newProjectId);
        this.editScoreRule(scoreInfoDTO, true, souType);
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterCopySou(projectId, souType, newProjectId);

        return newProjectId;
    }

    /**
     * 未开启立项审批时，自动提交审批通过
     *
     * @param projectId   寻源单ID{@link SouProject#getProjectId}
     * @param processNode 流程节点，说明当前处于哪个节点步骤中
     * @param souType     寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void autoSubmitPass(long projectId, SouProcessNodeEnum processNode, String souType) {
        // 1: 校验操作条件/权限
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("非法的操作，寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("与寻源单不符"), souType);
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "非拟定状态，不能操作");
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeAutoSubmitPass(projectId, processNode, souType);
        // 4: 提交审批
        this.callbackAfterApprovalSubmit(projectId, souType);
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterAutoSubmitPass(projectId, processNode, souType);
    }

    /**
     * 立项审批提交后的回调处理
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalSubmit(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeCallbackAfterApprovalSubmitAuth(projectId, souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeApprovalSubmit(projectId, souType);
        // 3: 更新数据
        souProjectDao.lambdaUpdate()
                .set(SouProject::getCurrentRound, 1)
                .set(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.SUBMITTED)
                //.set(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.APPROVED)
//                .set(SouProject::getProjectStatus, SouProjectStatusEnum.ACCEPT_SIGN_UP)
                .set(SouProject::getSignUpStartTime, new Date())
                .set(SouProject::getPublishTime, new Date())
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
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalPass(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeCallbackAfterApprovalPassAuth(projectId, souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeApprovalPass(projectId, souType);
        // 3: 更新寻源单状态等信息
        boolean hasSignUpNode = SouProjectStatusEnum.SIGN_UP.equals(souProjectDao.getById(project.getProjectId()).getProjectStatus());
        Date now = new Date();
        souProjectDao.lambdaUpdate()
                // 报名开始时间
                .set(hasSignUpNode, SouProject::getSignUpStartTime, now)
                // 发布时间
                .set(SouProject::getPublishTime, now)
                // 立项审批状态
                .set(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.APPROVED)
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.ACCEPT_SIGN_UP)

                .eq(SouProject::getProjectId, projectId)
                .eq(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.SUBMITTED)
                .update();
        // 4: 新增轮次
        long roundCount = souRoundDao.lambdaQuery()
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
                round.setInviteCount(Math.toIntExact(souVendorDao.lambdaQuery()
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
            souRoundDao.save(round);
        }
        // 6: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterApprovalPass(projectId, souType);
    }

    /**
     * 立项审批未通过后的回调处理
     *
     * @param param   回调参数
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalUnPass(ApiSouCreateApprovalUnPassDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class)
                .judgeCallbackAfterApprovalUnPassAuth(param.getProjectId(), param.getCreateApprovalStatus(), souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerBeforeApprovalUnPass(param, souType);
        // 3: 更新信息
        souProjectDao.lambdaUpdate()
                .set(SouProject::getCreateApprovalStatus, param.getCreateApprovalStatus())
                .eq(SouProject::getProjectId, param.getProjectId())
                .eq(SouProject::getProjectStatus, SouProjectStatusEnum.DRAFT)
                .eq(SouProject::getCreateApprovalStatus, SouApprovalStatusEnum.SUBMITTED)
                .update();
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitEventHandler.class).doHandlerAfterApprovalUnPass(param, souType);
    }

}
