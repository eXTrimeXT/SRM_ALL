package com.midea.cloud.srm.model.pj.base.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 * 组织流程角色
 */
@Data
@TableName("scc_pj_organization_role")
public class SccPjOrganizationRole extends BaseEntity {
    @TableId
    @ApiModelProperty("主键ID")
    private Long rowId;

    @ApiModelProperty("SRM组织ID")
    private Long srmOrgnizationId;

    @ApiModelProperty("HR组织ID")
    private Long hrOrgnizationId;

    @ApiModelProperty("组织/部门")
    private String groupName;

    @ApiModelProperty("上级流程角色编码")
    private String parentRoleCode;

    @ApiModelProperty("上级流程角色名称")
    private String parentRoleName;

    @ApiModelProperty("流程角色编码")
    private String roleCode;

    @ApiModelProperty("流程角色名称")
    private String roleName;

    @ApiModelProperty("是否启用标志Y/N")
    private String useFlag;
}
