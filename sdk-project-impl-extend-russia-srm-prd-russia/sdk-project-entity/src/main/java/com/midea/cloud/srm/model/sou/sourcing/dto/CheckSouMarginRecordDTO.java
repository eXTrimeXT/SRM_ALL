package com.midea.cloud.srm.model.sou.sourcing.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-08-08
 */
@SuppressWarnings("ALL")
@Data
public class CheckSouMarginRecordDTO {

    @ApiModelProperty("校验结果")
    private boolean result;
    @ApiModelProperty("错误信息")
    private String message;
}
