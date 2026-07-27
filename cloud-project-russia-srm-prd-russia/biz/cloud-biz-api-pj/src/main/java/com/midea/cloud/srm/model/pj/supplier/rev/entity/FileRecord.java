package com.midea.cloud.srm.model.pj.supplier.rev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.supplierauth.review.entity.SiteReviewModelDim;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 *  准入附件记录 模型
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: Jan 25, 2021 11:15:34 PM
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_sup_file_record")
@ApiModel(description = "准入附件记录")
@QlMatchType("FileRecord")
public class FileRecord extends BaseEntity {
    private static final long serialVersionUID = 931910L;

    @ApiModelProperty(value = "ID")
    @TableId("FILE_RECORD_ID")
    private Long fileRecordId;

    @ApiModelProperty(value = "模板ID")
    @TableId("REVIEW_MODEL_ID")
    private Long reviewModelId;

    @ApiModelProperty(value = "模板名称")
    @TableField("REVIEW_MODEL_NAME")
    private String reviewModelName;


    @ApiModelProperty(value = "附件模板配置ID")
    @TableField("FILE_CONFIG_ID")
    private Long fileConfigId;

    @ApiModelProperty(value = "现场评审单ID")
    @TableField("FORM_ID")
    private Long formId;

    @ApiModelProperty(value = "单据类型")
    @TableField("FORM_TYPE")
    private String formType;

    @ApiModelProperty(value = "模板描述")
    @TableField("TEMPLATE_DESC")
    private String templateDesc;

    @ApiModelProperty(value = "附件模板ID")
    @TableField("TEMPLATE_FILE_ID")
    private Long templateFileId;

    @ApiModelProperty(value = "附件模板名称")
    @TableField("TEMPLATE_FILE_NAME")
    private String templateFileName;

    @ApiModelProperty(value = "文件上传ID")
    @TableField("FILE_ID")
    private Long fileId;

    @ApiModelProperty(value = "原始文件名")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty(value = "附件是否必填")
    @TableField("IF_REQUIRED")
    private String ifRequired;

    @ApiModelProperty(value = "附件有效期z止是否必填")
    @TableField("IF_VALID_DATE")
    private String ifValidDate;

    @ApiModelProperty(value = "文件有效期止")
    @TableField("FILE_VALID_DATE")
    private Date fileValidDate;

    @ApiModelProperty(value = "评审人员")
    @TableField("REVIEW_PEOPLE")
    private String reviewPeople;

    @ApiModelProperty(value = "评审人员-名称")
    @TableField("REVIEW_PEOPLE_NAME")
    private String reviewPeopleName;

    @ApiModelProperty(value = "供方陪审员")
    @TableField("VENDOR_ASSESSOR")
    private String vendorAssessor;

    @ApiModelProperty(value = "评审日期")
    @TableField("REVIEW_DATE")
    private Date reviewDate;

    @ApiModelProperty(value = "得分")
    @TableField("SCORE")
    private Double score;

    @ApiModelProperty(value = "认证结果")
    @TableField("AUTH_RESULT")
    private String authResult;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "生效日期(YYYY-MM-DD)")
    @TableField("START_DATE")
    private Date startDate;

    @ApiModelProperty(value = "失效日期(YYYY-MM-DD)")
    @TableField("END_DATE")
    private Date endDate;

    @ApiModelProperty(value = "是否启用提醒")
    @TableField("IS_USE_REMINDER")
    private String isUseReminder;

    @ApiModelProperty(value = "岗位")
    @TableField("USER_POST")
    private String userPost;

    @TableField(exist = false)
    private List<SiteReviewModelDim> dimList;

}