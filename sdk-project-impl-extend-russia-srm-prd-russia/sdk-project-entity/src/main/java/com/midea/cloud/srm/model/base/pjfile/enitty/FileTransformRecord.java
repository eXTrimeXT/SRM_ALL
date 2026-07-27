package com.midea.cloud.srm.model.base.pjfile.enitty;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author luxc18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_base_file_transform_record")
public class FileTransformRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
    @TableId("RECORD_ID")
    private Long recordId;

    @ApiModelProperty(value = "供应商信用代码")
    @TableField("LC_CODE")
    private String lcCode;

    @ApiModelProperty(value = "文件类型(业务含义)")
    @TableField("BUSINESS_TYPE")
    private String businessType;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_DATE")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_DATE")
    private LocalDate endDate;

    @ApiModelProperty(value = "外围系统文件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty(value = "外围系统文件类型")
    @TableField("FILE_TYPE")
    private String fileType;

    @ApiModelProperty(value = "外围系统文件url")
    @TableField("FILE_URL")
    private String fileUrl;

    @ApiModelProperty(value = "srm文件id")
    @TableField("SRM_FILE_UPLOAD_ID")
    private Long srmFileUploadId;

    @ApiModelProperty(value = "状态")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "扩展字段1")
    @TableField("EXT_ATTRIBUTE1")
    private String extAttribute1;

    @ApiModelProperty(value = "扩展字段2")
    @TableField("EXT_ATTRIBUTE2")
    private String extAttribute2;

    @ApiModelProperty(value = "扩展字段3")
    @TableField("EXT_ATTRIBUTE3")
    private String extAttribute3;

    @ApiModelProperty(value = "扩展字段4")
    @TableField("EXT_ATTRIBUTE4")
    private String extAttribute4;

    @ApiModelProperty(value = "扩展字段5")
    @TableField("EXT_ATTRIBUTE5")
    private String extAttribute5;

    @ApiModelProperty(value = "扩展字段6")
    @TableField("EXT_ATTRIBUTE6")
    private String extAttribute6;

    @ApiModelProperty(value = "扩展字段7")
    @TableField("EXT_ATTRIBUTE7")
    private String extAttribute7;

    @ApiModelProperty(value = "扩展字段8")
    @TableField("EXT_ATTRIBUTE8")
    private String extAttribute8;

    @ApiModelProperty(value = "扩展字段9")
    @TableField("EXT_ATTRIBUTE9")
    private String extAttribute9;

    @ApiModelProperty(value = "扩展字段10")
    @TableField("EXT_ATTRIBUTE10")
    private String extAttribute10;
}