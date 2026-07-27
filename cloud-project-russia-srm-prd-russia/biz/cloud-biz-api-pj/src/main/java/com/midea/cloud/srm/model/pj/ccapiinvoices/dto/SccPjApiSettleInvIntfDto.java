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
@ApiModel("发票开具创建-结算行-结算明细列表-发票信息")
@Data
public class SccPjApiSettleInvIntfDto extends BaseDTO {
    @ApiModelProperty("主键")
    private Long settleInvIntfId;
    @ApiModelProperty("推送头外键")
    private Long invoiceIntfId;
    @ApiModelProperty("推送结算信息外键")
    private Long settleIntfId;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("发票备注")
    private String invoiceRemarks;
    @ApiModelProperty("原发票代码")
    private String oldInvoiceCode;
    @ApiModelProperty("原发票号码")
    private String oldInvoiceNo;
    @ApiModelProperty("红字信息表")
    private String redLetterTab;
    @ApiModelProperty("红冲原因")
    private String reverseReason;

}
