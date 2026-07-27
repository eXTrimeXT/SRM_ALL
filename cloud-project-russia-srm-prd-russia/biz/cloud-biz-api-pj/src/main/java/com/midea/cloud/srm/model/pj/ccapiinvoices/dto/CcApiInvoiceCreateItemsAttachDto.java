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
@ApiModel("发票开具创建-结算行-附件列表")
@Data
public class CcApiInvoiceCreateItemsAttachDto {

    @ApiModelProperty("附件名称")
    private String attachName;
    @ApiModelProperty("附件链接")
    private String attachUrl;
}
