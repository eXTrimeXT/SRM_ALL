package com.midea.cloud.srm.model.pj.ccapipayments.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款保存及自动提交接口-响应数据
 */
@ApiModel("批量付款保存及自动提交接口-响应数据")
@Data
public class ApiPaymentResponseDto {

    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("是否成功")
    private String success;
    @ApiModelProperty("状态码")
    private String statusCode;
    @ApiModelProperty("消息")
    private String message;
    @ApiModelProperty("返回数据")
    private List<ApiPaymentResponseDataDto> data;

}
