package com.midea.cloud.srm.sou.expert.spi.event.applyapprovalsubmit;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家库 - 专家申请审批提交回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
public interface IExtSouExpertApplyApprovalSubmitPlugin extends ISdkPlugin {
    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtSouExpertApplyApprovalSubmitContext judgeApplyApprovalSubmitAuth(ExtSouExpertApplyApprovalSubmitContext context) { return context; }

    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtSouExpertApplyApprovalSubmitContext beforeApplyApprovalSubmit(ExtSouExpertApplyApprovalSubmitContext context) { return context; }

    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtSouExpertApplyApprovalSubmitContext executeApplyApprovalSubmit(ExtSouExpertApplyApprovalSubmitContext context) { return context; }

    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtSouExpertApplyApprovalSubmitContext afterApplyApprovalSubmit(ExtSouExpertApplyApprovalSubmitContext context) { return context; }

    /**
     * 备注
     * @return 返回
     */
    @Override
    @ApiOperation("一般情况下，不用重写该方法!!!")
    default boolean isDefaultMatchAllScene() {
        return true;
    }

}
