package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanEventService;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.bindplan.IPrSouRequirementBindPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.bindplan.PrSouRequirementBindPlanContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.IPrSouProjectEditPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.PrSouProjectEditPlanContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.IPrSouProjectImportPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel.PrSouProjectImportPlanContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.removeplan.IPrSouProjectRemovePlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.removeplan.PrSouProjectRemovePlanContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.unbindplan.IPrSouRequirementUnbindPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.unbindplan.PrSouRequirementUnBindPlanContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 招标计划 - 项目计划 - 事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouProjectPlanEventServiceImpl implements PrSouProjectPlanEventService {

    @Autowired
    private QlService qlService;

    /**
     * 编辑项目计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtPrSouProjectPlan editPlan(ExtPrSouProjectPlan param) {
        // 1: 初始化上下文
        PrSouProjectEditPlanContext context = new PrSouProjectEditPlanContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouProjectEditPlanPlugin.class, context).judgeEditPlanAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouProjectEditPlanPlugin.class, context).beforeEditPlan(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouProjectEditPlanPlugin.class, context).executeEditPlan(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouProjectEditPlanPlugin.class, context).afterEditPlan(context);

        return context.getParam();
    }

    /**
     * 删除项目计划
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtPrSouProjectPlan removePlan(long projectPlanId) {
        ExtPrSouProjectPlan projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), projectPlanId, ExtPrSouProjectPlan.class);
        AssertUtils.notNull(projectPlan, "项目计划[{0}]不存在", projectPlanId);
        // 1: 初始化上下文
        PrSouProjectRemovePlanContext context = new PrSouProjectRemovePlanContext(projectPlanId, projectPlan.getSceneType());
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouProjectRemovePlanPlugin.class, context).judgeRemovePlanAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouProjectRemovePlanPlugin.class, context).beforeRemovePlan(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouProjectRemovePlanPlugin.class, context).executeRemovePlan(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouProjectRemovePlanPlugin.class, context).afterRemovePlan(context);

        return context.getResult();
    }

    /**
     * 用于招标计划绑定项目计划的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void bindPlan(long projectPlanId, long requirementHeadId) {
        ExtPrSouProjectPlan projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), projectPlanId, ExtPrSouProjectPlan.class);
        // 1: 初始化上下文
        PrSouRequirementBindPlanContext context = new PrSouRequirementBindPlanContext(projectPlanId, requirementHeadId, projectPlan.getSceneType());
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementBindPlanPlugin.class, context).judgeBindPlanAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementBindPlanPlugin.class, context).beforeBindPlan(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementBindPlanPlugin.class, context).executeBindPlan(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementBindPlanPlugin.class, context).afterBindPlan(context);
    }

    /**
     * 用于招标计划解绑项目计划的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void unbindPlan(@Nullable Long projectPlanId, @Nullable Long requirementHeadId) {
        AssertUtils.isFalse(projectPlanId == null && requirementHeadId == null, "非法的接口调用");
        ExtPrSouProjectPlan projectPlan; {
            if (projectPlanId != null) {
                projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), projectPlanId, ExtPrSouProjectPlan.class);
            } else {
                projectPlan = qlService.queryByWrapper(QlWrappers.query(ExtPrSouProjectPlan.class)
                        .eq(ExtPrSouProjectPlan::getRequirementHeadId, requirementHeadId), ExtPrSouProjectPlan.class)
                        .stream().findAny().orElse(null);
                if (projectPlan == null) { return; }
            }
        }
        // 1: 初始化上下文
        PrSouRequirementUnBindPlanContext context = new PrSouRequirementUnBindPlanContext(projectPlanId, requirementHeadId, projectPlan.getSceneType());
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouRequirementUnbindPlanPlugin.class, context).judgeUnbindPlanAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouRequirementUnbindPlanPlugin.class, context).beforeUnbindPlan(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouRequirementUnbindPlanPlugin.class, context).executeUnbindPlan(context);
        // 5: 后置处理
        SdkPluginProxy.proxy(IPrSouRequirementUnbindPlanPlugin.class, context).afterUnbindPlan(context);
    }

    /**
     * excel导入项目计划
     */
    @Nullable
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long importProjectPlansExcel(MultipartFile file, String sceneType) {
        // 1: 初始化上下文
        PrSouProjectImportPlanContext context = new PrSouProjectImportPlanContext(file, sceneType);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouProjectImportPlanPlugin.class, context).judgeImportPlanAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouProjectImportPlanPlugin.class, context).beforeImportPlan(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IPrSouProjectImportPlanPlugin.class, context).executeImportPlan(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouProjectImportPlanPlugin.class, context).afterImportPlan(context);

        return context.getResult();
    }

}
