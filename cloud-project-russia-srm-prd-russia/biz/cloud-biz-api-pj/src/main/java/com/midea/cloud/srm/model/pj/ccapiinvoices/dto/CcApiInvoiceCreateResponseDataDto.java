package com.midea.cloud.srm.model.pj.ccapiinvoices.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/04/08/ $
 * @Description:
 */
@ApiModel("发票开具创建-响应")
@Data
public class CcApiInvoiceCreateResponseDataDto {

    @ApiModelProperty("系统编码")
    private String systemCode;

    @ApiModelProperty("业务单据号")
    private String businessNo;

    @ApiModelProperty("请求流水号")
    private String reqSn;

    @ApiModelProperty("结算单据编码")
    private String settleDocumentCode;

}
