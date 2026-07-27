package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelUnPassDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolAssignDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolCreateSouDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCreateSouVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolEventService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.batchassign.IPrSouRequirementPoolBatchAssignPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.batchassign.PrSouRequirementPoolBatchAssignContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalpass.IPrSouRequirementCancelApprovalPassPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalpass.PrSouRequirementCancelApprovalPassContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalsubmit.IPrSouRequirementCancelApprovalSubmitPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalsubmit.PrSouRequirementCancelApprovalSubmitContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalunpass.IPrSouRequirementCancelApprovalUnPassPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalunpass.PrSouRequirementCancelApprovalUnPassContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.IPrSouRequirementChangePlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.PrSouRequirementChangePlanContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.IPrSouRequirementCreateBidSouPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.IPrSouRequirementCreateSouPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou.PrSouRequirementCreateSouContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsoureq.IPrSouRequirementCreateSouReqPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsoureq.PrSouRequirementCreateSouReqContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createvendorrecommend.IPrSouRequirementCreateVendorRecommendPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createvendorrecommend.PrSouRequirementCreateVendorRecommendContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.IPrSouRequirementCancelEditPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.PrSouRequirementCancelEditContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.removecancel.IPrSouRequirementCancelRemovePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.removecancel.PrSouRequirementCancelRemoveContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 招标计划池 - 事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Component
public class PrSouRequirementPoolEventServiceImpl implements PrSouRequirementPoolEventService {

    /**
     * 批量分配/转办
     * @param param 分配数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void batchAssign(ExtPrSouRequirementPoolAssignDTO param) {
        // 1: 初始化上下文
        PrSouRequirementPoolBatchAssignContext context = new PrSouRequirementPoolBatchAssignContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolBatchAssignPlugin.class, context).judgeBatchAssignAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolBatchAssignPlugin.class, context).beforeBatchAssign(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementPoolBatchAssignPlugin.class, context).executeBatchAssign(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementPoolBatchAssignPlugin.class, context).afterBatchAssign(context);
    }

    /**
     * 编辑招标计划取消单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtPrSouRequirementCancelDTO editReqCancel(ExtPrSouRequirementCancelDTO param) {
        // 1: 初始化上下文
        PrSouRequirementCancelEditContext context = new PrSouRequirementCancelEditContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditPlugin.class, context).judgeEditReqCancelAuth(context);
        // 3: 数据准备
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditPlugin.class, context).prepareEditReqCancel(context);
        // 4: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditPlugin.class, context).beforeEditReqCancel(context);
        // 5: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditPlugin.class, context).executeEditReqCancel(context);
        // 6: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditPlugin.class, context).afterEditReqCancel(context);

        return context.getParam();
    }

    /**
     * 删除招标取消单据
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     */
    @Nullable
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtPrSouRequirementCancelVO removeReqCancel(long requirementCancelId) {
        // 1: 初始化上下文
        PrSouRequirementCancelRemoveContext context = new PrSouRequirementCancelRemoveContext(requirementCancelId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelRemovePlugin.class, context).judgeRemoveCancelAuth(context);
        if (context.getReqCancel() == null) { return null; }
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelRemovePlugin.class, context).beforeRemoveCancel(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelRemovePlugin.class, context).executeRemoveCancel(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelRemovePlugin.class, context).afterRemoveCancel(context);

        return context.getResult();
    }

    /**
     * 变更招标计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtPrSouRequirementHeadDTO changeSouPlan(ExtPrSouRequirementHeadDTO param) {
        // 1: 初始化上下文
        PrSouRequirementChangePlanContext context = new PrSouRequirementChangePlanContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanPlugin.class, context).judgeChangePlanAuth(context);
        // 3: 数据准备
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanPlugin.class, context).prepareChangePlan(context);
        // 4: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanPlugin.class, context).beforeChangePlan(context);
        // 5: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanPlugin.class, context).executeChangePlan(context);
        // 6: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanPlugin.class, context).afterChangePlan(context);

        return context.getResult();
    }

    /**
     * 供应商推荐
     */
    @Nullable
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ApiExtSouRecommVendorInfoDTO createVendorRecommend(List<ExtPrSouRequirementHeadDTO> params) {
        // 1: 初始化上下文
        PrSouRequirementCreateVendorRecommendContext context = new PrSouRequirementCreateVendorRecommendContext(params);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateVendorRecommendPlugin.class, context).judgeVendorRecommendAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateVendorRecommendPlugin.class, context).beforeVendorRecommend(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateVendorRecommendPlugin.class, context).executeVendorRecommend(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateVendorRecommendPlugin.class, context).afterVendorRecommend(context);

        return context.getResult();
    }

    /**
     * 创建寻源需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public RecordDTO createSouReq(List<ExtPrSouRequirementHeadDTO> params) {
        // 1: 初始化上下文
        PrSouRequirementCreateSouReqContext context = new PrSouRequirementCreateSouReqContext(params);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouReqPlugin.class, context).judgeCreateSouReqAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouReqPlugin.class, context).beforeCreateSouReq(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouReqPlugin.class, context).executeCreateSouReq(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementCreateSouReqPlugin.class, context).afterCreateSouReq(context);

        return context.getResult();
    }

    /**
     * 创建寻源
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtPrSouRequirementCreateSouVO createSou(ExtPrSouRequirementPoolCreateSouDTO param) {
        param.setSouType(StringUtils.trimToNull(param.getSouType()));
        AssertUtils.notNull(param.getSouType(), "缺少souType参数");
        // 1: 初始化上下文
        PrSouRequirementCreateSouContext context = new PrSouRequirementCreateSouContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).judgeCreateSouAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).beforeCreateSou(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).executeCreateSou(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).afterCreateSou(context);

        return context.getResult();
    }

    /**
     * 招标计划取消审批提交后的回调处理
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterCancelApprovalSubmit(long requirementCancelId) {
        // 1: 初始化上下文
        PrSouRequirementCancelApprovalSubmitContext context = new PrSouRequirementCancelApprovalSubmitContext(requirementCancelId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalSubmitPlugin.class, context).judgeApprovalSubmitAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalSubmitPlugin.class, context).beforeApprovalSubmit(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalSubmitPlugin.class, context).executeApprovalSubmit(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalSubmitPlugin.class, context).afterApprovalSubmit(context);
    }

    /**
     * 招标计划取消审批通过后的回调处理
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterCancelApprovalPass(long requirementCancelId) {
        // 1: 初始化上下文
        PrSouRequirementCancelApprovalPassContext context = new PrSouRequirementCancelApprovalPassContext(requirementCancelId);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalPassPlugin.class, context).judgeApprovalPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalPassPlugin.class, context).beforeApprovalPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalPassPlugin.class, context).executeApprovalPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalPassPlugin.class, context).afterApprovalPass(context);
    }

    /**
     * 招标计划取消审批未通过后的回调处理
     * @param param 回调参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterCancelApprovalUnPass(ExtPrSouRequirementCancelUnPassDTO param) {
        // 1: 初始化上下文
        PrSouRequirementCancelApprovalUnPassContext context = new PrSouRequirementCancelApprovalUnPassContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalUnPassPlugin.class, context).judgeApprovalUnPassAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalUnPassPlugin.class, context).beforeApprovalUnPass(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalUnPassPlugin.class, context).executeApprovalUnPass(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementCancelApprovalUnPassPlugin.class, context).afterApprovalUnPass(context);
    }

    @Override
    public ExtPrSouRequirementCreateSouVO createBidSou(ExtPrSouRequirementPoolCreateSouDTO param) {
        param.setSouType(StringUtils.trimToNull(param.getSouType()));
        AssertUtils.notNull(param.getSouType(), "缺少souType参数");
        // 1: 初始化上下文
        PrSouRequirementCreateSouContext context = new PrSouRequirementCreateSouContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).judgeCreateSouAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).beforeCreateBidSou(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).executeCreateBidSou(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementCreateSouPlugin.class, context).afterCreateBidSou(context);

        return context.getResult();
    }

}
