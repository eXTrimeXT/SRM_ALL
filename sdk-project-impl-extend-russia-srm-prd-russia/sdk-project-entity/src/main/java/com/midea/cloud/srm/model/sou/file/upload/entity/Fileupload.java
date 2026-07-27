package com.midea.cloud.srm.model.sou.file.upload.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre>
 *  文件详情记录 模型
 * </pre>
 *
 * @author huanghb14@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-03-04 13:39:58
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_file_fileupload")
@ApiModel(description = "文件详情记录")
public class Fileupload extends BaseEntity {

    private static final long serialVersionUID = 6390591027354729861L;

    @ApiModelProperty(value = "ID")
    @TableId("FILEUPLOAD_ID")
    @NotNull(message = "文件ID不能为空", groups = {Default.class})
    private Long fileuploadId;

    @ApiModelProperty(value = "文件指纹")
    @TableField("FINGERPRINT")
    private String fingerprint;

    @ApiModelProperty(value = "上传介质类型")
    @TableField("UPLOAD_TYPE")
    private String uploadType;

    @ApiModelProperty(value = "业务ID，用于业务单据关联")
    @TableField("BUSINESS_ID")
    private Long businessId;

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
    private BigDecimal fileSize;

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
    private Long sceneFileUploadId;

    @ApiModelProperty(value = "场景附件管理ID")
    @TableField("SCENE_ATTACHMENT_ID")
    private Long sceneAttachmentId;

    @ApiModelProperty(value = "备注")
    @TableField("COMMENT")
    private String comment;


    @ApiModelProperty(value = "文件base64序列")
    @TableField(exist = false)
    private String base64;

    @ApiModelProperty(value = "文件标识")
    @TableField(exist = false)
    private String fileKey;

    @TableField(exist = false)
    private Date creationDateBegin;

    @TableField(exist = false)
    private Date creationDateEnd;

    @ApiModelProperty(value = "存储oa系统的文件路径")
    @TableField("OA_FILE_PATH")
    private String oaFilePath;

    @ApiModelProperty(value = "第三方附件上传时的文件指纹")
    @TableField("THIRD_FINGERPRINT")
    private String thirdFingerprint;

    @ApiModelProperty(value = "第三方附件信息")
    @TableField("THIRD_FILE_INFO")
    private String thirdFileInfo;


}
