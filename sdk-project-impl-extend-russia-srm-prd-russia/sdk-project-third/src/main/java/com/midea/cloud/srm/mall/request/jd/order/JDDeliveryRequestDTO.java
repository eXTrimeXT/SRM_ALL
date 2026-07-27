package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JDDeliveryRequestDTO extends BaseRequestDTO {
    private String jdOrderId;
    private Integer waybillCode;
}
