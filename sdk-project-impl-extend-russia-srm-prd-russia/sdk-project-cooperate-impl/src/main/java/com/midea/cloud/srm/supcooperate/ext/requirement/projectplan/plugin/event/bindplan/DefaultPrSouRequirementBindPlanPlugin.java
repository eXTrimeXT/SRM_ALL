package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.bindplan;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.bindplan.IPrSouRequirementBindPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.bindplan.PrSouRequirementBindPlanContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 项目计划 - 绑定项目插件
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementBindPlanPlugin implements IPrSouRequirementBindPlanPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementBindPlanContext judgeBindPlanAuth(PrSouRequirementBindPlanContext context) {
        ExtPrSouProjectPlan projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), context.getProjectPlanId(), ExtPrSouProjectPlan.class);
        AssertUtils.notNull(projectPlan, "项目计划不存在");
        AssertUtils.isTrue(ExtPrSouProjectPlanStatusEnum.NORMAL.name().equals(projectPlan.getPlanStatus()), "项目计划不是正常状态，不能绑定");

        context.setProjectPlan(projectPlan);
        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementBindPlanContext executeBindPlan(PrSouRequirementBindPlanContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouProjectPlan.class)
                .set(ExtPrSouProjectPlan::getPlanStatus, ExtPrSouProjectPlanStatusEnum.FINISH)
                .set(ExtPrSouProjectPlan::getRequirementHeadId, context.getRequirementHeadId())
                .eq(ExtPrSouProjectPlan::getProjectPlanId, context.getProjectPlanId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
