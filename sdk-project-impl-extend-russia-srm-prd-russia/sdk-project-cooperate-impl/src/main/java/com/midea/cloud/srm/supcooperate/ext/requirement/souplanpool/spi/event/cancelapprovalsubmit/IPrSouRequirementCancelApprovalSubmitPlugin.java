package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalsubmit;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 计划取消 - 审批提交回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
public interface IPrSouRequirementCancelApprovalSubmitPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouRequirementCancelApprovalSubmitContext judgeApprovalSubmitAuth(PrSouRequirementCancelApprovalSubmitContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouRequirementCancelApprovalSubmitContext beforeApprovalSubmit(PrSouRequirementCancelApprovalSubmitContext context) { return context; }
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouRequirementCancelApprovalSubmitContext executeApprovalSubmit(PrSouRequirementCancelApprovalSubmitContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouRequirementCancelApprovalSubmitContext afterApprovalSubmit(PrSouRequirementCancelApprovalSubmitContext context) { return context; }
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
