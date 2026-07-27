package com.midea.cloud.srm.sou.expert.spi.event.createexpertscore;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * 寻源 - 专家库 - 专家评审创建校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
public interface IExtSouExpertScoreCreateValidatePlugin extends ISdkFunctionPlugin<ExtSouExpertScoreCreateContext> {
    /**
     * 唯一对外调用方法
     * @param context 参数
     * @return 返回
     */
    @Override
    @ApiModelProperty("唯一对外调用方法")
    default ExtSouExpertScoreCreateContext execute(ExtSouExpertScoreCreateContext context) {
        // 1: 校验及转化专家评审
        context = SdkPluginProxy.proxy(IExtSouExpertScoreCreateValidatePlugin.class, context).validateAndConvertExpertScore(context);
        // 2: 校验及转化专家评审详情
        return SdkPluginProxy.proxy(IExtSouExpertScoreCreateValidatePlugin.class, context).validateAndConvertExpertScoreLines(context);
    }

    /**
     * 校验及转化专家评审
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化专家评审")
    default ExtSouExpertScoreCreateContext validateAndConvertExpertScore(ExtSouExpertScoreCreateContext context) { return context; }

    /**
     * 校验及转化专家评审详情
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化专家评审详情")
    default ExtSouExpertScoreCreateContext validateAndConvertExpertScoreLines(ExtSouExpertScoreCreateContext context) { return context; }

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
