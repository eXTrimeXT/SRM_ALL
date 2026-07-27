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
@ApiModel("发票开具创建-结算行-成本结转列表")
@Data
public class CcApiInvoiceCreateItemsCostInfoDto {

    @ApiModelProperty("服务编码")
    private String serviceCode;
    @ApiModelProperty("结转费用分类编码")
    private String coExpenseClassifyCode;
    @ApiModelProperty("结转金额")
    private BigDecimal costAmount;
    @ApiModelProperty("成本中心编码")
    private String costCenterCode;
    @ApiModelProperty("成本中心名称")
    private String costCenterName;
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("订单名称")
    private String orderName;
    @ApiModelProperty("研发类型")
    private String devType;
    @ApiModelProperty("车型编码")
    private String carCode;
    @ApiModelProperty("投放形式编码")
    private String layCode;
    @ApiModelProperty("备注")
    private String remarks;
}
