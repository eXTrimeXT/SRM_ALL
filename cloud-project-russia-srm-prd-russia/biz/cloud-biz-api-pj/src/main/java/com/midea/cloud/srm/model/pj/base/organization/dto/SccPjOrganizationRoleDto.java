package com.midea.cloud.srm.model.pj.base.organization.dto;

import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRole;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author huangbf3
 */
@ApiModel("组织角色明细")
@Data
@Accessors(chain = true)
public class SccPjOrganizationRoleDto {

    @ApiModelProperty("组织角色")
    private SccPjOrganizationRole organizationRole;

    @ApiModelProperty("组织角色人员")
    private List<SccPjOrganizationRoleUser> organizationRoleUsers;
}
