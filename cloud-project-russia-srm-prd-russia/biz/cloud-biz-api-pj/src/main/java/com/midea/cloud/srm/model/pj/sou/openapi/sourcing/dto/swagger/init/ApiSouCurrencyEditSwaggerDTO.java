package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init.ApiBidSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init.ApiBrgSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCurrencyEditDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 可用币种 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouCurrencyEditDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "可用币种")
@EqualsAndHashCode(callSuper = true)
public class ApiSouCurrencyEditSwaggerDTO extends BaseObjectX {

    /** @see ApiSouCurrencyEditDTO#getSouCurrencyId */
    @ApiModelProperty("ID")
    private Long souCurrencyId;

    /** @see ApiSouCurrencyEditDTO#getCurrencyCode */
    @ApiModelProperty(value = "币种编码(长度限制20)", required = true)
    private String currencyCode;

    /** @see ApiSouCurrencyEditDTO#getPricePrecision */
    @ApiModelProperty(value = "供应商报价精度(0~8)", required = true)
    private Integer pricePrecision;

    /**
     * @see InqSouCurrency#getPriceTax
     * @see ApiBidSouCurrencyEditDTO#getPriceTax
     * @see ApiBrgSouCurrencyEditDTO#getPriceTax
     * @see ApiCompSouCurrencyEditDTO#getPriceTax
     */
    @ApiModelProperty("汇率(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private BigDecimal priceTax;

    /** @see ApiSouCurrencyEditDTO#getSortIndex */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

}
