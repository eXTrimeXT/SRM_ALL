package com.midea.cloud.srm.model.pj.ccapiinvoices.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/08/ $
 * @Description:
 */
@ApiModel("发票开具创建-结算行-收款信息列表")
@Data
public class SccPjApiInvoiceCollIntfDto extends BaseDTO {
    @ApiModelProperty("主键")
    private Long collectionIntfId;
    @ApiModelProperty("外键")
    private Long invoiceIntfId;
    @ApiModelProperty("服务编码")
    private String serviceCode;
    @ApiModelProperty("收款行行号")
    private String collectionItemNo;
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
