package com.midea.cloud.srm.file.largerfile.dto;

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
@ApiModel("合并分支添加file主表")
public class MergeChunkDto {
    @ApiModelProperty("阿里组装分片用ID")
    private String uploadId;
    @ApiModelProperty("文件全路径")
    private String fileFullname;
    @ApiModelProperty("MD5值")
    private String fingerprint;

}
