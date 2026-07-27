package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源-核心-可用币种
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@ApiModel(description = "寻源-核心-可用币种")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_currency")
public class SouCurrency extends BaseEntity<SouCurrency> {

    @ApiModelProperty("ID")
    @TableId("SOU_CURRENCY_ID")
    private Long souCurrencyId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("币种编码")
    @TableField("CURRENCY_CODE")
    private String currencyCode;

    @ApiModelProperty("供应商报价精度")
    @TableField("PRICE_PRECISION")
    private Integer pricePrecision;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
