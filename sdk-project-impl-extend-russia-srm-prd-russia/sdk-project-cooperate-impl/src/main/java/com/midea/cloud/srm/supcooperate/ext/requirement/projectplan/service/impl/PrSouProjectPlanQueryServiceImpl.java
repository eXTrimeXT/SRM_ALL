package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.impl;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanQueryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.getplan.IPrSouProjectGetPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.getplan.PrSouProjectGetPlanContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.pageplans.IPrSouProjectPagePlansPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.pageplans.PrSouProjectPagePlansContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 招标计划 - 项目计划 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouProjectPlanQueryServiceImpl implements PrSouProjectPlanQueryService {

    @Autowired
    private QlService qlService;

    /**
     * 列表查询
     */
    @Override
    public List<ExtPrSouProjectPlan> pagePlans(ExtPrSouProjectPlanQueryDTO queryParam) {
        // 1: 初始化上下文
        PrSouProjectPagePlansContext context = new PrSouProjectPagePlansContext(queryParam);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouProjectPagePlansPlugin.class, context).judgePagePlansAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouProjectPagePlansPlugin.class, context).beforePagePlans(context);
        // 4: 执行查询
        context = SdkPluginProxy.proxy(IPrSouProjectPagePlansPlugin.class, context).executePagePlans(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouProjectPagePlansPlugin.class, context).afterPagePlans(context);

        return context.getResult();
    }

    /**
     * 查询项目计划详情
     */
    @Override
    public ExtPrSouProjectPlan getPlan(long projectPlanId) {
        ExtPrSouProjectPlan projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), projectPlanId, ExtPrSouProjectPlan.class);
        // 1: 初始化上下文
        PrSouProjectGetPlanContext context = new PrSouProjectGetPlanContext(projectPlanId, projectPlan.getSceneType());
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IPrSouProjectGetPlanPlugin.class, context).judgeGetPlanAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IPrSouProjectGetPlanPlugin.class, context).beforeGetPlan(context);
        // 4: 执行查询
        context = SdkPluginProxy.proxy(IPrSouProjectGetPlanPlugin.class, context).executeGetPlan(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IPrSouProjectGetPlanPlugin.class, context).afterGetPlan(context);

        return context.getResult();
    }

}
