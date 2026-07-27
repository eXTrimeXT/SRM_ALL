package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.importexcel;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import io.swagger.annotations.ApiModelProperty;

/**
 * 招标计划 - 项目计划 - 导入校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/06
 */
public interface IPrSouProjectImportPlanValidatePlugin extends ISdkFunctionPlugin<PrSouProjectImportPlanContext> {
    /**
     * 唯一对外调用方法
     * @param context 参数
     * @return 返回
     */
    @Override
    @ApiModelProperty("唯一对外调用方法")
    default PrSouProjectImportPlanContext execute(PrSouProjectImportPlanContext context) {
        // 1: 解析导入文件
        context = SdkPluginProxy.proxy(IPrSouProjectImportPlanValidatePlugin.class, context).parseImportFile(context);
        // 2: 校验及转化项目计划
        return SdkPluginProxy.proxy(IPrSouProjectImportPlanValidatePlugin.class, context).validateAndConvertProjectPlans(context);
    }

    /**
     * 解析导入文件
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("解析导入文件")
    default PrSouProjectImportPlanContext parseImportFile(PrSouProjectImportPlanContext context) { return context; }

    /**
     * 校验及转化项目计划
     * @param context 参数
     * @return 返回
     */
    @ApiModelProperty("校验及转化项目计划")
    default PrSouProjectImportPlanContext validateAndConvertProjectPlans(PrSouProjectImportPlanContext context) { return context; }

}
