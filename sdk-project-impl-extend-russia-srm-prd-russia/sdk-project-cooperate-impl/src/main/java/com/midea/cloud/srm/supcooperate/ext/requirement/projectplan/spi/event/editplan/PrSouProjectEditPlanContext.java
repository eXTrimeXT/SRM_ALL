package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.event.editplan;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 招标计划 - 项目计划 - 编辑上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouProjectEditPlanContext extends SdkPluginContext {

    @ApiModelProperty("入参")
    private ExtPrSouProjectPlan param;

    @Nullable
    @ApiModelProperty("现有的项目计划")
    private ExtPrSouProjectPlan existProjectPlan;

    @ApiModelProperty("项目计划实体")
    private ExtPrSouProjectPlan projectPlanEntity;
    @Override
    @Nullable
    public String getSceneType() {
        return param.getSceneType();
    }

    public PrSouProjectEditPlanContext(ExtPrSouProjectPlan param) {
        this.param = param;
    }

}
