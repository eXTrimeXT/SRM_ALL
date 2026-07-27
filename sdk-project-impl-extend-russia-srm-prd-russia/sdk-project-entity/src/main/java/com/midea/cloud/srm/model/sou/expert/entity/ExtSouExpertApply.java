package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.enums.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;

/**
 * 寻源 - 专家申请
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@TableName("scc_npm_sou_expert_apply")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertApply extends BaseEntity<ExtSouExpertApply> {

    public static final String ATTACH_FILE_SCENE_TYPE = "SCENE_EXT_SOU_EXPERT";

    @TableId("EXPERT_APPLY_ID")
    @ApiModelProperty("ID")
    private Long expertApplyId;

    @TableField("EXPERT_APPLY_NO")
    @ApiModelProperty("专家申请编号")
    private String expertApplyNo;

    /** @see ExtSouExpertApplyFromTypeEnum */
    @TableField("APPLY_FROM_TYPE")
    @ApiModelProperty("数据来源")
    private String applyFromType;

    @TableField("APPLY_BY_ID")
    @ApiModelProperty("申请人ID")
    private Long applyById;

    @TableField("APPLY_BY")
    @ApiModelProperty("申请人账号")
    private String applyBy;

    @TableField("APPLY_BY_CODE")
    @ApiModelProperty("申请人工号")
    private String applyByCode;

    @TableField("APPLY_BY_NICKNAME")
    @ApiModelProperty("申请人昵称")
    private String applyByNickname;

    @TableField("APPLY_TIME")
    @ApiModelProperty("申请时间")
    private Date applyTime;

    /** @see ExtSouExpertApplyStatusEnum */
    @TableField("APPLY_STATUS")
    @ApiModelProperty("申请状态")
    private String applyStatus;

    @TableField("HAS_SUBMIT")
    @ApiModelProperty("是否已提交")
    private Enable hasSubmit;

    /** @see ExtSouExpertEduEnum */
    @TableField("HIGHEST_DEGREE")
    @ApiModelProperty("最高学历")
    private String highestDegree;

    /** @see ExtSouExpertSexEnum */
    @TableField("SEX")
    @ApiModelProperty("性别")
    private String sex;

    @TableField("ORG_OU_ID")
    @ApiModelProperty("所属公司ID")
    private Long orgOuId;

    @TableField("ORG_OU_CODE")
    @ApiModelProperty("所属公司编码")
    private String orgOuCode;

    @TableField("ORG_OU_NAME")
    @ApiModelProperty("所属公司名称")
    private String orgOuName;

    @TableField("DEPARTMENT_ID")
    @ApiModelProperty("部门ID")
    private String departmentId;

    @TableField("DEPARTMENT_NAME")
    @ApiModelProperty("部门名称")
    private String departmentName;

    @TableField("JOB")
    @ApiModelProperty("职务")
    private String job;

    @TableField("JOB_RANK")
    @ApiModelProperty("职务职级(例如 P0/P1)")
    private String jobRank;

    /** @see ExtSouExpertLevelEnum */
    @TableField("EXPERT_LEVEL")
    @ApiModelProperty("专家等级")
    private String expertLevel;

    /** @see ExtSouExpertJobStatusEnum */
    @TableField("JOB_STATUS")
    @ApiModelProperty("在职状态")
    private String jobStatus;

    @TableField("PHONE")
    @ApiModelProperty("手机号码")
    private String phone;

    @TableField("HIRE_DATE")
    @ApiModelProperty("入职时间")
    private LocalDate hireDate;

    /** @see ExtSouExpertLevelEnum */
    @TableField("APPLY_LEVEL")
    @ApiModelProperty("申请等级")
    private String applyLevel;

    /** @see #expertApplyId */
    @TableField("FROM_APPLY_ID")
    @ApiModelProperty("用于专家升级时记录来源申请单号")
    private Long fromApplyId;

    @TableField("GREEN_REASON")
    @ApiModelProperty("绿色通道原因")
    private String greenReason;

    @TableField("UPGRADE_REASON")
    @ApiModelProperty("升级申请原因")
    private String upgradeReason;

    @TableField("BU_ID")
    @ApiModelProperty("板块id")
    private Long buId;

    @TableField("BU_CODE")
    @ApiModelProperty("板块编码")
    private String buCode;

    @TableField("BU_NAME")
    @ApiModelProperty("板块名称")
    private String buName;

    @TableField("IF_GREEN_PERSON_UPDATE")
    @ApiModelProperty("绿色通道个人更新状态(Y-已更新/N-未更新)")
    private Enable ifGreenPersonUpdate;

}
