package com.midea.cloud.srm.sou.expert.spi.event.editapply;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家申请编辑校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
public interface IExtSouExpertEditApplyValidatePlugin extends ISdkFunctionPlugin<ExtSouExpertEditApplyContext> {
    /**
     * 唯一对外调用方法
     * @param context 参数
     * @return 返回
     */
    @Override
    @ApiModelProperty("唯一对外调用方法")
    default ExtSouExpertEditApplyContext execute(ExtSouExpertEditApplyContext context) {
        // 1: 校验及转化申请
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertApply(context);
        // 2: 校验及转化附件
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertAttaches(context);
        // 3: 校验及转化适用品类
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertCategoryRelations(context);
        // 4: 校验及转化学历
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertEducations(context);
        // 5: 校验及转化适用组织
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertOrgRelations(context);
        // 6: 校验及转化工作经历
        context = SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertWorks(context);
        // 7: 校验及转化亲属工作经历
        return SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).validateAndConvertWorkRelations(context);
    }

    /**
     * 校验及转化申请
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化申请")
    default ExtSouExpertEditApplyContext validateAndConvertApply(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 校验及转化附件
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化附件")
    default ExtSouExpertEditApplyContext validateAndConvertAttaches(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 校验及转化适用品类
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化适用品类")
    default ExtSouExpertEditApplyContext validateAndConvertCategoryRelations(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 校验及转化学历
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化学历")
    default ExtSouExpertEditApplyContext validateAndConvertEducations(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 校验及转化适用组织
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化适用组织")
    default ExtSouExpertEditApplyContext validateAndConvertOrgRelations(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 校验及转化工作经历
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化工作经历")
    default ExtSouExpertEditApplyContext validateAndConvertWorks(ExtSouExpertEditApplyContext context) { return context; }

    /**
     * 校验及转化亲属工作经历
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验及转化亲属工作经历")
    default ExtSouExpertEditApplyContext validateAndConvertWorkRelations(ExtSouExpertEditApplyContext context) { return context; }

}
