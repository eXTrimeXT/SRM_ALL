package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.getplan;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 招标计划 - 项目计划 - 详情查询上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouProjectGetPlanContext extends SdkPluginContext {

    /** {@link ExtPrSouProjectPlan#getProjectPlanId} */
    @ApiModelProperty("入参: 项目计划ID")
    private Long projectPlanId;

    /** {@link ExtPrSouProjectPlan#getSceneType} */
    @ApiModelProperty("入参: 应用场景")
    private String sceneType;

    @ApiModelProperty("查询结果")
    private ExtPrSouProjectPlan result;

    public PrSouProjectGetPlanContext(long projectPlanId, String sceneType) {
        this.projectPlanId = projectPlanId;
        this.sceneType = sceneType;
    }

}
