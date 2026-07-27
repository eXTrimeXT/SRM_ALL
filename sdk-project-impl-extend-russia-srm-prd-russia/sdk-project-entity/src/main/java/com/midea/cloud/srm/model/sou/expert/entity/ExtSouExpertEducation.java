package com.midea.cloud.srm.model.sou.expert.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertEduEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 寻源 - 专家申请 - 学历
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@TableName("scc_npm_sou_expert_apply_edu")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertEducation extends BaseEntity<ExtSouExpertEducation> {

    @TableId("EXPERT_EDUCATION_ID")
    @ApiModelProperty("ID")
    private Long expertEducationId;

    /** @see ExtSouExpertApply#getExpertApplyId */
    @TableField("EXPERT_ID")
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    /** @see ExtSouExpertEduEnum */
    @TableField("EDUCATION")
    @ApiModelProperty("学历")
    private String education;

    @TableField("STUDY_COLLEGE")
    @ApiModelProperty("就读院校")
    private String studyCollege;

    @TableField("FULL_TIME_STUDY")
    @ApiModelProperty("是否全日制(Y/N)")
    private Enable fullTimeStudy;

    @TableField("STUDY_DATE_FROM")
    @ApiModelProperty("就读时间从")
    private LocalDate studyDateFrom;

    @TableField("STUDY_DATE_TO")
    @ApiModelProperty("就读时间到")
    private LocalDate studyDateTo;

    @TableField("MAJOR")
    @ApiModelProperty("主修专业")
    private String major;

    @ApiModelProperty("学位证书-文件ID")
    @TableField("DEGREE_CERTIFY_DOC_ID")
    private Long degreeCertifyDocId;

    @ApiModelProperty("学位证书-文件名称")
    @TableField("DEGREE_CERTIFY_FILE_NAME")
    private String degreeCertifyFileName;

    @ApiModelProperty("毕业证书-文件ID")
    @TableField("GRADUATION_CERTIFY_DOC_ID")
    private Long graduationCertifyDocId;

    @ApiModelProperty("毕业证书-文件名称")
    @TableField("GRADUATION_CERTIFY_FILE_NAME")
    private String graduationCertifyFileName;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
