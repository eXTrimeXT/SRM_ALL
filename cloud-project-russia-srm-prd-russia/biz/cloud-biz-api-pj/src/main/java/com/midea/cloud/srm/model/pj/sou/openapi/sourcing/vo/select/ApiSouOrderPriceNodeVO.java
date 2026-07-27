package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源报价报表-价格目录
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderPriceNodeVO extends BaseObjectX {

    @ApiModelProperty("未税单价")
    private BigDecimal notaxPrice;
    @ApiModelProperty("含税单价")
    private BigDecimal taxPrice;
    @ApiModelProperty("供应商编码")
    private String vendorCode;
    @ApiModelProperty("供应商名称")
    private String vendorName;
    @ApiModelProperty("时间")
    private Date date;

}
