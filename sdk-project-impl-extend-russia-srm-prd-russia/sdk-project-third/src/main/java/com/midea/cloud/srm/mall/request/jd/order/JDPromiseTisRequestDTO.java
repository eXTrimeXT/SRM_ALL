package com.midea.cloud.srm.mall.request.jd.order;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JDPromiseTisRequestDTO extends BaseRequestDTO {

    /**
     * 商品编号
     */
    private String skuId;
    /**
     * 数量
     */
    private String num;
    /**
     * 一级地址
     */
    private int province;
    /**
     * 二级地址
     */
    private int city;
    /**
     * 三级地址
     */
    private int county;
    /**
     * 四级地址
     */
    private int town;

}
