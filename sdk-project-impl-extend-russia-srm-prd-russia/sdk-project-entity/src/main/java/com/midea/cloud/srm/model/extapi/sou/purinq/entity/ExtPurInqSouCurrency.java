package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouCurrency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purinq_currency")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouCurrency extends BaseEntity<ExtPurInqSouCurrency> {

    /** @see SouCurrency#getSouCurrencyId */
    @TableId("SOU_CURRENCY_ID")
    @ApiModelProperty("ID")
    private Long souCurrencyId;

    /** @see SouCurrency#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("询价单ID")
    private Long projectId;

    @TableField("CURRENCY_CODE")
    @ApiModelProperty("币种编码")
    private String currencyCode;

    @TableField("PRICE_PRECISION")
    @ApiModelProperty("供应商报价精度")
    private Integer pricePrecision;

    /** @see PurchaseExchangeRate#getExchangeRateId */
    @TableField("EXCHANGE_RATE_ID")
    @ApiModelProperty("汇率ID")
    private Long exchangeRateId;

    /** @see PurchaseExchangeRate#getPriceTax */
    @TableField("PRICE_TAX")
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
