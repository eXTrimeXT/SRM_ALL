
package com.midea.cloud.srm.model.pj.ccapisettleacountings.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账-响应数据")
public class ApiSettleAcountingResponseData {

    @ApiModelProperty("系统编码")
    private String systemCode;

    @ApiModelProperty("业务单据号")
    private String businessNo;

    @ApiModelProperty("请求流水号")
    private String reqSn;

    @ApiModelProperty("结算单据编码")
    private String settleDocumentCode;

    @ApiModelProperty("凭证信息")
    private List<ApiSettleAcountingResponseDataVoucher> voucherList;
}
