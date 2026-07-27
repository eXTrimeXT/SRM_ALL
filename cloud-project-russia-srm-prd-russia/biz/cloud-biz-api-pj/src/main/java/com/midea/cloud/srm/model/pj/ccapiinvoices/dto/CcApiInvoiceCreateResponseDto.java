package com.midea.cloud.srm.model.pj.ccapiinvoices.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/08/ $
 * @Description:
 */
@ApiModel("发票开具创建-响应")
@Data
public class CcApiInvoiceCreateResponseDto {

    @ApiModelProperty("响应编码，200-成功")
    private Integer code;

    @ApiModelProperty("响应信息")
    private String msg;

    @ApiModelProperty("响应数据")
    private CcApiInvoiceCreateResponseDataDto data;

}
