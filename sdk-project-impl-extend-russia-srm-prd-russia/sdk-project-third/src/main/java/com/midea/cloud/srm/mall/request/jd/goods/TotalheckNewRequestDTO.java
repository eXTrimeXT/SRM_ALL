package com.midea.cloud.srm.mall.request.jd.goods;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import lombok.Data;

@Data
public class TotalheckNewRequestDTO extends BaseRequestDTO {

    private String province;
    private String city;
    private String county;
    private String town;
    private String skuIds;

}
