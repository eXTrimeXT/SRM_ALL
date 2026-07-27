package com.midea.cloud.srm.model.pj.contract.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 * 调长城开放平台创建已签署合同接口返回结果
 */
@Data
public class CreateContractReturnDTO {

    @ApiModelProperty("处理信息")
    private String path;

    @ApiModelProperty("处理信息")
    private String msg;

    @ApiModelProperty("响应码")
    private String code;

    @ApiModelProperty("处理信息")
    private String err_msg;

}
