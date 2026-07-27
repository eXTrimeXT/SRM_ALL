package com.midea.cloud.srm.supcooperate.ext.order.dto;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class JDOrderDetailTotalCheckRequestDTO extends BaseRequestDTO {

    @ApiModelProperty("京东一级地址编号")
    private String province;

    @ApiModelProperty("京东二级地址编号")
    private String city;

    @ApiModelProperty("京东三级地址编号")
    private String county;

    @ApiModelProperty("京东四级地址编号 如果没有请传0")
    private String town;

    @ApiModelProperty("商品编号，支持批量，以，分隔 (最高支持100个商品)，此参数最多传入100条记录")
    private String skuIds;

}
