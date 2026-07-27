package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划 - 计划取消编辑校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
public interface IPrSouRequirementCancelEditValidatePlugin extends ISdkFunctionPlugin<PrSouRequirementCancelEditContext> {
    /**
     * 唯一对外调用方法
     * @param context 参数
     * @return 返回
     */
    @Override
    @ApiModelProperty("唯一对外调用方法")
    default PrSouRequirementCancelEditContext execute(PrSouRequirementCancelEditContext context) {
        // 1: 校验及转化计划取消单
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditValidatePlugin.class, context).validateAndConvertCancel(context);
        // 2: 校验及转化计划取消明细
        context = SdkPluginProxy.proxy(IPrSouRequirementCancelEditValidatePlugin.class, context).validateAndConvertCancelLines(context);
        // 3: 校验及转化计划取消附件
        return SdkPluginProxy.proxy(IPrSouRequirementCancelEditValidatePlugin.class, context).validateAndConvertCancelAttaches(context);
    }
    /**
     * 校验及转化计划取消单
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化计划取消单")
    default PrSouRequirementCancelEditContext validateAndConvertCancel(PrSouRequirementCancelEditContext context) { return context; }

    /**
     * 校验及转化计划取消明细
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化计划取消明细")
    default PrSouRequirementCancelEditContext validateAndConvertCancelLines(PrSouRequirementCancelEditContext context) { return context; }

    /**
     * 校验及转化计划取消附件
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化计划取消附件")
    default PrSouRequirementCancelEditContext validateAndConvertCancelAttaches(PrSouRequirementCancelEditContext context) { return context; }

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
