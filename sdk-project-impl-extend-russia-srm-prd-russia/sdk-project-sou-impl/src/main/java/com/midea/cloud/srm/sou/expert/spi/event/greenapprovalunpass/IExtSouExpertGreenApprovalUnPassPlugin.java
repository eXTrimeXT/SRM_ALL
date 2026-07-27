package com.midea.cloud.srm.sou.expert.spi.event.greenapprovalunpass;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家库 - 专家申请绿色通道审批未通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
public interface IExtSouExpertGreenApprovalUnPassPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertGreenApprovalUnPassContext judgeGreenApprovalUnPassAuth(ExtSouExpertGreenApprovalUnPassContext context) { return context; }

    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertGreenApprovalUnPassContext beforeGreenApprovalUnPass(ExtSouExpertGreenApprovalUnPassContext context) { return context; }

    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertGreenApprovalUnPassContext executeGreenApprovalUnPass(ExtSouExpertGreenApprovalUnPassContext context) { return context; }

    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertGreenApprovalUnPassContext afterGreenApprovalUnPass(ExtSouExpertGreenApprovalUnPassContext context) { return context; }

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
