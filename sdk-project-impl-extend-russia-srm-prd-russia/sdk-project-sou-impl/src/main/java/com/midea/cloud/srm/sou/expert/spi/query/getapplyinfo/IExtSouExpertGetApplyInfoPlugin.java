package com.midea.cloud.srm.sou.expert.spi.query.getapplyinfo;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 专家库 - 查询专家申请详情插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
public interface IExtSouExpertGetApplyInfoPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertGetApplyInfoContext judgeGetApplyInfoAuth(ExtSouExpertGetApplyInfoContext context) { return context; }

    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertGetApplyInfoContext beforeGetApplyInfo(ExtSouExpertGetApplyInfoContext context) { return context; }

    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertGetApplyInfoContext executeGetApplyInfo(ExtSouExpertGetApplyInfoContext context) { return context; }

    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertGetApplyInfoContext afterGetApplyInfo(ExtSouExpertGetApplyInfoContext context) { return context; }

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
