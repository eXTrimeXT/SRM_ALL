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
@ApiModel("发票开具创建-结算行-结算明细列表-结算信息列表")
@Data
public class SccPjApiSettleInfoIntfDto extends BaseDTO {
    @ApiModelProperty("主键")
    private Long settleInfoIntfId;
    @ApiModelProperty("推送头外键")
    private Long invoiceIntfId;
    @ApiModelProperty("推送结算信息外键")
    private Long settleIntfId;
    @ApiModelProperty("业务编码")
    private String businessCode;
    @ApiModelProperty("规格型号")
    private String specsModel;
    @ApiModelProperty("结算数量")
    private BigDecimal settleNumber;
    @ApiModelProperty("单价")
    private BigDecimal price;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("折扣标识（true-折扣，false-不折扣）")
    private String discountFlag;
    @ApiModelProperty("折扣金额，折扣标识为true时，必填")
    private BigDecimal discountAmount;
    @ApiModelProperty("备注")
    private String remarks;
    @ApiModelProperty("零税率标识（默认X-非零税率）")
    private String zeroTaxRateFlag;
    @ApiModelProperty("优惠政策标识（默认false）")
    private String preferentialPolicy;
    @ApiModelProperty("增值税特殊管理")
    private String addTaxSpecial;
    @ApiModelProperty("原发票代码")
    private String oldInvoiceCode;
    @ApiModelProperty("原发票号码")
    private String oldInvoiceNo;
    @ApiModelProperty("红字信息表")
    private String redLetterTab;
    @ApiModelProperty("红冲原因")
    private String reverseReason;

}
