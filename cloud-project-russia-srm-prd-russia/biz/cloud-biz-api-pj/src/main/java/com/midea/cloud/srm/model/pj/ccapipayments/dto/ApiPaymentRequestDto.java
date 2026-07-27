package com.midea.cloud.srm.model.pj.ccapipayments.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款保存及自动提交接口-请求头
 */
@ApiModel("批量付款保存及自动提交接口-请求头")
@Data
public class ApiPaymentRequestDto {

    @ApiModelProperty("请求头")
    private ApiPaymentRequestHeadDto paymentRequestHead;

    @ApiModelProperty("请求行项目")
    private List<ApiPaymentRequestItemDto> batchImportPaymentRequestItems;
}
