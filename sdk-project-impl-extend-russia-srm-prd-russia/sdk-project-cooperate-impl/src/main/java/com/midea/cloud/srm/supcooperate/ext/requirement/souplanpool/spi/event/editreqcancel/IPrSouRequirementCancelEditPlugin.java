package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 计划取消编辑插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
public interface IPrSouRequirementCancelEditPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouRequirementCancelEditContext judgeEditReqCancelAuth(PrSouRequirementCancelEditContext context) { return context; }
    /**
     * 数据准备
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("数据准备")
    default PrSouRequirementCancelEditContext prepareEditReqCancel(PrSouRequirementCancelEditContext context) { return context; }
    /**
     * 前置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouRequirementCancelEditContext beforeEditReqCancel(PrSouRequirementCancelEditContext context) { return context; }
    /**
     * 执行处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouRequirementCancelEditContext executeEditReqCancel(PrSouRequirementCancelEditContext context) { return context; }
    /**
     * 后置处理
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouRequirementCancelEditContext afterEditReqCancel(PrSouRequirementCancelEditContext context) { return context; }
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
