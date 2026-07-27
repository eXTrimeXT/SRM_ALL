package com.midea.cloud.srm.supcooperate.spi.meiql.requirement.cancle.event.canclerequirements;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.context.RequirementCancleContext;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface IReuirementCancleEventPlugin extends ISdkPlugin {

    /**
     * 校验操作条件
     * @param context
     * @return
     */
    @ApiOperation("校验操作条件/权限")
    default RequirementCancleContext judgeCancleRequirementAuth(RequirementCancleContext context) { return context; }

    /**
     * 前置处理
     * @param context
     * @return
     */
    @ApiOperation("前置处理")
    default RequirementCancleContext beforeCancleRequirementAuth(RequirementCancleContext context) { return context; }

    /**
     * 处理
     * @param context
     * @return
     */
    @ApiOperation("处理")
    default RequirementCancleContext executeCancleRequirementAuth(RequirementCancleContext context) { return context; }

    /**
     * 后置处理
     * @param context
     * @return
     */
    @ApiOperation("后置处理")
    default RequirementCancleContext afterCancleRequirementAuth(RequirementCancleContext context) { return context; }
}
