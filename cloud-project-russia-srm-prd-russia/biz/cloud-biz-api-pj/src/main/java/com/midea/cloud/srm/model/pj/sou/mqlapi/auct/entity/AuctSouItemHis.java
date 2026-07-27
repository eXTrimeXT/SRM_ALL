package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouItemHis;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价MQL - 物料需求轮次结果记录
 * PS: 记录每个轮次下，该物料的竞价结果信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_auct_item_his")
public class AuctSouItemHis extends ExtAuctSouItemHis {

    @TableId("AUCT_SOU_ITEM_HIS_ID")
    @ApiModelProperty("ID")
    private Long auctSouItemHisId;

    /** @see SouItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("STANDARD_START_PRICE")
    @ApiModelProperty("起拍价(本币)")
    private BigDecimal standardStartPrice;

    @TableField("ORDER_START_PRICE")
    @ApiModelProperty("起拍价(报价币种)")
    private BigDecimal orderStartPrice;

    @TableField("STANDARD_NO_BID_PRICE")
    @ApiModelProperty("流拍价(本币)")
    private BigDecimal standardNoBidPrice;

    @TableField("ORDER_NO_BID_PRICE")
    @ApiModelProperty("流拍价(报价币种)")
    private BigDecimal orderNoBidPrice;

    @TableField("ORDERED_VENDOR_COUNT")
    @ApiModelProperty("已报价供应商数量")
    private Integer orderedVendorCount;

    @TableField("PRICE_PERCENT")
    @ApiModelProperty("本轮次该物料涨降幅百分比(最新价与起拍价的对比)")
    private BigDecimal pricePercent;

    @TableField("ORDER_NOTAX_PRICE_AMOUNT")
    @ApiModelProperty("原币未税涨降金额(最新价与起拍价的对比)")
    private BigDecimal orderNotaxPriceAmount;

    @TableField("STANDARD_NOTAX_PRICE_AMOUNT")
    @ApiModelProperty("本币未税涨降金额(最新价与起拍价的对比)")
    private BigDecimal standardNotaxPriceAmount;

    @TableField("ORDER_TAX_PRICE_AMOUNT")
    @ApiModelProperty("原币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal orderTaxPriceAmount;

    @TableField("STANDARD_TAX_PRICE_AMOUNT")
    @ApiModelProperty("本币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal standardTaxPriceAmount;

    @TableField("LATEST_ORDER_NOTAX_PRICE")
    @ApiModelProperty("原币未税最新报价")
    private BigDecimal latestOrderNotaxPrice;

    @TableField("LATEST_ORDER_TAX_PRICE")
    @ApiModelProperty("原币含税最新报价")
    private BigDecimal latestOrderTaxPrice;

    @TableField("LATEST_STANDARD_NOTAX_PRICE")
    @ApiModelProperty("本币未税最新报价")
    private BigDecimal latestStandardNotaxPrice;

    @TableField("LATEST_STANDARD_TAX_PRICE")
    @ApiModelProperty("本币含税最新报价")
    private BigDecimal latestStandardTaxPrice;

    @TableField("LATEST_STANDARD_GR_NOTAX_PRICE")
    @ApiModelProperty("本币组合未税最新报价")
    private BigDecimal latestStandardGrNotaxPrice;

    @TableField("LATEST_STANDARD_GR_TAX_PRICE")
    @ApiModelProperty("本币组合含税最新报价")
    private BigDecimal latestStandardGrTaxPrice;

    @TableField("PRICE_NO_BID")
    @ApiModelProperty("是否已达到流拍价")
    private Enable priceNoBid;

}
