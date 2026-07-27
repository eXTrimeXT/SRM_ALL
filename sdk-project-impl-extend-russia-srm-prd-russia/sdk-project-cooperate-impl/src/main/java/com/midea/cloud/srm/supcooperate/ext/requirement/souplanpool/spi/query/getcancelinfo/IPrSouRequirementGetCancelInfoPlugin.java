package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.getcancelinfo;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划池 - 取消单据查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
public interface IPrSouRequirementGetCancelInfoPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouRequirementGetCancelInfoContext judgeGetCancelInfoAuth(PrSouRequirementGetCancelInfoContext context) { return context; }
    /**
     * 前置处理
     * @param context
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouRequirementGetCancelInfoContext beforeGetCancelInfo(PrSouRequirementGetCancelInfoContext context) { return context; }
    /**
     * 执行处理
     * @param context
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouRequirementGetCancelInfoContext executeGetCancelInfo(PrSouRequirementGetCancelInfoContext context) { return context; }
    /**
     * 后置处理
     * @param context
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouRequirementGetCancelInfoContext afterGetCancelInfo(PrSouRequirementGetCancelInfoContext context) { return context; }
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
