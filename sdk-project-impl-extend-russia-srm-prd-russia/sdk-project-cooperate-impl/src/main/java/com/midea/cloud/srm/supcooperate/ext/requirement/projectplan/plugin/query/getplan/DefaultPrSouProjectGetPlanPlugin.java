package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.query.getplan;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.getplan.IPrSouProjectGetPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.getplan.PrSouProjectGetPlanContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 项目计划 - 详情查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectGetPlanPlugin implements IPrSouProjectGetPlanPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("执行查询")
    public PrSouProjectGetPlanContext executeGetPlan(PrSouProjectGetPlanContext context) {
        context.setResult(qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), context.getProjectPlanId(), ExtPrSouProjectPlan.class));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
