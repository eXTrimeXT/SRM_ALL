package com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 非招需求池 - 创建寻源插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/04
 */
public interface IExtPrRequirementCreateSouPlugin extends ISdkPlugin {
    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default ExtPrRequirementCreateSouContext judgeCreateSouAuth(ExtPrRequirementCreateSouContext context) { return context; }

    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("前置处理")
    default ExtPrRequirementCreateSouContext beforeCreateSou(ExtPrRequirementCreateSouContext context) { return context; }

    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("执行处理")
    default ExtPrRequirementCreateSouContext executeCreateSou(ExtPrRequirementCreateSouContext context) { return context; }

    /**
     * 备注
     * @param context 参数
     * @return 返回
     */
    @ApiOperation("后置处理")
    default ExtPrRequirementCreateSouContext afterCreateSou(ExtPrRequirementCreateSouContext context) { return context; }

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
