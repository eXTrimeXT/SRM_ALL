package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.dept.entity.Dept;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * (非材) 招标计划工作成员表
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_group")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementGroup extends BaseEntity<ExtPrSouRequirementGroup> {

    @TableId("REQUIREMENT_GROUP_ID")
    @ApiModelProperty("主键ID")
    private Long requirementGroupId;

    /** @see PrRequirementHead#getRequirementHeadId */
    @ApiModelProperty("招标计划ID")
    @TableField("REQUIREMENT_HEAD_ID")
    private Long requirementHeadId;

    @TableField("USER_ID")
    @ApiModelProperty("用户ID")
    private Long userId;

    @TableField("USERNAME")
    @ApiModelProperty("用户账号")
    private String username;

    @TableField("FULL_NAME")
    @ApiModelProperty("用户昵称")
    private String fullName;

    /** {@link PrSouRequirementGroupTypeEnum} 集合，逗号分隔 */
    @TableField("GROUP_TYPE")
    @ApiModelProperty("工作成员职责类型")
    private String groupType;

    @TableField("PHONE")
    @ApiModelProperty("联系方式")
    private String phone;

    @TableField("EMAIL")
    @ApiModelProperty("邮箱")
    private String email;

    @TableField("WORK_YEAR")
    @ApiModelProperty("工作年限")
    private BigDecimal workYear;

    /** @see Dept#getDeptid */
    @TableField("DEPARTMENT_ID")
    @ApiModelProperty("所属部门ID")
    private String departmentId;

    /** @see Dept#getDescr */
    @TableField("DEPARTMENT_NAME")
    @ApiModelProperty("所属部门名称")
    private String departmentName;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
