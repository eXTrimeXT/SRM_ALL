package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 - 可用币种
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_currency")
@ApiModel(description = "竞价.可用币种")
public class CompSouCurrency extends ExtCompSouCurrency {

    @ApiModelProperty("主键ID")
    @TableId("SOU_CURRENCY_ID")
    private Long souCurrencyId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouCurrency#getCurrencyCode */
    @ApiModelProperty("币种编码")
    @TableField("CURRENCY_CODE")
    private String currencyCode;

    /** @see SouCurrency#getPricePrecision */
    @ApiModelProperty("供应商报价精度")
    @TableField("PRICE_PRECISION")
    private Integer pricePrecision;

    /** @see PurchaseExchangeRate#getExchangeRateId */
    @TableField("EXCHANGE_RATE_ID")
    @ApiModelProperty("汇率ID")
    private Long exchangeRateId;

    /** @see PurchaseExchangeRate#getPriceTax */
    @TableField("PRICE_TAX")
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    /** @see SouCurrency#getSortIndex */
    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
