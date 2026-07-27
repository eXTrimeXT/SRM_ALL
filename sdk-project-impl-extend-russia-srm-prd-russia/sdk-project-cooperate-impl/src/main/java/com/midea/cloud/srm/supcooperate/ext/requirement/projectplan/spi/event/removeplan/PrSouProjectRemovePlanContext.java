package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.removeplan;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 招标计划 - 项目计划 - 删除上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouProjectRemovePlanContext extends SdkPluginContext {

    /** {@link ExtPrSouProjectPlan#getProjectPlanId} */
    @ApiModelProperty("入参: 项目计划ID")
    private Long projectPlanId;

    /** {@link ExtPrSouProjectPlan#getSceneType} */
    @ApiModelProperty("入参: 应用场景")
    private String sceneType;

    @ApiModelProperty("被删除的数据")
    private ExtPrSouProjectPlan result;

    public PrSouProjectRemovePlanContext(long projectPlanId, String sceneType) {
        this.projectPlanId = projectPlanId;
        this.sceneType = sceneType;
    }

}
