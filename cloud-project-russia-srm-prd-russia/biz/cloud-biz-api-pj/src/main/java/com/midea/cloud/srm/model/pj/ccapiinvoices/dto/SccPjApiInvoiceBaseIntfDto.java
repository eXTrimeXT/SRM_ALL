package com.midea.cloud.srm.model.pj.ccapiinvoices.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/08/ $
 * @Description:
 */
@ApiModel("发票开具创建-结算行-基本信息")
@Data
public class SccPjApiInvoiceBaseIntfDto extends BaseDTO {
    @ApiModelProperty("主键")
    private Long baseIntfId;
    @ApiModelProperty("外键")
    private Long invoiceIntfId;
    @ApiModelProperty("行号，一次请求不可重复")
    private Integer itemNo;
    @ApiModelProperty("客商类型（1-客户，2-供应商）")
    private String partnerType;
    @ApiModelProperty("客商编码")
    private String partnerCode;
    @ApiModelProperty("客商名称")
    private String partnerName;
    @ApiModelProperty("利润中心编码")
    private String profitCenterCode;
    @ApiModelProperty("利润中心名称")
    private String profitCenterName;
    @ApiModelProperty("成本中心编码")
    private String costCenterCode;
    @ApiModelProperty("成本中心名称")
    private String costCenterName;
    @ApiModelProperty("单据说明")
    private String documentExplain;
    @ApiModelProperty("客商分类（01-企业，02-个人，03-事业）")
    private String partnerClassify;
    @ApiModelProperty("客商社会信用统一代码")
    private String partnerTaxPayerCode;
    @ApiModelProperty("客商地址")
    private String partnerAddress;
    @ApiModelProperty("客商电话")
    private String partnerPhone;
    @ApiModelProperty("客商银行名称")
    private String partnerBankName;
    @ApiModelProperty("客商银行账号")
    private String partnerBankAccount;
    @ApiModelProperty("客商邮箱")
    private String partnerEmail;
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
