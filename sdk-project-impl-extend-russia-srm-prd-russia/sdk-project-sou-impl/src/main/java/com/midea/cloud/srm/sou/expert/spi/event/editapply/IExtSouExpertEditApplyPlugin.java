package com.midea.cloud.srm.sou.expert.spi.event.editapply;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家申请编辑插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
public interface IExtSouExpertEditApplyPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertEditApplyContext judgeEditApplyAuth(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 数据处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("数据处理")
    default ExtSouExpertEditApplyContext prepareEditApply(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertEditApplyContext beforeEditApply(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertEditApplyContext executeEditApply(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertEditApplyContext afterEditApply(ExtSouExpertEditApplyContext context) { return context; }

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
