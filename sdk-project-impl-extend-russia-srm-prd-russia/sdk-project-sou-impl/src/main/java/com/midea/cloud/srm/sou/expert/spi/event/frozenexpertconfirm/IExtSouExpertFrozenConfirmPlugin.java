package com.midea.cloud.srm.sou.expert.spi.event.frozenexpertconfirm;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家库 - 专家确认冻结插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
public interface IExtSouExpertFrozenConfirmPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertFrozenConfirmContext judgeFrozenConfirmAuth(ExtSouExpertFrozenConfirmContext context) { return context; }

    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertFrozenConfirmContext beforeFrozenConfirm(ExtSouExpertFrozenConfirmContext context) { return context; }

    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertFrozenConfirmContext executeFrozenConfirm(ExtSouExpertFrozenConfirmContext context) { return context; }

    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertFrozenConfirmContext afterFrozenConfirm(ExtSouExpertFrozenConfirmContext context) { return context; }

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
