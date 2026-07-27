package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.abandonrequire;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanEventService;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.abandonrequire.IRequirementInitAbandonPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.abandonrequire.RequirementInitAbandonContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 作废插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitAbandonPlugin implements IRequirementInitAbandonPlugin {

    @Autowired
    private PrSouProjectPlanEventService prSouProjectPlanEventService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitAbandonContext judgeAbandonRequirementAuth(RequirementInitAbandonContext context) {
        // 1: 调用核心方法
        context = SdkPluginProxy.callSuper(IRequirementInitAbandonPlugin.class, context, this).judgeAbandonRequirementAuth(context);
        // 2: 判断单据是否为招标计划
        AssertUtils.isTrue(ISdkPlugin.DEFAULT_SCENE.equals(context.getReqHead().getX("sceneType").toString()), "非招标计划，禁止访问该接口");

        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public RequirementInitAbandonContext afterAbandonRequirement(RequirementInitAbandonContext context) {
        // 1: 调用核心方法
        context = SdkPluginProxy.callSuper(IRequirementInitAbandonPlugin.class, context, this).afterAbandonRequirement(context);
        // 2: 解绑项目计划
        prSouProjectPlanEventService.unbindPlan(null, context.getParam().getRequirementHeadId());

        return context;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
