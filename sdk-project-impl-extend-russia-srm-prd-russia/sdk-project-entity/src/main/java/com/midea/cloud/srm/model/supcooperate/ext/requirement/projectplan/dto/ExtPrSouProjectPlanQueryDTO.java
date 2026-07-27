package com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 招标计划 - 项目计划 - 列表查询条件
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouProjectPlanQueryDTO extends BasePage {

    /** {@link ExtPrSouProjectPlan#getProjectName} */
    @ApiModelProperty("项目名称(模糊查询)")
    private String projectName;

    /** {@link ExtPrSouProjectPlan#getCreatedBy} */
    @ApiModelProperty("创建人账号")
    private String createdBy;

    /** {@link ExtPrSouProjectPlan#getPlanStatus} */
    @ApiModelProperty("项目状态(等值查询)")
    private String planStatus;

    /**
     * 入参格式化
     */
    public void formatParams() {
        projectName = StringUtils.trimToNull(projectName);
        createdBy = StringUtils.trimToNull(createdBy);
        planStatus = StringUtils.trimToNull(planStatus);
    }

}
