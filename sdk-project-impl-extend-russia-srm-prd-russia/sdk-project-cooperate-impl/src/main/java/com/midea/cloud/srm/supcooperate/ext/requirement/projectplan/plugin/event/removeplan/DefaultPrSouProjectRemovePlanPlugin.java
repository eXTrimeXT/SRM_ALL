package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.removeplan;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.removeplan.IPrSouProjectRemovePlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.removeplan.PrSouProjectRemovePlanContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 招标计划 - 项目计划 - 删除插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectRemovePlanPlugin implements IPrSouProjectRemovePlanPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouProjectRemovePlanContext judgeRemovePlanAuth(PrSouProjectRemovePlanContext context) {
        ExtPrSouProjectPlan existProjectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), context.getProjectPlanId(),  ExtPrSouProjectPlan.class);
        AssertUtils.notNull(existProjectPlan, "项目计划[{0}]不存在", context.getProjectPlanId());

        context.setResult(existProjectPlan);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouProjectRemovePlanContext executeRemovePlan(PrSouProjectRemovePlanContext context) {
        qlService.deleteByKeys(ExtPrSouProjectPlan.class.getSimpleName(), Collections.singletonList(context.getProjectPlanId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
