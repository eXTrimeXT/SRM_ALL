package com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto;

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
public class JDSaveOrUpdatePoNoRequestDTO extends BaseRequestDTO {

    @ApiModelProperty("京东订单号")
    private Long jdOrderId;

    @ApiModelProperty("采购单号，长度范围[1-26]")
    private String poNo;

}
