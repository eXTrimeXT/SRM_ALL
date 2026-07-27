package com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.query.listrequires;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import io.swagger.annotations.ApiOperation;

/**
 * 采购申请 - 列表查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/06/02
 */
public interface IRequirementInitListPlugin extends ISdkPlugin {
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("前置处理")
    default RequirementInitListContext beforeListRequirements(RequirementInitListContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("数据查询")
    default RequirementInitListContext executeListRequirements(RequirementInitListContext context) { return context; }
    /**
     * 备注
     * @param context
     * @return 返回
     */
    @ApiOperation("后置处理")
    default RequirementInitListContext afterListRequirements(RequirementInitListContext context) { return context; }
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
