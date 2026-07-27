package com.midea.cloud.srm.mall.request.jd.goods;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;

@Data
public class GetSellPriceRequestDTO extends BaseRequestDTO {
    private String sku;
}
