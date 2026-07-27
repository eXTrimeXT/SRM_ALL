package com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm 查询京东订单物流信息请求参数DTO
 *
 * @author srm
 * @date 2024-05-20
 */

@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class JDDeliveryRequestDTO extends BaseRequestDTO {

    @ApiModelProperty("京东订单号")
    private String jdOrderId;

    @ApiModelProperty("是否返回订单的配送信息。0不返回配送信息。1，返回配送信息。只支持最近2个月的配送信息查询。")
    private Integer waybillCode;

}
