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
@ApiModel("发票开具创建-请求")
@Data
public class CcApiInvoiceCreateRequestDto {

    @ApiModelProperty("请求头")
    private CcApiInvoiceCreateHeaderDto header;

    @ApiModelProperty("结算行")
    private List<CcApiInvoiceCreateItemsDto> items;

}
