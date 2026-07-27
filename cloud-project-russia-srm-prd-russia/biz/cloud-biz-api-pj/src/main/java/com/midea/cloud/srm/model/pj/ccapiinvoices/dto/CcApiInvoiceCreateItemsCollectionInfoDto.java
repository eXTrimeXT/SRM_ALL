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
@ApiModel("发票开具创建-结算行-收款信息列表")
@Data
public class CcApiInvoiceCreateItemsCollectionInfoDto {

    @ApiModelProperty("服务编码")
    private String serviceCode;
    @ApiModelProperty("收款行行号")
    private Integer collectionItemNo;
    @ApiModelProperty("款项性质编码")
    private String paymentTypeCode;
    @ApiModelProperty("阶段名称")
    private String stageName;
    @ApiModelProperty("收款金额")
    private BigDecimal collectionAmount;
    @ApiModelProperty("收款日期")
    private String collectionDate;
    @ApiModelProperty("备注")
    private String remarks;
}
