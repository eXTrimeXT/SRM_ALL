package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("查看比价")
public class ApiExtComparePriceDto extends BaseObjectX {

    @ApiModelProperty("包名")
    private String extPackageName;

    @ApiModelProperty("名称")
    private String itemDesc;

    @ApiModelProperty("品牌")
    private String extBrand;

    @ApiModelProperty("规格型号")
    private String categoryName;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("数量")
    private BigDecimal extQuantity;

    @ApiModelProperty("供应商报价")
    List<ApiExtCompareVendorPriceDto> priceList;
}
