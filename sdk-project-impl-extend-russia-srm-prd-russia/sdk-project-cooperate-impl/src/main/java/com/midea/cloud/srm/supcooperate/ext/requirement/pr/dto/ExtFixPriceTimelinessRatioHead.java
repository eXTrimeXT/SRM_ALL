package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author srm
 * @Description: 定价及时率头表实体
 * @date 2024/7/5
 */
@Data
public class ExtFixPriceTimelinessRatioHead {
    @ApiModelProperty("可下单数量")
    private Integer orderQuantity;
    @ApiModelProperty("采购员账号")
    private String createdBy;
    @ApiModelProperty("采购员名称")
    private String createdFullName;

    @Override
    public String toString() {
        return "ExtFixPriceTimelinessRatioHead{" +
                "createdBy='" + createdBy + '\'' +
                ", createdFullName='" + createdFullName + '\'' +
                ", ratio=" + ratio +
                '}';
    }
    /**
     * 定价及时率:1-延迟/总共
     */
    /**
     * 定价及时率：按时完成(onTime)/总数
     */
    @ApiModelProperty("定价及时率")
    private String ratio;
}
