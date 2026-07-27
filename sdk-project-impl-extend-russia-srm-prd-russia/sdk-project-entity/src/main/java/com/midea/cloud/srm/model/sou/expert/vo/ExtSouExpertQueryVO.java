package com.midea.cloud.srm.model.sou.expert.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertEducation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;

/**
 * 专家库 - 列表查询结果
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertQueryVO extends ExtSouExpert {

    /** @see ExtSouExpertApply#getHighestDegree  */
    @ApiModelProperty("最高学历")
    private String highestDegree;

    /** @see ExtSouExpertEducation#getStudyDateTo */
    @ApiModelProperty("毕业时间")
    private LocalDate studyDateTo;

    /** @see ExtSouExpertApply#getSex */
    @ApiModelProperty("性别")
    private String sex;

    /** @see ExtSouExpertApply#getOrgOuName */
    @ApiModelProperty("所属单位名称")
    private String orgOuName;

    /** @see ExtSouExpertApply#getDepartmentName */
    @ApiModelProperty("部门/科室")
    private String departmentName;

    /** @see ExtSouExpertApply#getJob */
    @ApiModelProperty("职务")
    private String job;

    /** @see ExtSouExpertApply#getJobRank */
    @ApiModelProperty("职务等级(序列等级)")
    private String jobRank;

    /** @see ExtSouExpertApply#getExpertLevel */
    @ApiModelProperty("序列等级")
    private String expertLevel;

    /** @see ExtSouExpertApply#getJobStatus */
    @ApiModelProperty("在职状态")
    private String jobStatus;

    /** @see ExtSouExpertApply#getPhone */
    @ApiModelProperty("手机号码")
    private String phone;

    /** @see ExtSouExpertApply#getHireDate */
    @ApiModelProperty("入厂时间")
    private LocalDate hireDate;

    /** @see ExtSouExpertEducation#getStudyCollege */
    @ApiModelProperty("毕业院校")
    private String studyCollege;

    /** @see ExtSouExpertEducation#getMajor */
    @ApiModelProperty("所学专业")
    private String major;

    /** @see ExtSouExpertApply#getApplyLevel */
    @ApiModelProperty("申报等级")
    private String applyLevel;

    /** @see ExtSouExpertApply#getGreenReason */
    @ApiModelProperty("绿色通道")
    private String greenReason;

    /** @see ExtSouExpertApply#getApplyFromType */
    @ApiModelProperty("数据来源")
    private String applyFromType;

    /** @see ExtSouExpertApply#getApplyTime */
    @ApiModelProperty("申请日期")
    private Date applyTime;

    /** @see ExtSouExpertApply#getExpertApplyNo */
    @ApiModelProperty("申请单号")
    private String expertApplyNo;

    /** @see ExtSouExpert#getHasFrozen */
    @ApiModelProperty("是否冻结")
    private Enable hasFrozen;

    /** @see ExtSouExpert#getFrozenStatus */
    @ApiModelProperty("冻结状态")
    private String frozenStatus;

    /** @see ExtSouExpert#getFrozenReason */
    @ApiModelProperty("冻结/解冻原因")
    private String frozenReason;

    /** @see ExtSouExpert#getFrozenRejectReason */
    @ApiModelProperty("拒绝说明")
    private String frozenRejectReason;

    @ApiModelProperty("板块id")
    private Long buId;

    @ApiModelProperty("板块编码")
    private String buCode;

    @ApiModelProperty("板块名称")
    private String buName;

}
