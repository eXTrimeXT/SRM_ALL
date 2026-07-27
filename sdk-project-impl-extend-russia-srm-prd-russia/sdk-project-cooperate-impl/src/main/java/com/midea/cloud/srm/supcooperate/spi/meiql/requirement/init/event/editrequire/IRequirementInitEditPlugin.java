package com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 采购申请 - 详情编辑插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/06/02
 */
public interface IRequirementInitEditPlugin extends ISdkPlugin {
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("校验操作条件/权限")
    default RequirementInitEditContext judgeEditRequirementAuth(RequirementInitEditContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("数据准备")
    default RequirementInitEditContext prepareEditRequirement(RequirementInitEditContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("前置处理")
    default RequirementInitEditContext beforeEditRequirement(RequirementInitEditContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("数据保存")
    default RequirementInitEditContext executeEditRequirement(RequirementInitEditContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("后置处理")
    default RequirementInitEditContext afterEditRequirement(RequirementInitEditContext context) { return context; }
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