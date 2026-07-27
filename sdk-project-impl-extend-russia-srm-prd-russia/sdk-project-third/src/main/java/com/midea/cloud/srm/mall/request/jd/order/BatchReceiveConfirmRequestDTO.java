package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BatchReceiveConfirmRequestDTO extends BaseRequestDTO {
    /**
     * 京东子单号，请以，(英文逗号)分割。
     * 例如：129408,129409(最高支持50个订单)
     */
    private String jdOrderIds;
}
