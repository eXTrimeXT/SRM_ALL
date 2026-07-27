

package com.midea.cloud.srm.model.pj.ccapisettleacountings.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账-响应数据-凭证信息")
public class ApiSettleAcountingResponseDataVoucher {

    @ApiModelProperty("行号，对应请求中baseInfo中的itemNo")
    private Integer itemNo;

    @ApiModelProperty("凭证号，多个凭证号以英文“,”分隔")
    private String voucherNo;

    @ApiModelProperty("凭证年")
    private String year;

    @ApiModelProperty("公司代码")
    private String companyCode;

}
