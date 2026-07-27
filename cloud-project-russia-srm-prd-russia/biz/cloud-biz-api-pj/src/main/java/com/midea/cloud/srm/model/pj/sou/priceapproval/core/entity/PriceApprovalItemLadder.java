package com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.pj.sou.model.entity.ExtPriceApprovalItemLadder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 价格审批单 - 中标行阶梯价
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/18
 */
@Data
@ApiModel("价格审批单-中标行阶梯价")
@TableName("scc_price_approval_item_ladder")
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalItemLadder extends ExtPriceApprovalItemLadder {

    @TableId("APPROVAL_ITEM_LADDER_ID")
    @ApiModelProperty("ID")
    private Long approvalItemLadderId;

    /** @see PriceApprovalItem#getApprovalItemId */
    @TableField("APPROVAL_ITEM_ID")
    @ApiModelProperty("中标行ID")
    private Long approvalItemId;

    /** @see PriceApprovalItem#getApprovalId */
    @TableField("APPROVAL_ID")
    @ApiModelProperty("价格审批单ID")
    private Long approvalId;

    /**
     * 区间信息
     */
    @ApiModelProperty("阶梯区间从")
    @TableField("BEGIN_QUANTITY")
    private BigDecimal beginQuantity;

    @ApiModelProperty("阶梯区间到")
    @TableField("END_QUANTITY")
    private BigDecimal endQuantity;

    /**
     * 价格信息
     */
    @ApiModelProperty("原币未税单价")
    @TableField("ORDER_NOTAX_PRICE")
    private BigDecimal orderNotaxPrice;

    @ApiModelProperty("原币含税单价")
    @TableField("ORDER_TAX_PRICE")
    private BigDecimal orderTaxPrice;

    @ApiModelProperty("本币未税单价")
    @TableField("STANDARD_NOTAX_PRICE")
    private BigDecimal standardNotaxPrice;

    @ApiModelProperty("本币含税单价")
    @TableField("STANDARD_TAX_PRICE")
    private BigDecimal standardTaxPrice;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
