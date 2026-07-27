package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 招标计划 - 计划取消
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_cancel")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementCancel extends BaseEntity<ExtPrSouRequirementCancel> {

    @TableId("REQUIREMENT_CANCEL_ID")
    @ApiModelProperty("ID")
    private Long requirementCancelId;

    @TableField("REQUIREMENT_CANCEL_NO")
    @ApiModelProperty("计划取消编号")
    private String requirementCancelNo;

    /** @see PrSouRequirementCancelStatusEnum */
    @TableField("CANCEL_STATUS")
    @ApiModelProperty("取消状态")
    private String cancelStatus;

    @TableField("DEPARTMENT_ID")
    @ApiModelProperty("申请部门ID")
    private String departmentId;

    @TableField("DEPARTMENT_NAME")
    @ApiModelProperty("申请部门名称")
    private String departmentName;

    @TableField("APPLY_DATE")
    @ApiModelProperty("申请日期")
    private LocalDate applyDate;

    @TableField("APPLY_BY_ID")
    @ApiModelProperty("申请人ID")
    private Long applyById;

    @TableField("APPLY_BY")
    @ApiModelProperty("申请人账号")
    private String applyBy;

    @TableField("APPLY_BY_NICKNAME")
    @ApiModelProperty("申请人昵称")
    private String applyByNickname;

    @TableField("CANCEL_REASON")
    @ApiModelProperty("取消原因")
    private String cancelReason;

    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;
}
