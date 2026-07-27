package com.midea.cloud.srm.mall.request.jd.common;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;

@Data
public class JDAddressRequestDTO extends BaseRequestDTO {
    /**
     * 地址
     */
    private String address;

}
