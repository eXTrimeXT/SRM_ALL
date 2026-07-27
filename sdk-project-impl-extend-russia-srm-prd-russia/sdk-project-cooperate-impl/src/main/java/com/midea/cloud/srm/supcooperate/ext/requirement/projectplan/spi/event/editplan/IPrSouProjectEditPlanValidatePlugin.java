package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 项目计划 - 编辑校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
public interface IPrSouProjectEditPlanValidatePlugin extends ISdkFunctionPlugin<PrSouProjectEditPlanContext> {
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @Override
    @ApiOperation("执行处理")
    default PrSouProjectEditPlanContext execute(PrSouProjectEditPlanContext context) {
        return SdkPluginProxy.proxy(IPrSouProjectEditPlanValidatePlugin.class, context).validateAndContextProjectPlan(context);
    }

    /**
     * 校验及构造项目计划
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及构造项目计划")
    default PrSouProjectEditPlanContext validateAndContextProjectPlan(PrSouProjectEditPlanContext context) { return context; }

}
