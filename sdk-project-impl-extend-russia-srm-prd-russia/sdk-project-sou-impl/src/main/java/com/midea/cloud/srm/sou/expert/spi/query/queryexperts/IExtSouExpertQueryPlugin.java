package com.midea.cloud.srm.sou.expert.spi.query.queryexperts;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 专家库 - 列表查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
public interface IExtSouExpertQueryPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertQueryContext judgeQueryExpertsAuth(ExtSouExpertQueryContext context) { return context; }

    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertQueryContext beforeQueryExperts(ExtSouExpertQueryContext context) { return context; }

    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertQueryContext executeQueryExperts(ExtSouExpertQueryContext context) { return context; }

    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertQueryContext afterQueryExperts(ExtSouExpertQueryContext context) { return context; }

    /**
     * 一般情况下，不用重写该方法!!!
     * @return 返回
     */
    @Override
    @ApiOperation("一般情况下，不用重写该方法!!!")
    default boolean isDefaultMatchAllScene() {
        return true;
    }

}
