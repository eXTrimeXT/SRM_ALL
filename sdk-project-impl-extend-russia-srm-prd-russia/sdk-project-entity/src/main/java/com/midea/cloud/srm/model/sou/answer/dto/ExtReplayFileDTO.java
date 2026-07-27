package com.midea.cloud.srm.model.sou.answer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author GW00302625
 */
@Data
@ApiModel("澄清回复文件")
public class ExtReplayFileDTO extends ReplayFileDTO{
    @ApiModelProperty(value = "供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;
}
