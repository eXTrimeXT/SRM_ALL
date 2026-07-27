package com.midea.cloud.srm.model.pj.hruser.dto;

import com.midea.cloud.srm.model.base.organization.entity.Organization;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
@ApiOperation("用户组织信息")
public class HrUserOrgnizationDto {
    @ApiModelProperty("用户部门组织")
    private Organization departmentOrganization;

    @ApiModelProperty("用户公司组织信息")
    private Organization ouOrganization;

    @ApiModelProperty("用户板块组织信息")
    private Organization buOrganization;
}
