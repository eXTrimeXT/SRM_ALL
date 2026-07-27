package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.batchassign;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 招标计划池 - 分配/转办插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
public interface IPrSouRequirementPoolBatchAssignPlugin extends ISdkPlugin {
    /**
     * 校验操作条件/权限
     * @param context
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default PrSouRequirementPoolBatchAssignContext judgeBatchAssignAuth(PrSouRequirementPoolBatchAssignContext context) { return context; }
    /**
     * 前置处理
     * @param context
     * @return 返回
     */
    @ApiOperation("前置处理")
    default PrSouRequirementPoolBatchAssignContext beforeBatchAssign(PrSouRequirementPoolBatchAssignContext context) { return context; }
    /**
     * 执行处理
     * @param context
     * @return 返回
     */
    @ApiOperation("执行处理")
    default PrSouRequirementPoolBatchAssignContext executeBatchAssign(PrSouRequirementPoolBatchAssignContext context) { return context; }
    /**
     * 后置处理
     * @param context
     * @return 返回
     */
    @ApiOperation("后置处理")
    default PrSouRequirementPoolBatchAssignContext afterBatchAssign(PrSouRequirementPoolBatchAssignContext context) { return context; }
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
