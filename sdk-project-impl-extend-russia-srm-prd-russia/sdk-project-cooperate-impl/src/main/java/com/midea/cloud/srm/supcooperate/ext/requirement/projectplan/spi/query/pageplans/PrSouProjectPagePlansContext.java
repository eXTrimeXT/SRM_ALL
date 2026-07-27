package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.spi.query.pageplans;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 招标计划 - 项目计划 - 列表查询上下文
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouProjectPagePlansContext extends SdkPluginContext {

    @ApiModelProperty("入参: 列表查询条件")
    private ExtPrSouProjectPlanQueryDTO queryParam;

    @ApiModelProperty("查询结果")
    private List<ExtPrSouProjectPlan> result;

    public PrSouProjectPagePlansContext(ExtPrSouProjectPlanQueryDTO queryParam) {
        this.queryParam = queryParam;
    }

}
