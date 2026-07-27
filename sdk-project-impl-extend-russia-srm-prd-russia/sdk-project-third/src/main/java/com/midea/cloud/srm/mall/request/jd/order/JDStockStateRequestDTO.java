package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JDStockStateRequestDTO extends BaseRequestDTO {

    /**
     * 商品和数量 [{skuId: 569172,num:101}]。“{skuId: 569172,num:10}”为1条记录
     * 此参数最多传入100条记录
     */
    private String skuNums;

    /**
     * 格式：13_1000_4277_0 (分别代表1、2、3、4级地址)
     */
    private String area;
}
