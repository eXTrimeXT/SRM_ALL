package com.midea.cloud.srm.model.pj.ccapiinvoices.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/08/ $
 * @Description:
 */
@ApiModel("发票开具创建-结算行-结算明细列表")
@Data
public class CcApiInvoiceCreateItemsSettleDetailDto {

    @ApiModelProperty("服务编码，不同结算明细不能重复")
    private String serviceCode;
    @ApiModelProperty("税收分类编码")
    private String taxClassifyCode;
    @ApiModelProperty("发票信息")
    private CcApiInvoiceCreateItemsSettleDetailInvoiceInfoDto invoiceInfo;
    @ApiModelProperty("结算信息列表")
    private List<CcApiInvoiceCreateItemsSettleDetailSettleInfoDto> settleInfoList;
}
