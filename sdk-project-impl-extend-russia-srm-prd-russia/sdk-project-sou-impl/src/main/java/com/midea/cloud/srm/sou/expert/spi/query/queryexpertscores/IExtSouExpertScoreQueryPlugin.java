package com.midea.cloud.srm.sou.expert.spi.query.queryexpertscores;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 专家评审 - 列表查询插件
 *
 * @author zhangwk12@meicloud.com
 */
public interface IExtSouExpertScoreQueryPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertScoreQueryContext judgeQueryExpertScoresAuth(ExtSouExpertScoreQueryContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertScoreQueryContext beforeQueryExpertScores(ExtSouExpertScoreQueryContext context) { return context; }
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertScoreQueryContext executeQueryExpertScores(ExtSouExpertScoreQueryContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertScoreQueryContext afterQueryExpertScores(ExtSouExpertScoreQueryContext context) { return context; }
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
