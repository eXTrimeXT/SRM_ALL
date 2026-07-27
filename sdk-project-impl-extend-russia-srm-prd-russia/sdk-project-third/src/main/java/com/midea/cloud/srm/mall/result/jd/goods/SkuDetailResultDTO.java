package com.midea.cloud.srm.mall.result.jd.goods;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 商品详情
@Data
public class SkuDetailResultDTO extends JDBaseResult implements CommonResultDTO {
    private GoodsProductDetail result;

    @Data
    public static class GoodsProductDetail {
        @ApiModelProperty("sku名称")
        private String name;
        @ApiModelProperty("sku编号")
        private String sku;
        @ApiModelProperty("偏移量")
        private String brandName;
        @ApiModelProperty("主站上下架状态 (1上架 0下架)")
        private String state;
        //...还有其它很多字段，如有用到再引入
    }





}
