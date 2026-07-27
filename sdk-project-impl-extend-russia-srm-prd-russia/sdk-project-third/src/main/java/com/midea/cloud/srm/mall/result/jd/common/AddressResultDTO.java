package com.midea.cloud.srm.mall.result.jd.common;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 商品详情
@Data
    public class AddressResultDTO extends JDBaseResult implements CommonResultDTO {
    private AddressDetail result;

    @Data
    public static class AddressDetail {
        @ApiModelProperty("国家ID")
        private String nationId;

        @ApiModelProperty("国家名称")
        private String nation;

        @ApiModelProperty("一级地址ID")
        private Integer provinceId;

        @ApiModelProperty("一级地址名称")
        private String province;

        @ApiModelProperty("二级地址ID")
        private Integer cityId;

        @ApiModelProperty("二级地址名称")
        private String city;

        @ApiModelProperty("三级地址ID")
        private Integer countyId;

        @ApiModelProperty("三级地址名称")
        private String county;

        @ApiModelProperty("四级地址ID")
        private Integer townId;

        @ApiModelProperty("四级地址名称")
        private String town;
    }





}
