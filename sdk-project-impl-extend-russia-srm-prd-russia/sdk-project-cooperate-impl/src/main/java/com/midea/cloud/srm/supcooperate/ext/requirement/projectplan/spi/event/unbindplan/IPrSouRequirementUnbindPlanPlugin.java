package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.unbindplan;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 项目计划 - 解绑项目插件
 * @author huangbf3
 */
public interface IPrSouRequirementUnbindPlanPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouRequirementUnBindPlanContext judgeUnbindPlanAuth(PrSouRequirementUnBindPlanContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouRequirementUnBindPlanContext beforeUnbindPlan(PrSouRequirementUnBindPlanContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouRequirementUnBindPlanContext executeUnbindPlan(PrSouRequirementUnBindPlanContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouRequirementUnBindPlanContext afterUnbindPlan(PrSouRequirementUnBindPlanContext context) { return context; }
    /**
     * 备注
     * @return 返回
     */
    @Override
    @ApiOperation("一般情况下，不用重写该方法!!!")
    default boolean isDefaultMatchAllScene() {
        return true;
    }

}
