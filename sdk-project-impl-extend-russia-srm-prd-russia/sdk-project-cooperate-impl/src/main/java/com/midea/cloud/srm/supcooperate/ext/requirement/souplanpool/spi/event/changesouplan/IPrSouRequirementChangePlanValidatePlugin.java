package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划池 - 计划变更校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
public interface IPrSouRequirementChangePlanValidatePlugin extends ISdkFunctionPlugin<PrSouRequirementChangePlanContext> {
    /**
     * 唯一对外调用方法
     * @param context 参数
     * @return 返回
     */
    @Override
    @ApiModelProperty("唯一对外调用方法")
    default PrSouRequirementChangePlanContext execute(PrSouRequirementChangePlanContext context) {
        // 1: 校验及转化采购申请/招标计划
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanValidatePlugin.class, context).validateAndConvertReqHead(context);
        // 2: 校验及转化工作成员
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanValidatePlugin.class, context).validateAndConvertReqGroups(context);
        // 3: 校验及转化推荐供应商
        context = SdkPluginProxy.proxy(IPrSouRequirementChangePlanValidatePlugin.class, context).validateAndConvertReqVendors(context);
        // 4: 校验及转化附件
        return SdkPluginProxy.proxy(IPrSouRequirementChangePlanValidatePlugin.class, context).validateAndConvertReqAttaches(context);
    }
    /**
     * 校验及转化采购申请/招标计划
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化采购申请/招标计划")
    default PrSouRequirementChangePlanContext validateAndConvertReqHead(PrSouRequirementChangePlanContext context) { return context; }

    /**
     * 校验及转化工作成员
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化工作成员")
    default PrSouRequirementChangePlanContext validateAndConvertReqGroups(PrSouRequirementChangePlanContext context) { return context; }
    /**
     * 校验及转化推荐供应商
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化推荐供应商")
    default PrSouRequirementChangePlanContext validateAndConvertReqVendors(PrSouRequirementChangePlanContext context) { return context; }
    /**
     * 校验及转化附件
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化附件")
    default PrSouRequirementChangePlanContext validateAndConvertReqAttaches(PrSouRequirementChangePlanContext context) { return context; }
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
