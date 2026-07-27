package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.pj.sou.score.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouGroupOperateAuthEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouGroupRoleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源核心 - 询价工作小组
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_group")
@ApiModel("工作小组")
public class SouGroup extends BaseEntity<SouGroup> {

    @TableId("GROUP_ID")
    @ApiModelProperty("ID")
    private Long groupId;

    /** @see SouProject#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /**
     * PS: {@link User#getUserType}必须为采购商
     * @see User#getUserId
     */
    @TableField("USER_ID")
    @ApiModelProperty("成员ID")
    private Long userId;

    /**
     * PS: {@link User#getUserType}必须为采购商
     * @see User#getUsername
     */
    @TableField("USER_NAME")
    @ApiModelProperty("成员账号")
    private String userName;

    /**
     * PS: {@link User#getUserType}必须为采购商
     * @see User#getNickname
     */
    @TableField("FULL_NAME")
    @ApiModelProperty("成员姓名")
    private String fullName;

    /** @see User#getPhone */
    @TableField("PHONE")
    @ApiModelProperty("电话")
    private String phone;

    /** @see User#getEmail */
    @TableField("EMAIL")
    @ApiModelProperty("邮箱")
    private String email;

    /** @see User#getDepartment */
    @TableField("POSITION")
    @ApiModelProperty("岗位")
    private String position;

    /** @see SouGroupRoleEnum */
    @TableField("GROUP_ROLE")
    @ApiModelProperty("角色-SouGroupRoleEnum(字典值:SOU_GROUP_ROLE)")
    private String groupRole;

    /** @see SouScoreDimensionCodeEnum */
    @TableField("SCORE_AUTH")
    @ApiModelProperty("评分权限-SouGroupScoreAuthEnum(字典值:SCC_SOU_SCORE_DIMENSION_CODE)")
    private String scoreAuth;

    /** @see SouGroupOperateAuthEnum */
    @TableField("OPERATE_AUTH")
    @ApiModelProperty("操作权限-SouGroupOperateAuthEnum(字典值:SOU_GROUP_OPERATE_AUTH)")
    private String operateAuth;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
