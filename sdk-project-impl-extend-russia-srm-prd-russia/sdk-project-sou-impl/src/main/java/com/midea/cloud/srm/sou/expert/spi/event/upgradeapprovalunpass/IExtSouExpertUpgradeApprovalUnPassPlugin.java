package com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalunpass;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家库 - 专家升级审批未通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
public interface IExtSouExpertUpgradeApprovalUnPassPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertUpgradeApprovalUnPassContext judgeUpgradeApprovalUnPassAuth(ExtSouExpertUpgradeApprovalUnPassContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertUpgradeApprovalUnPassContext beforeUpgradeApprovalUnPass(ExtSouExpertUpgradeApprovalUnPassContext context) { return context; }
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertUpgradeApprovalUnPassContext executeUpgradeApprovalUnPass(ExtSouExpertUpgradeApprovalUnPassContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertUpgradeApprovalUnPassContext afterUpgradeApprovalUnPass(ExtSouExpertUpgradeApprovalUnPassContext context) { return context; }
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
