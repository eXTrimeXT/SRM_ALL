package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertEducation;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertWork;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 专家库 - 初始化导入信息
 *
 * @author zhangwk12@meiclolud.com
 * @since 2023-11-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertInitImportExcelDTO extends BaseObjectX {

    /** @see ExtSouExpertApply#getOrgOuName */
    @ApiModelProperty("公司名称")
    private String orgOuName;

    /** @see ExtSouExpertApply#getDepartmentName */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /** @see ExtSouExpertApply#getApplyByNickname */
    @ApiModelProperty("申请人昵称")
    private String applyByNickname;

    /** @see ExtSouExpertApply#getApplyByCode */
    @ApiModelProperty("申请人工号")
    private String applyByCode;

    /** @see ExtSouExpertEducation#getEducation */
    @ApiModelProperty("学历")
    private String education;

    /** @see ExtSouExpertEducation#getStudyDateTo */
    @ApiModelProperty("毕业时间")
    private LocalDate studyDateTo;

    /** @see ExtSouExpertApply#getSex */
    @ApiModelProperty("性别")
    private String sex;

    /** @see ExtSouExpertApply#getJob */
    @ApiModelProperty("职务")
    private String job;

    /** @see ExtSouExpertApply#getJobRank */
    @ApiModelProperty("序列等级")
    private String jobRank;

    /** @see ExtSouExpert#getJobStatus */
    @ApiModelProperty("在职状态")
    private String jobStatus;

    /** @see ExtSouExpertApply#getPhone */
    @ApiModelProperty("手机号码")
    private String phone;

    /** @see ExtSouExpertWork#getEntryDate */
    @ApiModelProperty("入厂时间")
    private LocalDate entryDate;

    /** @see ExtSouExpertEducation#getStudyCollege */
    @ApiModelProperty("毕业院校")
    private String studyCollege;

    /** @see ExtSouExpertEducation#getMajor */
    @ApiModelProperty("所学专业")
    private String major;

    /** @see ExtSouExpertApply#getExpertLevel */
    @ApiModelProperty("申报等级")
    private String expertLevel;

}
