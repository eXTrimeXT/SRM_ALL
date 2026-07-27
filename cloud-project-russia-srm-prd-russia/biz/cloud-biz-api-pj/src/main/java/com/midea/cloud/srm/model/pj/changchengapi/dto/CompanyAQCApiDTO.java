package com.midea.cloud.srm.model.pj.changchengapi.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

/**
 * @author huangbf3
 * 供应商大数据爱企查结果
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class CompanyAQCApiDTO {

    @ApiModelProperty("供应商名称")
    private String company;

    private ItemData data;

    @Data
    @ApiOperation("详细数据")
    public static class ItemData {

        @ApiModelProperty("供应商名称")
        private String entName;

        @ApiModelProperty("法人")
        private String legalPerson;

        @ApiModelProperty("开业日期")
        private String startDate;

        @ApiModelProperty("企业地址")
        private String regAddr;

        @ApiModelProperty("注册资金")
        private String regCapital;

        @ApiModelProperty("主要人员")
        private List<String> directorsData;

        @ApiModelProperty("主要股东")
        private List<String> shareholdersData;

        @ApiModelProperty("是否失信")
        private Boolean brokenPromises;

        @ApiModelProperty("是否经营异常")
        private Boolean abnormalOperation;
    }
}
