package com.midea.cloud.srm.mall.request.jd.goods;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;

import java.util.List;

@Data
public class JDGoodsRequestDTO extends BaseRequestDTO {

    private String jdField;
    private String sku;

    // 随便定义，  定义List也行， 多个对象也行
    private List<Object> params;

}
