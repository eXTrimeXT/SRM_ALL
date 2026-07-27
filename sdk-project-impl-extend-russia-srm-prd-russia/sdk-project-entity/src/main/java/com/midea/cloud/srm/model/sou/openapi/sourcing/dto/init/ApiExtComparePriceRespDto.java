package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("查看比较返回对象")
@Data
public class ApiExtComparePriceRespDto extends BaseObjectX {

    @ApiModelProperty("比价列表")
    private List<ApiExtComparePriceDto> comparePriceList;

    @ApiModelProperty("未税暂定总价-按包名小计")
    private Map<String, List<BigDecimal>> extPacknameProvPriceSumNoTaxMap;

    @ApiModelProperty("含税暂定总价-按包名小计")
    private Map<String, List<BigDecimal>> extPacknameProvPriceSumTaxMap;

    /**
     * 发票类型
     */
    @ApiModelProperty("发票类型")
    private List<String> extInvoiceTypeList;

    /**
     * 暂定未税总价（万元）
     */
    @ApiModelProperty("未税暂定总价")
    private List<BigDecimal> extProvPriceSumNoTaxList;

    /**
     * 暂定含税总价（万元）
     */
    @ApiModelProperty("含税暂定总价")
    private List<BigDecimal> extProvPriceSumTaxList;

    /**
     * 供应商未税、含税暂定总价 Map 对象
     */
    @ApiModelProperty("供应商未税、含税暂定总价 Map 对象")
    Map<Long, ApiExtCompareVendorPriceDto> priceMap;

    @ApiModelProperty("合并招标标识")
    private Boolean mergeFlag;
}
