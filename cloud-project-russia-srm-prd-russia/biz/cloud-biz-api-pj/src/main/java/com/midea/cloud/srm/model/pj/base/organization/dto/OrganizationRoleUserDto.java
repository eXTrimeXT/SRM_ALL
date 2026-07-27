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
@ApiModel("流程角色用户")
@Data
public class OrganizationRoleUserDto {

    @ApiModelProperty("流程角色编码*")
    @ExcelProperty(value = "流程角色编码*",index=0)
    private String roleCode;

    @ApiModelProperty("人员账号*")
    @ExcelProperty(value = "人员账号*",index=1)
    private String userName;

    @ApiModelProperty("错误提示信息")
    @ExcelProperty(value = "错误提示信息",index=2)
    private String errorMessage;

}
