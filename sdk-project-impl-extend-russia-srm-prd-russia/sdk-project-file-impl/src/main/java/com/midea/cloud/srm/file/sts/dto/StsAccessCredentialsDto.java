package com.midea.cloud.srm.file.sts.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 备注
 * @author FuBiao
 */
@Data
@ApiModel("STS临时认证DTO")
public class StsAccessCredentialsDto {

    @ApiModelProperty("用户标识")
    private String accessKeyId;
    @ApiModelProperty("秘钥")
    private String accessKeySecret;
    @ApiModelProperty("用户票据")
    private String securityToken;
    @ApiModelProperty("有效期")
    private String expiration;
}

