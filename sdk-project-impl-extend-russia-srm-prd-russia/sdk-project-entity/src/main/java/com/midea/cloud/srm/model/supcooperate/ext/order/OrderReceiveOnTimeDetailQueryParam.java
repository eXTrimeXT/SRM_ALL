package com.midea.cloud.srm.model.supcooperate.ext.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@Data
public class OrderReceiveOnTimeDetailQueryParam extends OrderReceiveOnTimeQueryParam{

    @ApiModelProperty("订单编号")
    private String orderNumber;

    @ApiModelProperty("交货日期")
    private List<Date> deliveryDate;

    @ApiModelProperty("物料编码")
    private String materialCode;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

}
