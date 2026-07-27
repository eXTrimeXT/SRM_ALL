package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.bindplan;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 项目计划 - 绑定项目插件
 * @author huangbf3
 */
public interface IPrSouRequirementBindPlanPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouRequirementBindPlanContext judgeBindPlanAuth(PrSouRequirementBindPlanContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouRequirementBindPlanContext beforeBindPlan(PrSouRequirementBindPlanContext context) { return context; }
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouRequirementBindPlanContext executeBindPlan(PrSouRequirementBindPlanContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouRequirementBindPlanContext afterBindPlan(PrSouRequirementBindPlanContext context) { return context; }
    /**
     * 一般情况下，不用重写该方法
     * @return 返回
     */
    @Override
    @ApiOperation("一般情况下，不用重写该方法!!!")
    default boolean isDefaultMatchAllScene() {
        return true;
    }

}
