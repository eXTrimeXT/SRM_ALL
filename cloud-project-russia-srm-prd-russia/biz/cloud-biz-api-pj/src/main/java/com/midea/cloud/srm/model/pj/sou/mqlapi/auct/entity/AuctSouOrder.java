package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 竞价 MQL - 报价单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/15
 */
@Data
@TableName("scc_sou_auct_order")
@EqualsAndHashCode(callSuper = true)
public class AuctSouOrder extends ExtAuctSouOrder {

    @ApiModelProperty("寻源核心-供应商报价头ID")
    @TableId("ORDER_ID")
    private Long orderId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouVendor#getVendorId */
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    /** @see SouRound#getRound */
    @ApiModelProperty("报价轮次")
    @TableField("ROUND")
    private Integer round;

    /** 前提是供应商只用一种币种报价 */
    @ApiModelProperty("原币未税总价")
    @TableField("ORDER_NOTAX_TOTAL_PRICE")
    private BigDecimal orderNotaxTotalPrice;

    /** 前提是供应商只用一种币种报价 */
    @ApiModelProperty("原币含税总价")
    @TableField("ORDER_TAX_TOTAL_PRICE")
    private BigDecimal orderTaxTotalPrice;

}
