package com.midea.cloud.srm.model.pj.ccapipayments.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款及自动提交审批推送行-接口表实体类
 */
@ApiModel("批量付款及自动提交审批推送行-接口表实体类")
@Data
public class SccPjApiPaymentLineIntf extends BaseEntity {
    @ApiModelProperty("主键")
    private Long paymentLineIntfId;
    @ApiModelProperty("外键")
    private Long paymentIntfId;
    @ApiModelProperty("序号")
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
    @ApiModelProperty("付款对象类型")
    private String customerSupplyEnum;
    @ApiModelProperty("收款方类型")
    private String receiveTypeStatus;
    @ApiModelProperty("付款日期")
    private String paymentDate;
    @ApiModelProperty("付款方式")
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
    @ApiModelProperty("处理序号")
    private String processSerialNum;
    @ApiModelProperty("处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试")
    private String processStatus;
    @ApiModelProperty("处理信息")
    private String processMessage;
    @ApiModelProperty("处理时间")
    private Date processDate;
    @ApiModelProperty("处理批次号")
    private Long processGroupId;

}
