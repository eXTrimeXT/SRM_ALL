package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.unbindplan;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.unbindplan.IPrSouRequirementUnbindPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.unbindplan.PrSouRequirementUnBindPlanContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 项目计划 - 解绑项目插件
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementUnbindPlanPlugin implements IPrSouRequirementUnbindPlanPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementUnBindPlanContext judgeUnbindPlanAuth(PrSouRequirementUnBindPlanContext context) {
        ExtPrSouProjectPlan projectPlan; {
            if (context.getProjectPlanId() != null) {
                projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), context.getProjectPlanId(), ExtPrSouProjectPlan.class);
            } else {
                projectPlan = qlService.queryByWrapper(QlWrappers.query(ExtPrSouProjectPlan.class)
                        .eq(ExtPrSouProjectPlan::getRequirementHeadId, context.getRequirementHeadId()), ExtPrSouProjectPlan.class)
                        .stream().findAny().orElse(null);
                if (projectPlan == null) { return context; }
            }
        }
        AssertUtils.notNull(projectPlan, "项目计划不存在");
        AssertUtils.isTrue(ExtPrSouProjectPlanStatusEnum.FINISH.name().equals(projectPlan.getPlanStatus()), "项目计划不是完结状态，不能解绑");

        context.setProjectPlan(projectPlan);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementUnBindPlanContext executeUnbindPlan(PrSouRequirementUnBindPlanContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouProjectPlan.class)
                .set(ExtPrSouProjectPlan::getPlanStatus, ExtPrSouProjectPlanStatusEnum.NORMAL)
                .set(ExtPrSouProjectPlan::getRequirementHeadId, null)
                .eq(ExtPrSouProjectPlan::getProjectPlanId, context.getProjectPlanId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
