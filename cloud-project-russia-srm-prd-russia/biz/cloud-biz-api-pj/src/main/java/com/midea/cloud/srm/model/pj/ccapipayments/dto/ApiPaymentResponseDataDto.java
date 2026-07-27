package com.midea.cloud.srm.model.pj.ccapipayments.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款保存及自动提交接口-响应数据
 */
@ApiModel("批量付款保存及自动提交接口-响应数据")
@Data
public class ApiPaymentResponseDataDto extends BaseDTO {

    @ApiModelProperty("单据号，共享返回")
    private String orderNo;
    @ApiModelProperty("支付行项目唯一标识")
    private String requestItemId;
    @ApiModelProperty("支付序号")
    private Integer itemNum;
}
