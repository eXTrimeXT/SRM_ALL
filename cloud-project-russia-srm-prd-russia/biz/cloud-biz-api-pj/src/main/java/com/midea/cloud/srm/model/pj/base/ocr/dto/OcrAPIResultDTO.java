package com.midea.cloud.srm.model.pj.base.ocr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class OcrAPIResultDTO<T> {

    @ApiModelProperty("状态码")
    private String code;

    @ApiModelProperty("描述信息")
    private String message;

    @ApiModelProperty("请求是否成功")
    private String success;

    @ApiModelProperty("结果数据")
    private T result;
}
