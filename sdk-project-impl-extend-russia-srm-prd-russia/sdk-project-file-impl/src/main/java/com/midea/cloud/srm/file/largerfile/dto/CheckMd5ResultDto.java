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
@ApiModel("根据MD5验证是否秒传结果返回")
public class CheckMd5ResultDto {

    @ApiModelProperty("是否秒传（0否1是）")
    private String isUpfile;
    @ApiModelProperty("文件表ID(秒传时返回)")
    private String fileUploadId;
    @ApiModelProperty("阿里组装分片ID（不秒传时返回）")
    private String uploadId;
    @ApiModelProperty("uuid文件名称")
    private String fileFullname;
}
