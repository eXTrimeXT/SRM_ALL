package com.midea.cloud.srm.sou.expert.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.dto.*;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertFrozenStatusEnum;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertEventService;
import com.midea.cloud.srm.sou.expert.spi.event.applyapprovalpass.ExtSouExpertApplyApprovalPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.applyapprovalpass.IExtSouExpertApplyApprovalPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.applyapprovalsubmit.ExtSouExpertApplyApprovalSubmitContext;
import com.midea.cloud.srm.sou.expert.spi.event.applyapprovalsubmit.IExtSouExpertApplyApprovalSubmitPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.applyapprovalunpass.ExtSouExpertApplyApprovalUnPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.applyapprovalunpass.IExtSouExpertApplyApprovalUnPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalpass.ExtSouExpertChangeApprovalPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalpass.IExtSouExpertChangeApprovalPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalsubmit.ExtSouExpertChangeApprovalSubmitContext;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalsubmit.IExtSouExpertChangeApprovalSubmitPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalunpass.ExtSouExpertChangeApprovalUnPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalunpass.IExtSouExpertChangeApprovalUnPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.ExtSouExpertScoreCreateContext;
import com.midea.cloud.srm.sou.expert.spi.event.createexpertscore.IExtSouExpertScoreCreatePlugin;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.ExtSouExpertEditApplyContext;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.IExtSouExpertEditApplyPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.expertscore.ExtSouExpertDoScoreContext;
import com.midea.cloud.srm.sou.expert.spi.event.expertscore.IExtSouExpertDoScorePlugin;
import com.midea.cloud.srm.sou.expert.spi.event.frozenexpert.ExtSouExpertFrozenContext;
import com.midea.cloud.srm.sou.expert.spi.event.frozenexpert.IExtSouExpertFrozenPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.frozenexpertconfirm.ExtSouExpertFrozenConfirmContext;
import com.midea.cloud.srm.sou.expert.spi.event.frozenexpertconfirm.IExtSouExpertFrozenConfirmPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.frozenexpertreject.ExtSouExpertFrozenRejectContext;
import com.midea.cloud.srm.sou.expert.spi.event.frozenexpertreject.IExtSouExpertFrozenRejectPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalpass.ExtSouExpertGreenApprovalPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalpass.IExtSouExpertGreenApprovalPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalsubmit.ExtSouExpertGreenApprovalSubmitContext;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalsubmit.IExtSouExpertGreenApprovalSubmitPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalunpass.ExtSouExpertGreenApprovalUnPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalunpass.IExtSouExpertGreenApprovalUnPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.quiteexpert.ExtSouExpertQuiteContext;
import com.midea.cloud.srm.sou.expert.spi.event.quiteexpert.IExtSouExpertQuitePlugin;
import com.midea.cloud.srm.sou.expert.spi.event.removeapply.ExtSouExpertRemoveApplyContext;
import com.midea.cloud.srm.sou.expert.spi.event.removeapply.IExtSouExpertRemoveApplyPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpert.ExtSouExpertUnfrozenContext;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpert.IExtSouExpertUnfrozenPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertconfirm.ExtSouExpertUnfrozenConfirmContext;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertconfirm.IExtSouExpertUnfrozenConfirmPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertreject.ExtSouExpertUnfrozenRejectContext;
import com.midea.cloud.srm.sou.expert.spi.event.unfrozenexpertreject.IExtSouExpertUnfrozenRejectPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalpass.ExtSouExpertUpgradeApprovalPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalpass.IExtSouExpertUpgradeApprovalPassPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalsubmit.ExtSouExpertUpgradeApprovalSubmitContext;
import com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalsubmit.IExtSouExpertUpgradeApprovalSubmitPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalunpass.ExtSouExpertUpgradeApprovalUnPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalunpass.IExtSouExpertUpgradeApprovalUnPassPlugin;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 专家库 - 事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Service
public class ExtSouExpertEventServiceImpl implements ExtSouExpertEventService {

    /**
     * 编辑专家申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtSouExpertApplyDTO editApply(ExtSouExpertApplyDTO param) {
        // 1: 初始化上下文
        ExtSouExpertEditApplyContext context = new ExtSouExpertEditApplyContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyPlugin.class, context).judgeEditApplyAuth(context);
        // 3: 数据准备
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyPlugin.class, context).prepareEditApply(context);
        // 4: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyPlugin.class, context).beforeEditApply(context);
        // 5: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyPlugin.class, context).executeEditApply(context);
        // 6: 后置处理
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyPlugin.class, context).afterEditApply(context);

        return context.getParam();
    }

    /**
     * 删除专家申请
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Nullable
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtSouExpertApplyVO removeApply(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertRemoveApplyContext context = new ExtSouExpertRemoveApplyContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertRemoveApplyPlugin.class, context).judgeRemoveApplyAuth(context);
        if (context.getExpertApply() == null) { return null; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertRemoveApplyPlugin.class, context).beforeRemoveApply(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertRemoveApplyPlugin.class, context).executeRemoveApply(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IExtSouExpertRemoveApplyPlugin.class, context).afterRemoveApply(context);

        return context.getResult();
    }

    /**
     * 冻结专家
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void frozenExpert(ExtSouExpertFrozenDTO param) {
        // 1: 初始化上下文
        ExtSouExpertFrozenContext context = new ExtSouExpertFrozenContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenPlugin.class, context).judgeFrozenExpertAuth(context);
        if (ExtSouExpertFrozenStatusEnum.FROZEN_UN_CONFIRM.name().equals(context.getExpert().getFrozenStatus())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenPlugin.class, context).beforeFrozenExpert(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenPlugin.class, context).executeFrozenExpert(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertFrozenPlugin.class, context).afterFrozenExpert(context);
    }

    /**
     * 确认冻结专家
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void frozenExpertConfirm(long expertId) {
        // 1: 初始化上下文
        ExtSouExpertFrozenConfirmContext context = new ExtSouExpertFrozenConfirmContext(expertId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenConfirmPlugin.class, context).judgeFrozenConfirmAuth(context);
        if (ExtSouExpertFrozenStatusEnum.FROZEN.name().equals(context.getExpert().getFrozenStatus())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenConfirmPlugin.class, context).beforeFrozenConfirm(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenConfirmPlugin.class, context).executeFrozenConfirm(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertFrozenConfirmPlugin.class, context).afterFrozenConfirm(context);
    }

    /**
     * 拒绝冻结专家
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void frozenExpertReject(ExtSouExpertFrozenRejectDTO param) {
        // 1: 初始化上下文
        ExtSouExpertFrozenRejectContext context = new ExtSouExpertFrozenRejectContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenRejectPlugin.class, context).judgeFrozenRejectAuth(context);
        if (context.getExpert().getFrozenStatus() == null || ExtSouExpertFrozenStatusEnum.UNFROZEN.name().equals(context.getExpert().getFrozenStatus())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenRejectPlugin.class, context).beforeFrozenReject(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertFrozenRejectPlugin.class, context).executeFrozenReject(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertFrozenRejectPlugin.class, context).afterFrozenReject(context);
    }

    /**
     * 解冻专家
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void unFrozenExpert(ExtSouExpertUnFrozenDTO param) {
        // 1: 初始化上下文
        ExtSouExpertUnfrozenContext context = new ExtSouExpertUnfrozenContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenPlugin.class, context).judgeUnfrozenExpertAuth(context);
        if (ExtSouExpertFrozenStatusEnum.UNFROZEN_UN_CONFIRM.name().equals(context.getExpert().getFrozenStatus())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenPlugin.class, context).beforeUnfrozenExpert(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenPlugin.class, context).executeUnfrozenExpert(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertUnfrozenPlugin.class, context).afterUnfrozenExpert(context);
    }

    /**
     * 确认解冻专家
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void unfrozenExpertConfirm(long expertId) {
        // 1: 初始化上下文
        ExtSouExpertUnfrozenConfirmContext context = new ExtSouExpertUnfrozenConfirmContext(expertId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenConfirmPlugin.class, context).judgeUnfrozenConfirmAuth(context);
        if (context.getExpert().getFrozenStatus() == null || ExtSouExpertFrozenStatusEnum.UNFROZEN.name().equals(context.getExpert().getFrozenStatus())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenConfirmPlugin.class, context).beforeUnfrozenConfirm(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenConfirmPlugin.class, context).executeUnfrozenConfirm(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertUnfrozenConfirmPlugin.class, context).afterUnfrozenConfirm(context);
    }

    /**
     * 拒绝解冻专家
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void unfrozenExpertReject(ExtSouExpertFrozenRejectDTO param) {
        // 1: 初始化上下文
        ExtSouExpertUnfrozenRejectContext context = new ExtSouExpertUnfrozenRejectContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenRejectPlugin.class, context).judgeUnfrozenRejectAuth(context);
        if (ExtSouExpertFrozenStatusEnum.UNFROZEN.name().equals(context.getExpert().getFrozenStatus())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenRejectPlugin.class, context).beforeUnfrozenReject(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertUnfrozenRejectPlugin.class, context).executeUnfrozenReject(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertUnfrozenRejectPlugin.class, context).afterUnfrozenReject(context);
    }

    /**
     * 专家退出
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void quiteExpert(ExtSouExpertQuiteDTO param) {
        // 1: 初始化上下文
        ExtSouExpertQuiteContext context = new ExtSouExpertQuiteContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertQuitePlugin.class, context).judgeQuiteExpertAuth(context);
        if (Enable.Y.equals(context.getExpert().getHasQuite())) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertQuitePlugin.class, context).beforeQuiteExpert(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertQuitePlugin.class, context).executeQuiteExpert(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertQuitePlugin.class, context).afterQuiteExpert(context);
    }

    /**
     * 专家申请审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApplyApprovalSubmit(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertApplyApprovalSubmitContext context = new ExtSouExpertApplyApprovalSubmitContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalSubmitPlugin.class, context).judgeApplyApprovalSubmitAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalSubmitPlugin.class, context).beforeApplyApprovalSubmit(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalSubmitPlugin.class, context).executeApplyApprovalSubmit(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertApplyApprovalSubmitPlugin.class, context).afterApplyApprovalSubmit(context);
    }

    /**
     * 专家申请审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApplyApprovalPass(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertApplyApprovalPassContext context = new ExtSouExpertApplyApprovalPassContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalPassPlugin.class, context).judgeApplyApprovalPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalPassPlugin.class, context).beforeApplyApprovalPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalPassPlugin.class, context).executeApplyApprovalPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertApplyApprovalPassPlugin.class, context).afterApplyApprovalPass(context);
    }

    /**
     * 专家申请审批未通过后的回调处理
     * @param param 回调参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApplyApprovalUnPass(ExtSouExpertApplyUnPassDTO param) {
        // 1: 初始化上下文
        ExtSouExpertApplyApprovalUnPassContext context = new ExtSouExpertApplyApprovalUnPassContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalUnPassPlugin.class, context).judgeApplyApprovalUnPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalUnPassPlugin.class, context).beforeApplyApprovalUnPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertApplyApprovalUnPassPlugin.class, context).executeApplyApprovalUnPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertApplyApprovalUnPassPlugin.class, context).afterApplyApprovalUnPass(context);
    }

    /**
     * 专家升级审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterUpgradeApprovalSubmit(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertUpgradeApprovalSubmitContext context = new ExtSouExpertUpgradeApprovalSubmitContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalSubmitPlugin.class, context).judgeUpgradeApprovalSubmitAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalSubmitPlugin.class, context).beforeUpgradeApprovalSubmit(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalSubmitPlugin.class, context).executeUpgradeApprovalSubmit(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalSubmitPlugin.class, context).afterUpgradeApprovalSubmit(context);
    }

    /**
     * 专家升级审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterUpgradeApprovalPass(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertUpgradeApprovalPassContext context = new ExtSouExpertUpgradeApprovalPassContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalPassPlugin.class, context).judgeUpgradeApprovalPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalPassPlugin.class, context).beforeUpgradeApprovalPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalPassPlugin.class, context).executeUpgradeApprovalPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalPassPlugin.class, context).afterUpgradeApprovalPass(context);
    }

    /**
     * 专家升级审批未通过后的回调处理
     * @param param 回调参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterUpgradeApprovalUnPass(ExtSouExpertApplyUnPassDTO param) {
        // 1: 初始化上下文
        ExtSouExpertUpgradeApprovalUnPassContext context = new ExtSouExpertUpgradeApprovalUnPassContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalUnPassPlugin.class, context).judgeUpgradeApprovalUnPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalUnPassPlugin.class, context).beforeUpgradeApprovalUnPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalUnPassPlugin.class, context).executeUpgradeApprovalUnPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertUpgradeApprovalUnPassPlugin.class, context).afterUpgradeApprovalUnPass(context);
    }

    /**
     * 专家变更审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterChangeApprovalSubmit(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertChangeApprovalSubmitContext context = new ExtSouExpertChangeApprovalSubmitContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalSubmitPlugin.class, context).judgeChangeApprovalSubmitAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalSubmitPlugin.class, context).beforeChangeApprovalSubmit(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalSubmitPlugin.class, context).executeChangeApprovalSubmit(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertChangeApprovalSubmitPlugin.class, context).afterChangeApprovalSubmit(context);
    }

    /**
     * 专家变更审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterChangeApprovalPass(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertChangeApprovalPassContext context = new ExtSouExpertChangeApprovalPassContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalPassPlugin.class, context).judgeChangeApprovalPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalPassPlugin.class, context).beforeChangeApprovalPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalPassPlugin.class, context).executeChangeApprovalPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertChangeApprovalPassPlugin.class, context).afterChangeApprovalPass(context);
    }

    /**
     * 专家变更审批未通过后的回调处理
     * @param param 回调参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterChangeApprovalUnPass(ExtSouExpertApplyUnPassDTO param) {
        // 1: 初始化上下文
        ExtSouExpertChangeApprovalUnPassContext context = new ExtSouExpertChangeApprovalUnPassContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalUnPassPlugin.class, context).judgeChangeApprovalUnPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalUnPassPlugin.class, context).beforeChangeApprovalUnPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertChangeApprovalUnPassPlugin.class, context).executeChangeApprovalUnPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertChangeApprovalUnPassPlugin.class, context).afterChangeApprovalUnPass(context);
    }

    /**
     * 专家审批绿色通道审批提交后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterGreenApprovalSubmit(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertGreenApprovalSubmitContext context = new ExtSouExpertGreenApprovalSubmitContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalSubmitPlugin.class, context).judgeGreenApprovalSubmitAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalSubmitPlugin.class, context).beforeGreenApprovalSubmit(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalSubmitPlugin.class, context).executeGreenApprovalSubmit(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertGreenApprovalSubmitPlugin.class, context).afterGreenApprovalSubmit(context);
    }

    /**
     * 专家申请绿色通道审批通过后的回调处理
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterGreenApprovalPass(long expertApplyId) {
        // 1: 初始化上下文
        ExtSouExpertGreenApprovalPassContext context = new ExtSouExpertGreenApprovalPassContext(expertApplyId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalPassPlugin.class, context).judgeGreenApprovalPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalPassPlugin.class, context).beforeGreenApprovalPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalPassPlugin.class, context).executeGreenApprovalPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertGreenApprovalPassPlugin.class, context).afterGreenApprovalPass(context);
    }

    /**
     * 专家申请绿色通道审批未通过后的回调处理
     * @param param 回调参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterGreenApprovalUnPass(ExtSouExpertApplyUnPassDTO param) {
        // 1: 初始化上下文
        ExtSouExpertGreenApprovalUnPassContext context = new ExtSouExpertGreenApprovalUnPassContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalUnPassPlugin.class, context).judgeGreenApprovalUnPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalUnPassPlugin.class, context).beforeGreenApprovalUnPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertGreenApprovalUnPassPlugin.class, context).executeGreenApprovalUnPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertGreenApprovalUnPassPlugin.class, context).afterGreenApprovalUnPass(context);
    }

    /**
     * 批量创建专家评审信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void createExpertScores(List<ExtSouExpertScoreCreateDTO> params) {
        // 1: 初始化上下文
        ExtSouExpertScoreCreateContext context = new ExtSouExpertScoreCreateContext(params);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertScoreCreatePlugin.class, context).judgeCreateExpertScoreAuth(context);
        // 3: 数据准备
        context = SdkPluginProxy.proxy(IExtSouExpertScoreCreatePlugin.class, context).prepareCreateExpertScore(context);
        // 4: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertScoreCreatePlugin.class, context).beforeCreateExpertScore(context);
        // 5: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertScoreCreatePlugin.class, context).executeCreateExpertScore(context);
        // 6: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertScoreCreatePlugin.class, context).afterCreateExpertScore(context);
    }

    /**
     * 专家评分
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void expertDoScore(List<ExtSouExpertDoScoreDTO> params) {
        // 1: 初始化上下文
        ExtSouExpertDoScoreContext context = new ExtSouExpertDoScoreContext(params);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtSouExpertDoScorePlugin.class, context).judgeDoScoreAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtSouExpertDoScorePlugin.class, context).beforeDoScore(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtSouExpertDoScorePlugin.class, context).executeDoScore(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IExtSouExpertDoScorePlugin.class, context).afterDoScore(context);
    }

}
