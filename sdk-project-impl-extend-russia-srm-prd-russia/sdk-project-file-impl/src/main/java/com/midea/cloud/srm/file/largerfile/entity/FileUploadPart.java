package com.midea.cloud.srm.file.largerfile.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <pre>
 *  大文件上传分片表 模型
 * </pre>
 *
 * @author fubiao
 * @version 1.00.00
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_file_fileupload_part")
@ApiModel(description = "分片")
public class FileUploadPart extends BaseEntity <FileUploadPart>{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("FILEUPLOAD_PART_ID")
    private Long fileuploadPartId;

    @ApiModelProperty(value = "文件指纹")
    @TableField("FINGERPRINT")
    private String fingerprint;

    @ApiModelProperty(value = "上传介质类型")
    @TableField("UPLOAD_TYPE")
    private String uploadType;

    @ApiModelProperty(value = "业务ID，用于业务单据关联")
    @TableField("BUSINESS_ID")
    private String businessId;

    @ApiModelProperty(value = "文件所属模块")
    @TableField("FILE_MODULAR")
    private String fileModular;

    @ApiModelProperty(value = "文件所属功能")
    @TableField("FILE_FUNCTION")
    private String fileFunction;

    @ApiModelProperty(value = "文件所属类型")
    @TableField("FILE_TYPE")
    private String fileType;

    @ApiModelProperty(value = "文件全路径名")
    @TableField("FILE_FULLNAME")
    private String fileFullname;

    @ApiModelProperty(value = "文件扩展名")
    @TableField("FILE_EXTEND_TYPE")
    private String fileExtendType;

    @ApiModelProperty(value = "原始文件名")
    @TableField("FILE_SOURCE_NAME")
    private String fileSourceName;

    @ApiModelProperty(value = "文件大小")
    @TableField("FILE_SIZE")
    private Long fileSize;

    @ApiModelProperty(value = "文件路径")
    @TableField("FILE_PATH")
    private String filePath;

    @ApiModelProperty(value = "纯文件名")
    @TableField("FILE_PURE_NAME")
    private String filePureName;

    @ApiModelProperty(value = "来源类型")
    @TableField("SOURCE_TYPE")
    private String sourceType;

    @ApiModelProperty(value = "临时文件过期时间")
    @TableField("EXPIRE_TIME")
    private Date expireTime;

    @ApiModelProperty(value = "场景附件模板原始名称")
    @TableField("SCENE_FILE_SOURCE_NAME")
    private String sceneFileSourceName;

    @ApiModelProperty(value = "场景附件模板文件ID")
    @TableField("SCENE_FILEUPLOAD_ID")
    private String sceneFileuploadId;

    @ApiModelProperty(value = "场景附件管理ID")
    @TableField("SCENE_ATTACHMENT_ID")
    private String sceneAttachmentId;

    @ApiModelProperty(value = "备注")
    @TableField("COMMENT")
    private String comment;

    @ApiModelProperty(value = "分片大小，单位byte")
    @TableField("CHUNK_SIZE")
    private Long chunkSize;

    @ApiModelProperty(value = "最后分片大小，单位byte")
    @TableField("LAST_CHUNK_SIZE")
    private Long lastChunkSize;

    @ApiModelProperty(value = "分片数量")
    @TableField("CHUNK_AMOUNT")
    private Long chunkAmount;

    @ApiModelProperty(value = "分片序号")
    @TableField("CHUNK_NUM")
    private Long chunkNum;

    @ApiModelProperty(value = "上传状态")
    @TableField("UPLOAD_STATUS")
    private String uploadStatus;

    @ApiModelProperty(value = "阿里组装分片用ID")
    @TableField("UPLOAD_ID")
    private String uploadId;

}