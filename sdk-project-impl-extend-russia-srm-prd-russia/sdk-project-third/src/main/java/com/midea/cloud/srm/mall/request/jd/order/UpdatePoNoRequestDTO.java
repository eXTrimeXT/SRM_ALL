package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdatePoNoRequestDTO extends BaseRequestDTO {

    /**
     * jd订单号
     */
    private String jdOrderId;
    /**
     * 采购订单号（长城用来传输送货单号）
     */
    private String poNo;
}
