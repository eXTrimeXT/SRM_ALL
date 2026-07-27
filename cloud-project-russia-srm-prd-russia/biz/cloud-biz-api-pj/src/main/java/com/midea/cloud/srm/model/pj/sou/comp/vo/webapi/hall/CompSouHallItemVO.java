package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemHis;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 - 竞价大厅物料详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouHallItemVO extends ApiCompSouItemVO {

    /** @see CompSouItemHis#getRound */
    @ApiModelProperty("轮次")
    private Integer round;

    /** @see CompSouItemHis#getPricePercent */
    @ApiModelProperty("本轮次该物料涨降幅百分比(最新价与起拍价的对比)")
    private BigDecimal pricePercent;

    /** @see CompSouItemHis#getOrderNotaxPriceAmount */
    @ApiModelProperty("原币未税张降金额(最新价与起拍价的对比)")
    private BigDecimal orderNotaxPriceAmount;

    /** @see CompSouItemHis#getOrderTaxPriceAmount */
    @ApiModelProperty("原币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal orderTaxPriceAmount;

    /** @see CompSouItemHis#getStandardNotaxPriceAmount */
    @ApiModelProperty("本币未税张降金额(最新价与起拍价的对比)")
    private BigDecimal standardNotaxPriceAmount;

    /** @see CompSouItemHis#getStandardTaxPriceAmount */
    @ApiModelProperty("本币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal standardTaxPriceAmount;

    /** @see CompSouItemHis#getLatestOrderNotaxPrice */
    @ApiModelProperty("原币未税最新报价")
    private BigDecimal latestOrderNotaxPrice;

    /** @see CompSouItemHis#getLatestOrderTaxPrice */
    @ApiModelProperty("原币含税最新报价")
    private BigDecimal latestOrderTaxPrice;

    /** @see CompSouItemHis#getLatestStandardNotaxPrice */
    @ApiModelProperty("本币未税最新报价")
    private BigDecimal latestStandardNotaxPrice;

    /** @see CompSouItemHis#getLatestStandardTaxPrice */
    @ApiModelProperty("本币含税最新报价")
    private BigDecimal latestStandardTaxPrice;

    /** @see CompSouItemHis#getLatestStandardGroupNotaxPrice */
    @ApiModelProperty("本币组合未税最新报价")
    private BigDecimal latestStandardGroupNotaxPrice;

    /** @see CompSouItemHis#getLatestStandardGroupTaxPrice */
    @ApiModelProperty("本币组合含税最新报价")
    private BigDecimal latestStandardGroupTaxPrice;

    @ApiModelProperty("已报价供应商数量")
    private Integer compVendorCount;

}
