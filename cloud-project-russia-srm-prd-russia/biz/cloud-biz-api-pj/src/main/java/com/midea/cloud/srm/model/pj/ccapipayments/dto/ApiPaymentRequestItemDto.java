package com.midea.cloud.srm.model.pj.ccapipayments.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款保存及自动提交接口-请求行项目
 */
@ApiModel("批量付款保存及自动提交接口-请求行项目")
@Data
public class ApiPaymentRequestItemDto extends BaseDTO {

    @ApiModelProperty("序号 从1开始排序")
    private Integer xh;
    @ApiModelProperty("利润中心代码")
    private String profitCenterCode;
    @ApiModelProperty("利润中心名称")
    private String profitCenterName;
    @ApiModelProperty("成本中心代码")
    private String costCenterCode;
    @ApiModelProperty("成本中心名称")
    private String costCenterName;
    @ApiModelProperty("供应商代码")
    private String supplyCode;
    @ApiModelProperty("供应商")
    private String supplyName;
    @ApiModelProperty("付款性质名称")
    private String paymentNatureName;
    @ApiModelProperty("应付账款账面金额")
    private BigDecimal accountsPayableAmount;
    @ApiModelProperty("付款对象类型 员工:Personnel,客户:Customer,供应商:Supply,一次性供应商:OnceSupplier")
    private String customerSupplyEnum;
    @ApiModelProperty("收款方类型 客户-单位:CustomerUnit, 客户-个人:CustomerIndividual,供应商个人:SupplierIndividual,员工:Personnel, 个人:Individual, 一次性供应商-个人:OnceSupplierIndividual, 供应商-单位:SupplyUnit, 一次性供应商-单位:OnceSupplierUnit")
    private String receiveTypeStatus;
    @ApiModelProperty("付款日期 格式：yyyy-MM-dd HH:mm:ss 需传每月的10日、20日、22日-月末")
    private String paymentDate;
    @ApiModelProperty("付款方式 电汇:Telegraphic,商业承兑:Commercial,银行承兑:Bank,信用证:Credit,被动扣款:PassiveDeduction,信用证付款:CreditPayment,开保函付款:GuaranteePayment,电子债权凭证:ElectronicPaymentVoucher")
    private String paymentMethod;
    @ApiModelProperty("付款金额")
    private BigDecimal totalAmount;
    @ApiModelProperty("支付金额")
    private BigDecimal paymentMoney;
    @ApiModelProperty("收款银行名称")
    private String receivingBankName;
    @ApiModelProperty("收款银行联行号")
    private String bankLinkNumber;
    @ApiModelProperty("收款银行账号")
    private String receivingBankAccount;
    @ApiModelProperty("已到票金额")
    private BigDecimal arrivedTicketAmount;
    @ApiModelProperty("收款方代码")
    private String receiverCode;
    @ApiModelProperty("收款方名称")
    private String receiverName;
    @ApiModelProperty("支付说明")
    private String paymentNote;
    @ApiModelProperty("唯一标识（uuid生成）")
    private String onlyKey;

}
