package com.midea.cloud.srm.supcooperate.ext.order.dto;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm 查询京东订单详情请求DTO
 *
 * @author srm
 * @date 2024-05-20
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class JDOrderDetailRequestDTO extends BaseRequestDTO {

    @ApiModelProperty("京东订单号")
    private Long jdOrderId;

    @ApiModelProperty("扩展参数。支持多个状态组合查询[英文逗号间隔]orderType 订单类型jdOrderState 京东订单状态poNo 采购单号finishTime 订单完成时间createOrderTime 订单创建时间paymentType 订单支付类型outTime 订单出库时间 invoiceType 订单发票类型")
    private String queryExts;

}
