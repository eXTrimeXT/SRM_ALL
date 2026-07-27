package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.unbindplan;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 招标计划 - 项目计划 - 解绑项目上下文
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementUnBindPlanContext extends SdkPluginContext {

    /** {@link ExtPrSouProjectPlan#getProjectPlanId} */
    @Nullable
    @ApiModelProperty("入参: 项目计划ID")
    private Long projectPlanId;

    /** {@link ExtPrSouRequirementHead#getRequirementHeadId} */
    @Nullable
    @ApiModelProperty("入参: 招标计划ID")
    private Long requirementHeadId;

    /** {@link ExtPrSouProjectPlan#getSceneType} */
    @ApiModelProperty("入参: 应用场景")
    private String sceneType;

    @ApiModelProperty("项目计划(IPrSouRequirementBindPlanPlugin#judgeBindPlanAuth环节填补)")
    private ExtPrSouProjectPlan projectPlan;

    public PrSouRequirementUnBindPlanContext(@Nullable Long projectPlanId, @Nullable Long requirementHeadId, String sceneType) {
        this.projectPlanId = projectPlanId;
        this.requirementHeadId = requirementHeadId;
        this.sceneType = sceneType;
    }

}
