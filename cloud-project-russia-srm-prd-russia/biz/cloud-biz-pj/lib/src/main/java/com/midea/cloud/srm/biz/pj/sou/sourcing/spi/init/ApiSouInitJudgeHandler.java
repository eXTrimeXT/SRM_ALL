package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editvendor.ApiSouVendorEditHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouInitScoreInfoDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 寻源openAPI - 立项服务接口判断
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouInitJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;
    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;

    @ApiOperation("是否可以查看寻源基本信息")
    public SouProject judgeGetProjectAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        return project;
    }

    @ApiOperation("当前是否可以复制询价单")
    public void judgeCopyAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
    }

    /**
     * 当前操作人是否可以编辑寻源项目信息
     */
    public SouProject judgeEditProjectAuth(Long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "非拟定状态，禁止修改寻源单");
        switch (project.getCreateApprovalStatus()) {
            //拟定
            case DRAFT:
                //已驳回
            case REJECTED:
                //已撤回
            case WITHDRAW:
                //已作废
            case ABANDONED:
                break;
                //已提交
            case SUBMITTED:
                throw new IllegalArgumentException("寻源单已提交审批，禁止修改");
                //已审批
            case APPROVED:
                throw new IllegalArgumentException("寻源单已审批通过，禁止修改");
            default:;
        }
        return project;
    }

    /**
     * 当前操作人是否可以编辑寻源项目需求
     */
    public void judgeEditRequireAuth(long projectId, boolean isCopy, String souType) {
        SouProject project = this.judgeEditProjectAuth(projectId, souType);
        if (!isCopy) {
            SouProcessNode node = souProcessNodeDao.lambdaQuery()
                    .eq(SouProcessNode::getProcessConfigId, project.getProcessConfigId())
                    .eq(SouProcessNode::getProjectId, projectId)
                    .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.projectInfo)
                    .one();
            AssertUtils.notNull(node, "在寻源流程中找不到'项目信息'节点记录");
            AssertUtils.isTrue(Enable.Y.equals(node.getNodeStatus()), "请先保存项目信息");
        }
    }

    /**
     * 当前操作人是否可以查看寻源供应商列表信息
     */
    public void judgeListVendorsAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("非法的操作，寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("与寻源单不符"), souType);
    }

    /**
     * 当前操作人是否可以编辑/提交供应商信息
     */
    public void judgeEditVendorsAuth(long projectId, boolean isCopy, String souType) {
        SouProject project = this.judgeEditProjectAuth(projectId, souType);
        if (!isCopy) {
            SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
            AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("流程配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), project.getProcessConfigId());
            AssertUtils.isTrue(Enable.Y.equals(processConfig.getInviteVendor()), "当前单据无邀请供应商节点");
            SouProcessNode node = souProcessNodeDao.lambdaQuery()
                    .eq(SouProcessNode::getProcessConfigId, project.getProcessConfigId())
                    .eq(SouProcessNode::getProjectId, projectId)
                    .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.requireInfo)
                    .one();
            AssertUtils.notNull(node, "在寻源流程中找不到'物料需求'节点记录");
            AssertUtils.isTrue(Enable.Y.equals(node.getNodeStatus()), "请先保存物料需求信息");
        }

        AssertUtils.notNull(project.getPublishScope(), "请选择发布范围");
        AssertUtils.notNull(project.getOrderWay(), "请选择报价方式");
        List<SouItem> itemList = souItemDao.lambdaQuery()
                .eq(SouItem::getProjectId, projectId)
                .list();
        AssertUtils.notEmpty(itemList, "请先维护物料需求信息");
        if (SouOrderWayEnum.COMBINED.equals(project.getOrderWay())) {
            boolean allHasGroup = itemList.stream().map(SouItem::getItemGroup).allMatch(Objects::nonNull);
            AssertUtils.isTrue(allHasGroup, "请先维护完整的物料需求信息");
        }
    }

    /**
     * 当前操作人是否可以选定寻源所需的评分规则
     */
    public void judgeEditScoreRuleAuth(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType) {
        SouProject project = this.judgeEditProjectAuth(param.getProjectId(), souType);
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        if (!isCopy) {
            AssertUtils.notNull(processConfig, LocaleHandler.getLocaleMsg("流程配置信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), project.getProcessConfigId());
            if (Enable.Y.equals(processConfig.getInviteVendor()) && Objects.equals(SouPublishScopeEnum.INVITE_TENDER, project.getPublishScope())) {
                SouProcessNode node = souProcessNodeDao.lambdaQuery()
                        .eq(SouProcessNode::getProcessConfigId, project.getProcessConfigId())
                        .eq(SouProcessNode::getProjectId, param.getProjectId())
                        .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.inviteVendor)
                        .one();
                AssertUtils.notNull(node, "在寻源流程中找不到'邀请供应商'节点记录");
                AssertUtils.isTrue(Enable.Y.equals(node.getNodeStatus()), "请先保存邀请供应商信息");
            } else {
                SouProcessNode node = souProcessNodeDao.lambdaQuery()
                        .eq(SouProcessNode::getProcessConfigId, project.getProcessConfigId())
                        .eq(SouProcessNode::getProjectId, param.getProjectId())
                        .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.requireInfo)
                        .one();
                AssertUtils.notNull(node, "在寻源流程中找不到'物料需求'节点记录");
                AssertUtils.isTrue(Enable.Y.equals(node.getNodeStatus()), "请先保存物料需求信息");
            }
        }

        if (SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(param.getScoreRuleType())) {
            if (param.getScoreTemplateId() == null) {
                AssertUtils.isTrue(param.isTempSave(), "请选择评分规则");
                return;
            }
            SouScoreRule scoreRule = souScoreRuleDao.getById(param.getScoreTemplateId());
            AssertUtils.notNull(scoreRule, LocaleHandler.getLocaleMsg("评分规则")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getScoreTemplateId());
            AssertUtils.isTrue(scoreRule.getSouType().equals(project.getSouType()), "评分规则不属于当前寻源类型");
            AssertUtils.isTrue(SouScoreRuleStatusEnum.VALID.equals(scoreRule.getScoreRuleStatus()), "评分规则不是有效状态，不能使用");
            if (processConfig != null && Enable.Y.equals(processConfig.getTechManagement())) {
                long techCount = souScoreRuleLineDao.lambdaQuery()
                        .eq(SouScoreRuleLine::getScoreRuleId, param.getScoreTemplateId())
                        .eq(SouScoreRuleLine::getDimension, SouScoreRuleDimensionEnum.TECHNOLOGY)
                        .count();
                AssertUtils.isTrue(techCount > 0, "评分规则缺少技术维度评分，不能使用");
            }
        }
    }

    /**
     * 当前操作人是否可以删除寻源单
     */
    public SouProject judgeRemoveSouAuth(long projectId) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "非拟定状态不能删除");
        return project;
    }


    /**
     * 当前操作人是否可以作废寻源单
     */
    public SouProject judgeCancelSouAuth(long projectId) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        switch (project.getProjectStatus()) {
            //已取消
            case CANCEL:
                throw new IllegalArgumentException("寻源单已取消，请勿重复操作");
            default:
                break;
        }
        return project;
    }

    /**
     * 当前操作人是否可以调用立项审批提交后的回调接口
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    public void judgeCallbackAfterApprovalSubmitAuth(long projectId, String souType) {
        // 1: 校验单据状态
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"));
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("非法的操作，寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("与寻源单不符"), souType);
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "非拟定状态，不能进行立项审批");
        switch (project.getCreateApprovalStatus()) {
            //已提交
            case SUBMITTED:
                throw new IllegalArgumentException("寻源单已提交，请勿重复操作");
                //已审批
            case APPROVED:
                throw new IllegalArgumentException("寻源单已审批，不能进行立项审批");
            default:;
        }
        /* 3: 校验供应商风险 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorEditHandler.class)
                .checkVendorRiskForInit(projectId, souVendorDao.list(SouVendor::getProjectId, projectId), false);
    }

    /**
     * 当前操作人是否可以调用立项审批通过后的回调接口
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    public SouProject judgeCallbackAfterApprovalPassAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"));
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("非法的操作，寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("与寻源单不符"), souType);
        return project;
    }

    /**
     * 当前操作人是否可以调用立项审批未通过后的回调接口
     *
     * @param projectId            {@link SouProject#getProjectId}
     * @param createApprovalStatus 立项审核状态
     * @param souType              寻源类型{@link SouTypeEnum}
     */
    public void judgeCallbackAfterApprovalUnPassAuth(long projectId, SouApprovalStatusEnum createApprovalStatus, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源信息")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"));
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("非法的操作，寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("与寻源单不符"), souType);
        AssertUtils.isTrue(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "非拟定状态，不能进行立项审批");
        AssertUtils.isTrue(SouApprovalStatusEnum.SUBMITTED.equals(project.getCreateApprovalStatus()), "审批状态不是已提交");

        switch (createApprovalStatus) {
            // 已提交
            case SUBMITTED:
                // 已审批
            case APPROVED:
                // 拟定
            case DRAFT:
                throw new IllegalArgumentException("错误的接口调用");
            default:
                break;
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
