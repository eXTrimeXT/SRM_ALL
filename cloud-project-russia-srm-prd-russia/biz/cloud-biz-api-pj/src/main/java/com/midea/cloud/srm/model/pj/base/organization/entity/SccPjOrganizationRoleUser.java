package com.midea.cloud.srm.model.pj.base.organization.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 * 组织流程角色员工
 */
@Data
@TableName("scc_pj_organization_role_user")
public class SccPjOrganizationRoleUser extends BaseEntity {
    @TableId
    @ApiModelProperty("主键ID")
    private Long rowId;

    @ApiModelProperty("组织角色ID")
    private Long organizationRoleId;

    @ApiModelProperty("员工名称")
    private String userNickName;

    @ApiModelProperty("员工账号")
    private String userName;

    @ApiModelProperty("HR员工ID")
    private Long hrUserId;

    @ApiModelProperty("SRM员工ID")
    private Long srmUserId;
}
