package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.releasebudget;

import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.releasebudget.IRequirementInitReleaseBudgetPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.releasebudget.RequirementInitReleaseBudgetContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 释放预算插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
public class PrSouRequirementInitReleaseBudgetPlugin implements IRequirementInitReleaseBudgetPlugin {

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitReleaseBudgetContext judgeReleaseBudgetAuth(RequirementInitReleaseBudgetContext context) {
        throw new IllegalArgumentException("招标计划不支持该功能");
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
