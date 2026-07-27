package com.midea.cloud.srm.model.pj.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 简易询价-可用币种
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_inq_currency")
@ApiModel(description = "简易询价-可用币种")
public class InqSouCurrency extends ExtInqSouCurrency {

    @TableId("SOU_CURRENCY_ID")
    @ApiModelProperty("ID")
    private Long souCurrencyId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
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
