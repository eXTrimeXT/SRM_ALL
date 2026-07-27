package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouItemHis;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 - 物料需求轮次结果记录
 * PS: 记录每个轮次下，该物料的竞价结果信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_sou_item_his")
public class CompSouItemHis extends ExtCompSouItemHis {

    @TableId("COMP_SOU_ITEM_HIS_ID")
    @ApiModelProperty("ID")
    private Long compSouItemHisId;

    /** @see SouItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("sou_item_id")
    private Long souItemId;

    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("PRICE_PERCENT")
    @ApiModelProperty("本轮次该物料涨降幅百分比(最新价与起拍价的对比)")
    private BigDecimal pricePercent;

    @TableField("ORDER_NOTAX_PRICE_AMOUNT")
    @ApiModelProperty("原币未税张降金额(最新价与起拍价的对比)")
    private BigDecimal orderNotaxPriceAmount;

    @TableField("ORDER_TAX_PRICE_AMOUNT")
    @ApiModelProperty("原币含税涨降金额(最新价与起拍价的对比)")
    private BigDecimal orderTaxPriceAmount;

    @TableField("STANDARD_NOTAX_PRICE_AMOUNT")
    @ApiModelProperty("本币未税张降金额(最新价与起拍价的对比)")
    private BigDecimal standardNotaxPriceAmount;

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
    private BigDecimal latestStandardGroupNotaxPrice;

    @TableField("LATEST_STANDARD_GR_TAX_PRICE")
    @ApiModelProperty("本币组合含税最新报价")
    private BigDecimal latestStandardGroupTaxPrice;

}
