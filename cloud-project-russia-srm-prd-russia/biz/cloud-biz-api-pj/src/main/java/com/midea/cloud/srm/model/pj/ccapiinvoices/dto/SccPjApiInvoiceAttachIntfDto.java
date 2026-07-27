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
@ApiModel("发票开具创建-结算行-附件列表")
@Data
public class SccPjApiInvoiceAttachIntfDto extends BaseDTO {

    @ApiModelProperty("主键")
    private Long attachIntfId;
    @ApiModelProperty("外键")
    private Long invoiceIntfId;
    @ApiModelProperty("附件名称")
    private String attachName;
    @ApiModelProperty("附件链接")
    private String attachUrl;
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
