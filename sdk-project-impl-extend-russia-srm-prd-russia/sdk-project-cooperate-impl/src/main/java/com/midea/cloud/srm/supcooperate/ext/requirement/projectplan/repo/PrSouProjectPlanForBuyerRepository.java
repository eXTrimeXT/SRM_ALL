package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanEventService;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanQueryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplan.flow.PrSouRequirementFlowServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * mql - 招标计划 - 项目计划
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/05
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouProjectPlanForBuyerRepository extends CrudRepository {

    @Autowired
    private PrSouProjectPlanQueryService prSouProjectPlanQueryService;
    @Autowired
    private PrSouProjectPlanEventService prSouProjectPlanEventService;
    @Autowired
    private PrSouRequirementFlowServiceImpl prSouRequirementFlowService;

    public PrSouProjectPlanForBuyerRepository() {
        super();
        // 业务查询
        this.register("listProjectPlans", this::listProjectPlans, false, "项目计划列表查询");
        this.registerBefore("listProjectPlans", this::beforelistProjectPlans);
        this.registerAfter("listProjectPlans", this::afterlistProjectPlans);
        this.register("getPlan", this::getPlan, false, "项目计划详情查询");
        // 业务事件
        this.register("editPlan", this::editPlan, true, "编辑项目计划");
        this.register("removePlan", this::removePlan, true, "删除项目计划");
    }

    @ApiOperation("项目计划列表查询")
    private QlResult listProjectPlans(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        return super.query(ProxyQlQueryAction.proxy(queryAction, "query"));
    }

    @ApiOperation("前置处理: 项目计划列表查询")
    private void beforelistProjectPlans(QlQueryAction var1, Payload var2) {}

    @ApiOperation("后置处理: 项目计划列表查询")
    private void afterlistProjectPlans(QlQueryAction queryAction, QlResult result, Map<String/* mqlType */, Collection<Record>> repoData) {}

    @ApiOperation("项目计划详情查询")
    private QlResult getPlan(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long projectPlanId; {
            List<ExtPrSouProjectPlan> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouProjectPlan>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtPrSouProjectPlan param = params.get(0);
            AssertUtils.notNull(param.getProjectPlanId(), "缺少projectPlanId参数");
            projectPlanId = param.getProjectPlanId();
        }

        ExtPrSouProjectPlan result = prSouProjectPlanQueryService.getPlan(projectPlanId);
        return ResultUtil.build(queryAction, "projectPlanId", Collections.singletonList(result), false);
    }

    @ApiOperation("编辑项目计划")
    private QlResult editPlan(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        ExtPrSouProjectPlan param; {
            List<ExtPrSouProjectPlan> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouProjectPlan>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            param = params.get(0);
            param.setSceneType(ISdkPlugin.DEFAULT_SCENE);
        }

        ExtPrSouProjectPlan result = prSouProjectPlanEventService.editPlan(param);
        return ResultUtil.build(queryAction, "projectPlanId", Collections.singletonList(result), false);
    }

    @ApiOperation("删除项目计划")
    private QlResult removePlan(QlQueryAction queryAction) {
        SouUserTypeCheckUtils.checkIsBuyer();

        long projectPlanId; {
            List<ExtPrSouProjectPlan> params = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), new TypeReference<List<ExtPrSouProjectPlan>>() {});
            AssertUtils.notEmpty(params, "缺少数据");
            ExtPrSouProjectPlan param = params.get(0);
            AssertUtils.notNull(param.getProjectPlanId(), "缺少projectPlanId参数");
            projectPlanId = param.getProjectPlanId();
        }

        ExtPrSouProjectPlan result = prSouProjectPlanEventService.removePlan(projectPlanId);
        return ResultUtil.build(queryAction, "projectPlanId", Collections.singletonList(result), false);
    }

}
