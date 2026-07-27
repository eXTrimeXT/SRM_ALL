package com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.query.getrequire;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 采购申请 - 详情查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/06/02
 */
public interface IRequirementInitGetPlugin extends ISdkPlugin {
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default RequirementInitGetContext judgeGetRequirementAuth(RequirementInitGetContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("前置处理")
    default RequirementInitGetContext beforeGetRequirement(RequirementInitGetContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("数据查询")
    default RequirementInitGetContext executeGetRequirement(RequirementInitGetContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("后置处理")
    default RequirementInitGetContext afterGetRequirement(RequirementInitGetContext context) { return context; }
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