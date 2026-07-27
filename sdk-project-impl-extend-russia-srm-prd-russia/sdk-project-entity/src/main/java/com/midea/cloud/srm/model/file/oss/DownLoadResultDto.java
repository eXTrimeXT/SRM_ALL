package com.midea.cloud.srm.model.file.oss;

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
@ApiModel("获取下载签名")
public class DownLoadResultDto {
    @ApiModelProperty("下载签名")
    private String sign;
    @ApiModelProperty("是否使用签名下载")
    private String isTrue;
    @ApiModelProperty("全路径名称")
    private String fileFullname;
    @ApiModelProperty("全路径")
    private String fileUrl;

    @ApiModelProperty("文件ID")
    private Long fileuploadId;
}
