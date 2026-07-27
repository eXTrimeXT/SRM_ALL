package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价MQL - 可用币种
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_auct_currency")
@ApiModel(description = "竞价-可用币种")
public class AuctSouCurrency extends ExtAuctSouCurrency {

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

    @TableField("PRICE_TAX")
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
