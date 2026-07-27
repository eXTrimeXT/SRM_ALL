package com.midea.cloud.srm.model.pj.ccapiinvoices.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: panmq
 * @Date: 2024/04/08/ $
 * @Description:
 */
@ApiModel("发票开具创建-结算行-合同信息")
@Data
public class CcApiInvoiceCreateItemsContractInfoDto {

    @ApiModelProperty("合同号")
    private String contractNo;
    @ApiModelProperty("合同名称")
    private String contractName;
    @ApiModelProperty("合同金额")
    private BigDecimal contractAmount;
    @ApiModelProperty("付款方式（01-电汇，02-银行承兑，03-信用证，04-现金，05-抵货款，06-POS机刷卡）")
    private String payType;
    @ApiModelProperty("收款银行")
    private String collectionBank;
    @ApiModelProperty("收款账号，包含资金收款业务必填")
    private String collectionAccount;
    @ApiModelProperty("付款银行")
    private String payBank;
    @ApiModelProperty("付款账号")
    private String payAccount;
    @ApiModelProperty("是否含税（true-含税，false-不含税）")
    private Boolean containTax;
}
