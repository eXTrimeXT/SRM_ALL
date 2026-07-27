package com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementApprovalUnPassDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementHeadDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlRequirementAbandonDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitEventService;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.abandonrequire.IRequirementInitAbandonPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.abandonrequire.RequirementInitAbandonContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackpass.IRequirementInitCallbackPassPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackpass.RequirementInitCallbackPassContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbacksubmit.IRequirementInitCallbackSubmitPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbacksubmit.RequirementInitCallbackSubmitContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackunpass.IRequirementInitCallbackUnPassPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackunpass.RequirementInitCallbackUnPassContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.copyrequire.IRequirementInitCopyPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.copyrequire.RequirementInitCopyContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.IRequirementInitEditPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.RequirementInitEditContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.releasebudget.IRequirementInitReleaseBudgetPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.releasebudget.RequirementInitReleaseBudgetContext;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.remverequire.IRequirementInitRemovePlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.remverequire.RequirementInitRemoveContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * mql - 采购申请立项事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/19
 */
@Service
public class MqlPrRequirementInitEventServiceImpl implements MqlPrRequirementInitEventService {

    /**
     * 编辑采购申请单
     * @param param 采购申请单数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public MqlPrRequirementHeadDTO editRequirement(MqlPrRequirementHeadDTO param) {
        // 1: 初始化上下文
        RequirementInitEditContext context = new RequirementInitEditContext().setParam(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitEditPlugin.class, context).judgeEditRequirementAuth(context);
        // 3: 数据准备
        context = SdkPluginProxy.proxy(IRequirementInitEditPlugin.class, context).prepareEditRequirement(context);
        // 4: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitEditPlugin.class, context).beforeEditRequirement(context);
        // 5: 数据保存
        context = SdkPluginProxy.proxy(IRequirementInitEditPlugin.class, context).executeEditRequirement(context);
        // 6: 后置处理
        context = SdkPluginProxy.proxy(IRequirementInitEditPlugin.class, context).afterEditRequirement(context);

        return context.getParam();
    }

    /**
     * 删除采购申请单
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public MqlPrRequirementHeadVO removeRequirement(long requirementHeadId) {
        // 1: 初始化上下文
        RequirementInitRemoveContext context = new RequirementInitRemoveContext().setRequirementHeadId(requirementHeadId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitRemovePlugin.class, context).judgeRemoveRequirementAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitRemovePlugin.class, context).beforeRemoveRequirement(context);
        // 4: 删除数据
        context = SdkPluginProxy.proxy(IRequirementInitRemovePlugin.class, context).executeRemoveRequirement(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IRequirementInitRemovePlugin.class, context).afterRemoveRequirement(context);

        return context.getResult();
    }

    /**
     * 废弃采购申请单
     * @param param 废弃信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public void abandonRequirement(MqlRequirementAbandonDTO param) {
        // 1: 初始化上下文
        RequirementInitAbandonContext context = new RequirementInitAbandonContext().setParam(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitAbandonPlugin.class, context).judgeAbandonRequirementAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitAbandonPlugin.class, context).beforeAbandonRequirement(context);
        // 4: 作废单据
        context = SdkPluginProxy.proxy(IRequirementInitAbandonPlugin.class, context).executeAbandonRequirement(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IRequirementInitAbandonPlugin.class, context).afterAbandonRequirement(context);
    }

    /**
     * 复制采购申请单
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     * @param appUser 操作人信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public long/* requirementHeadId */ copyRequirement(long requirementHeadId, LoginAppUser appUser) {
        // 1: 初始化上下文
        RequirementInitCopyContext context = new RequirementInitCopyContext().setRequirementHeadId(requirementHeadId).setAppUser(appUser);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitCopyPlugin.class, context).judgeCopyRequirementAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitCopyPlugin.class, context).beforeCopyRequirement(context);
        // 4: 复制单据
        context = SdkPluginProxy.proxy(IRequirementInitCopyPlugin.class, context).executeCopyRequirement(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IRequirementInitCopyPlugin.class, context).afterCopyRequirement(context);

        return context.getEditDTO().getRequirementHeadId();
    }

    /**
     * 释放采购申请未使用的预算
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public void releaseRequirementBudget(long requirementHeadId) {
        // 1: 构造上下文
        RequirementInitReleaseBudgetContext context = new RequirementInitReleaseBudgetContext().setRequirementHeadId(requirementHeadId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitReleaseBudgetPlugin.class, context).judgeReleaseBudgetAuth(context);
        if (context.getReqHead().getUnusedBudget().compareTo(BigDecimal.ZERO) <= 0) { return; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitReleaseBudgetPlugin.class, context).beforeReleaseBudget(context);
        // 4: 释放预算
        context = SdkPluginProxy.proxy(IRequirementInitReleaseBudgetPlugin.class, context).executeReleaseBudget(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IRequirementInitReleaseBudgetPlugin.class, context).afterReleaseBudget(context);
    }

    /**
     * 立项审批提交后的回调处理
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public void callbackAfterApprovalSubmit(long requirementHeadId) {
        // 1: 初始化上下文
        RequirementInitCallbackSubmitContext context = new RequirementInitCallbackSubmitContext().setRequirementHeadId(requirementHeadId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitCallbackSubmitPlugin.class, context).judgeCallbackSubmitAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitCallbackSubmitPlugin.class, context).beforeCallbackSubmit(context);
        // 4: 更新数据
        context = SdkPluginProxy.proxy(IRequirementInitCallbackSubmitPlugin.class, context).executeCallbackSubmit(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IRequirementInitCallbackSubmitPlugin.class, context).afterCallbackSubmit(context);
    }

    /**
     * 立项审批通过后的回调处理
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public void callbackAfterApprovalPass(long requirementHeadId) {
        // 1: 初始化上下文
        RequirementInitCallbackPassContext context = new RequirementInitCallbackPassContext().setRequirementHeadId(requirementHeadId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitCallbackPassPlugin.class, context).judgeCallbackPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitCallbackPassPlugin.class, context).beforeCallbackPass(context);
        // 4: 更新数据
        context = SdkPluginProxy.proxy(IRequirementInitCallbackPassPlugin.class, context).executeCallbackPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IRequirementInitCallbackPassPlugin.class, context).afterCallbackPass(context);
    }

    /**
     * 立项审批未通过后的回调处理
     * @param param 回调参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation =  Propagation.REQUIRED)
    public void callbackAfterApprovalUnPass(MqlPrRequirementApprovalUnPassDTO param) {
        // 1: 初始化上下文
        RequirementInitCallbackUnPassContext context = new RequirementInitCallbackUnPassContext().setParam(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IRequirementInitCallbackUnPassPlugin.class, context).judgeCallbackUnPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IRequirementInitCallbackUnPassPlugin.class, context).beforeCallbackUnPass(context);
        // 4: 更新数据
        context = SdkPluginProxy.proxy(IRequirementInitCallbackUnPassPlugin.class, context).executeCallbackUnPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IRequirementInitCallbackUnPassPlugin.class, context).afterCallbackUnPass(context);
    }

}
