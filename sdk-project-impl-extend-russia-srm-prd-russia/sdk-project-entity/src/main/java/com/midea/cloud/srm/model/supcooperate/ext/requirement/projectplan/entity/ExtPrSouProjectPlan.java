package com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.dept.entity.Dept;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * (非材) 招标计划-项目计划
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_project_plan")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouProjectPlan extends BaseEntity<ExtPrSouProjectPlan> {

    @TableId("PROJECT_PLAN_ID")
    @ApiModelProperty("ID")
    private Long projectPlanId;

    @TableField("PROJECT_NAME")
    @ApiModelProperty("项目名称")
    private String projectName;

    @TableField("SCENE_TYPE")
    @ApiModelProperty("应用场景")
    private String sceneType;

    @TableField("INIT_DATE")
    @ApiModelProperty("立项时间")
    private LocalDate initDate;

    /** @see Dept#getDeptid */
    @TableField("DEPARTMENT_ID")
    @ApiModelProperty("投资部门ID")
    private String departmentId;

    /** @see Dept#getDescr */
    @TableField("DEPARTMENT_NAME")
    @ApiModelProperty("投资部门名称")
    private String departmentName;

    @TableField("INIT_AMOUNT")
    @ApiModelProperty("立项金额")
    private BigDecimal initAmount;

    @TableField("PLAN_NO")
    @ApiModelProperty("计划编号")
    private String planNo;

    @TableField("PLAN_ADDRESS")
    @ApiModelProperty("投资地点")
    private String planAddress;

    @TableField("PLAN_LEVEL")
    @ApiModelProperty("项目级别")
    private String planLevel;

    /** @see ExtPrSouProjectPlanStatusEnum */
    @TableField("PLAN_STATUS")
    @ApiModelProperty("项目状态")
    private String planStatus;

    /** {@link ExtPrSouRequirementHead#getRequirementHeadId} */
    @TableField("REQUIREMENT_HEAD_ID")
    @ApiModelProperty("被引用的招标计划")
    private Long requirementHeadId;

}
