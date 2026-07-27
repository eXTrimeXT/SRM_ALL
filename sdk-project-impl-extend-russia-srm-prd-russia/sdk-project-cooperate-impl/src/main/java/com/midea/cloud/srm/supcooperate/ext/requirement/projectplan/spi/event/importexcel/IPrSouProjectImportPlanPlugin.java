package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 项目计划 - 导入插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/06
 */
public interface IPrSouProjectImportPlanPlugin extends ISdkPlugin {
    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouProjectImportPlanContext judgeImportPlanAuth(PrSouProjectImportPlanContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouProjectImportPlanContext beforeImportPlan(PrSouProjectImportPlanContext context) { return context; }
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouProjectImportPlanContext executeImportPlan(PrSouProjectImportPlanContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouProjectImportPlanContext afterImportPlan(PrSouProjectImportPlanContext context) { return context; }
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
