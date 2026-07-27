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
@ApiModel("发票开具创建-结算行")
@Data
public class CcApiInvoiceCreateItemsDto {

    @ApiModelProperty("基本信息")
    private CcApiInvoiceCreateItemsBaseInfoDto baseInfo;

    @ApiModelProperty("合同信息")
    private CcApiInvoiceCreateItemsContractInfoDto contractInfo;

    @ApiModelProperty("结算明细列表")
    private List<CcApiInvoiceCreateItemsSettleDetailDto> settleDetailList;

    @ApiModelProperty("成本结转列表")
    private List<CcApiInvoiceCreateItemsCostInfoDto> costInfoList;

    @ApiModelProperty("收款信息列表")
    private List<CcApiInvoiceCreateItemsCollectionInfoDto> collectionInfoList;

    @ApiModelProperty("附件列表")
    private List<CcApiInvoiceCreateItemsAttachDto> attachList;
}
