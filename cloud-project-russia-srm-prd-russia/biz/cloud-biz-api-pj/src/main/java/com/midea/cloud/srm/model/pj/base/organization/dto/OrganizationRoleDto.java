package com.midea.cloud.srm.model.pj.base.organization.dto;

import com.alibaba.excel.annotation.ExcelProperty;
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
@ApiModel("流程角色")
@Data
public class OrganizationRoleDto {


    @ApiModelProperty("组织编码*")
    @ExcelProperty(value = "组织编码*",index=0)
    private String organizationCode;

    @ApiModelProperty("组织/部门名称*")
    @ExcelProperty(value = "组织/部门名称*",index=1)
    private String organizationName;

    @ApiModelProperty("流程角色编码（唯一）*")
    @ExcelProperty(value = "流程角色编码（唯一）*",index=2)
    private String roleCode;

    @ApiModelProperty("流程角色名称*")
    @ExcelProperty(value = "流程角色名称*",index=3)
    private String roleName;

    @ApiModelProperty("上级流程角色编码")
    @ExcelProperty(value = "上级流程角色编码",index=4)
    private String parentRoleCode;

    @ApiModelProperty("上级流程角色名称")
    @ExcelProperty(value = "上级流程角色名称",index=5)
    private String parentRoleName;

    @ApiModelProperty("错误提示信息")
    @ExcelProperty(value = "错误提示信息",index=6)
    private String errorMessage;
}
