package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 工作小组
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSouGroupEditDTO extends BaseObjectX {

    /** @see SouGroup#getGroupId */
    @ApiModelProperty("ID")
    private Long groupId;

    /** @see SouGroup#getUserId */
    @ApiModelProperty(value = "成员账号", required = true)
    private String userName;

    /** @see SouGroup#getPhone */
    @ApiModelProperty("电话(长度限制240)")
    private String phone;

    /** @see SouGroup#getEmail */
    @ApiModelProperty("邮箱(长度限制240)")
    private String email;

    /** @see SouGroup#getPosition */
    @ApiModelProperty("岗位(长度限制60)")
    private String position;

    /** @see SouGroup#getGroupRole */
    @ApiModelProperty("角色-SouGroupRoleEnum(字典值: SOU_GROUP_RULE)")
    private String groupRole;

    /** @see SouGroup#getScoreAuth */
    @ApiModelProperty("评分权限-SouGroupScoreAuthEnum(字典值:SCC_SOU_SCORE_DIMENSION_CODE)")
    private String scoreAuth;

    /** @see SouGroup#getOperateAuth */
    @ApiModelProperty("操作权限-SouGroupOperateAuthEnum(字典值:SOU_GROUP_OPERATE_AUTH)(长度限制200)")
    private String operateAuth;

    /** @see SouGroup#getSortIndex */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

}
