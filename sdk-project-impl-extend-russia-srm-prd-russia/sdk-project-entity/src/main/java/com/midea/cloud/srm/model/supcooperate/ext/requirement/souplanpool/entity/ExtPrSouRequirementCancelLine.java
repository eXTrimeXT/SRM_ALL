package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划 - 计划取消明细
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_cancel_line")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementCancelLine extends BaseEntity<ExtPrSouRequirementCancelLine> {

    @TableId("REQUIREMENT_CANCEL_LINE_ID")
    @ApiModelProperty("ID")
    private Long requirementCancelLineId;

    /** @see ExtPrSouRequirementCancel#getRequirementCancelId */
    @TableField("REQUIREMENT_CANCEL_ID")
    @ApiModelProperty("计划取消ID")
    private Long requirementCancelId;

    /** @see ExtPrSouRequirementHead#getRequirementHeadId */
    @TableField("REQUIREMENT_HEAD_ID")
    @ApiModelProperty("招标计划ID")
    private Long requirementHeadId;

    /** @see ExtPrSouRequirementHead#getOrgBuId */
    @TableField("ORG_BU_ID")
    @ApiModelProperty("所属板块ID")
    private Long orgBuId;

    /** @see ExtPrSouRequirementHead#getOrgBuCode */
    @TableField("ORG_BU_CODE")
    @ApiModelProperty("所属板块编码")
    private String orgBuCode;

    /** @see ExtPrSouRequirementHead#getOrgBuName */
    @TableField("ORG_BU_NAME")
    @ApiModelProperty("所属板块名称")
    private String orgBuName;

    /** @see RequirementHead#getOrgId */
    @ApiModelProperty("业务实体ID")
    @TableField("ORG_ID")
    private Long orgId;

    /** @see RequirementHead#getOrgCode */
    @ApiModelProperty("业务实体编码")
    @TableField("ORG_CODE")
    private String orgCode;

    /** @see RequirementHead#getOrgName */
    @ApiModelProperty("业务实体名称")
    @TableField("ORG_NAME")
    private String orgName;

    /** @see RequirementHead#getCeeaDepartmentId */
    @ApiModelProperty("申请部门ID")
    @TableField("CEEA_DEPARTMENT_ID")
    private String ceeaDepartmentId;

    /** @see RequirementHead#getCeeaDepartmentName */
    @ApiModelProperty("申请部门名称")
    @TableField("CEEA_DEPARTMENT_NAME")
    private String ceeaDepartmentName;

    /** @see RequirementHead#getCategoryId */
    @TableField("CATEGORY_ID")
    @ApiModelProperty("所属品类ID")
    private Long categoryId;

    /** @see RequirementHead#getCategoryId */
    @TableField("CATEGORY_CODE")
    @ApiModelProperty("所属品类编码")
    private String categoryCode;

    /** @see RequirementHead#getCategoryName */
    @TableField("CATEGORY_NAME")
    @ApiModelProperty("所属品类名称")
    private String categoryName;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

    @TableField("PROJECT_NAME")
    @ApiModelProperty("项目名称")
    private String projectName;

}
