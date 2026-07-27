package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.plugin.event.eidtplan;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.IPrSouProjectEditPlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.IPrSouProjectEditPlanValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan.PrSouProjectEditPlanContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 招标计划 - 项目计划 - 编辑插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouProjectEditPlanPlugin implements IPrSouProjectEditPlanPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouProjectEditPlanContext judgeEditPlanAuth(PrSouProjectEditPlanContext context) {
        // 1: 查询现有的项目计划
        if (context.getParam().getProjectPlanId() != null) {
            ExtPrSouProjectPlan existProjectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), context.getParam().getProjectPlanId(), ExtPrSouProjectPlan.class);
            if (existProjectPlan != null) {
                AssertUtils.isTrue(ExtPrSouProjectPlanStatusEnum.NORMAL.name().equals(existProjectPlan.getPlanStatus())
                        || ExtPrSouProjectPlanStatusEnum.CANCEL.name().equals(existProjectPlan.getPlanStatus()), "单据状态不是正常或者取消，禁止编辑");
            }
            context.setExistProjectPlan(existProjectPlan);
        }

        return context;
    }

    @Override
    @ApiOperation("前置处理")
    public PrSouProjectEditPlanContext beforeEditPlan(PrSouProjectEditPlanContext context) {
        return SdkPluginProxy.proxy(IPrSouProjectEditPlanValidatePlugin.class, context).execute(context);
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouProjectEditPlanContext executeEditPlan(PrSouProjectEditPlanContext context) {
        if (context.getExistProjectPlan() != null) {
            qlService.update(Collections.singletonList(context.getParam()));
        } else {
            qlService.create(Collections.singletonList(context.getParam()));
        }
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
