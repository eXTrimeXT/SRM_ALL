package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.pageplans;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 项目计划 - 列表查询插件s
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
public interface IPrSouProjectPagePlansPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouProjectPagePlansContext judgePagePlansAuth(PrSouProjectPagePlansContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouProjectPagePlansContext beforePagePlans(PrSouProjectPagePlansContext context) { return context; }
    /**
     * 执行查询
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行查询")
    default PrSouProjectPagePlansContext executePagePlans(PrSouProjectPagePlansContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouProjectPagePlansContext afterPagePlans(PrSouProjectPagePlansContext context) { return context; }
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
