package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.bindplan;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 招标计划 - 项目计划 - 绑定项目上下文
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementBindPlanContext extends SdkPluginContext {

    /** {@link ExtPrSouProjectPlan#getProjectPlanId} */
    @ApiModelProperty("入参: 项目计划ID")
    private Long projectPlanId;

    /** {@link ExtPrSouRequirementHead#getRequirementHeadId} */
    @ApiModelProperty("入参: 招标计划ID")
    private Long requirementHeadId;

    /** {@link ExtPrSouProjectPlan#getSceneType} */
    @ApiModelProperty("应用场景")
    private String sceneType;

    @ApiModelProperty("项目计划(IPrSouRequirementBindPlanPlugin#judgeBindPlanAuth环节填补)")
    private ExtPrSouProjectPlan projectPlan;

    public PrSouRequirementBindPlanContext(long projectPlanId, long requirementHeadId, String sceneType) {
        this.projectPlanId = projectPlanId;
        this.requirementHeadId = requirementHeadId;
        this.sceneType = sceneType;
    }

}
