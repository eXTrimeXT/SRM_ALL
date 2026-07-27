package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞价MQL - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/14
 */
@Data
@TableName("scc_sou_auct_order_item")
@EqualsAndHashCode(callSuper = true)
public class AuctSouOrderItem extends ExtAuctSouOrderItem {

    /** @see SouOrderItem#getOrderItemId */
    @TableId("ORDER_ITEM_ID")
    @ApiModelProperty("物料需求行ID")
    private Long orderItemId;

    /** @see SouOrderItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouOrderItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

    /** @see SouOrderItem#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouOrderItem#getVendorId */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrderItem#getRound */
    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("ORDERED_COUNT")
    @ApiModelProperty("报价次数")
    private Integer orderedCount;

    @TableField("EXTEND_ORDERED_COUNT")
    @ApiModelProperty("延时期间报价次数")
    private Integer extendOrderedCount;

    @TableField("PRICE_NO_BID")
    @ApiModelProperty("报价是否流拍")
    private Enable priceNoBid;

    @TableField("PRICE_PERCENT")
    @ApiModelProperty("报价增降幅(报价相比于起始价)")
    private BigDecimal pricePercent;

    @TableField("ORDER_NOTAX_PRICE_AMOUNT")
    @ApiModelProperty("原币未税增降金额")
    private BigDecimal orderNotaxPriceAmount;

    @TableField("ORDER_TAX_PRICE_AMOUNT")
    @ApiModelProperty("原币含税增降金额")
    private BigDecimal orderTaxPriceAmount;

    @TableField("STANDARD_NOTAX_PRICE_AMOUNT")
    @ApiModelProperty("本币未税增降金额")
    private BigDecimal standardNotaxPriceAmount;

    @TableField("STANDARD_TAX_PRICE_AMOUNT")
    @ApiModelProperty("本币含税增降金额")
    private BigDecimal standardTaxPriceAmount;

    @TableField("ORDER_NOTAX_TOTAL_PRICE")
    @ApiModelProperty("原币未税总金额(单价*数量)")
    private BigDecimal orderNotaxTotalPrice;

    @TableField("ORDER_TAX_TOTAL_PRICE")
    @ApiModelProperty("原币含税总金额(单价*数量)")
    private BigDecimal orderTaxTotalPrice;

    @TableField("STANDARD_NOTAX_TOTAL_PRICE")
    @ApiModelProperty("本币未税总金额(单价*数量)")
    private BigDecimal standardNotaxTotalPrice;

    @TableField("STANDARD_TAX_TOTAL_PRICE")
    @ApiModelProperty("本币含税总金额(单价*数量)")
    private BigDecimal standardTaxTotalPrice;

    /**
     * 提交人信息
     */
    @TableField("SUBMIT_BY_ID")
    @ApiModelProperty("提交人ID")
    private Long submitById;

    @TableField("SUBMIT_BY")
    @ApiModelProperty("提交人账号")
    private String submitBy;

    @TableField("SUBMIT_BY_IP")
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    @TableField("SUBMIT_FULL_NAME")
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    @TableField("SUBMIT_TIME")
    @ApiModelProperty("提交时间")
    private Date submitTime;

}
