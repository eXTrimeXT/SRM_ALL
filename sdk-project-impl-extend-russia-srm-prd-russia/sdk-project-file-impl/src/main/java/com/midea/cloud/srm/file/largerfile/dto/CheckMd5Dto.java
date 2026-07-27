package com.midea.cloud.srm.file.largerfile.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 备注
 * @author FuBiao
 */
@Data
@Slf4j
@ApiModel("根据MD5验证是否秒传")
public class CheckMd5Dto {

    @ApiModelProperty("MD5标识")
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
    @ApiModelProperty("原始文件名")
    private String fileSourceName;
    @ApiModelProperty("阿里组装分片用ID")
    private String uploadId;
    @ApiModelProperty("UUID名称")
    private String fileFullname;
    @ApiModelProperty("附件大小")
    private BigDecimal fileSize;
}
