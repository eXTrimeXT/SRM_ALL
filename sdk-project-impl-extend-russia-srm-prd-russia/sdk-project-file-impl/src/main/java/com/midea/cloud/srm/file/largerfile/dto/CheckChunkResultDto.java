package com.midea.cloud.srm.file.largerfile.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 备注
 * @author FuBiao
 */
@Data
@Slf4j
@ApiModel("后端返回成功分片列表")
public class CheckChunkResultDto {

    @ApiModelProperty("MD5值")
    private String fingerprint;
    @ApiModelProperty("上传介质类型")
    private String uploadType;
    @ApiModelProperty("来源类型")
    private String sourceType;
    @ApiModelProperty("文件所属模块")
    private String fileModular;
    @ApiModelProperty("文件所属功能")
    private String fileFunction;
    @ApiModelProperty("文件所属类型")
    private String fileType;
    @ApiModelProperty("源文件名")
    private String fileSourceName;
    @ApiModelProperty("原始文件大小")
    private Long fileSize;
    @ApiModelProperty("分片大小，单位byte")
    private Long chunkSize;
    @ApiModelProperty("最后分片大小，单位byte")
    private Long lastChunkSize;
    @ApiModelProperty("分片数量")
    private Long chunkAmount;
    @ApiModelProperty("分片序号")
    private Long chunkNum;
    @ApiModelProperty("阿里组装分片用ID")
    private String uploadId;

    @ApiModelProperty(value = "文件全路径名")
    private String fileFullname;
























}
