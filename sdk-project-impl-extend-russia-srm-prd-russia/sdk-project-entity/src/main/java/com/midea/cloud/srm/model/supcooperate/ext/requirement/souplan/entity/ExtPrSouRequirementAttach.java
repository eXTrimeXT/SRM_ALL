package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementFileTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * (非材) 招标计划附件表
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_attach")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementAttach extends BaseEntity<ExtPrSouRequirementAttach> {

    @TableId("REQUIREMENT_ATTACH_ID")
    @ApiModelProperty("主键ID")
    private Long requirementAttachId;

    /** @see PrRequirementHead#getRequirementHeadId */
    @ApiModelProperty("招标计划ID")
    @TableField("REQUIREMENT_HEAD_ID")
    private Long requirementHeadId;

    /** @see PrSouRequirementFileTypeEnum */
    @TableField("FILE_TYPE")
    @ApiModelProperty("文件类型")
    private String fileType;

    @TableField("FILE_ID")
    @ApiModelProperty("文件ID")
    private Long fileId;

    @TableField("FILE_NAME")
    @ApiModelProperty("文件名称")
    private String fileName;

    @TableField("UPDATE_DATE")
    @ApiModelProperty("上传时间")
    private LocalDate updateDate;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
