package com.midea.cloud.srm.model.pj.pricetax.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author huangbf3
 * 汇率
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_purchase_ex_rate")
@ApiModel(description = "汇率")
@QlMatchType("PurchaseExchangeRate")
public class PurchaseExchangeRate extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId("EXCHANGE_RATE_ID")
    private Long exchangeRateId;

    /**
     * 来源币种
     *
     * @see PurchaseCurrency#getCurrencyCode
     */
    @ApiModelProperty(value = "来源币种")
    @TableField("FROM_CURRENCY_CODE")
    private String fromCurrencyCode;

    /**
     * 目标币种
     *
     * @see PurchaseCurrency#getCurrencyCode
     */
    @ApiModelProperty(value = "目标币种")
    @TableField("TO_CURRENCY_CODE")
    private String toCurrencyCode;

    @ApiModelProperty(value = "转换日期")
    @TableField("EXCHANGE_DATE")
    private Date exchangeDate;

    @ApiModelProperty(value = "汇率")
    @TableField("PRICE_TAX")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "汇率类型(字典值: EXCHANGE_RATE_TYPE)")
    @TableField("RATE_TYPE")
    private String rateType;

    /**
     * 数据来源(字典值: EXCHANGE_RATE_SOURCE_TYPE)
     *
     * @see PurchaseExchangeRateSourceType#MANUAL   手工创建
     * @see PurchaseExchangeRateSourceType#ERP      ERP
     */
    @ApiModelProperty(value = "数据来源")
    @TableField("SOURCE_TYPE")
    private String sourceType;

    @ApiModelProperty(value = "是否有效(Y/N)")
    @TableField("ENABLED")
    private String enabled;

}
