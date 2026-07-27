package com.midea.cloud.srm.supcooperate.ext.checkorders.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 验收单明细
 * </p>
 *
 * @author zenghx2
 * @since 2023-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_check_order_detail")
public class CheckOrderDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,验收单明细
     */
      @TableId("CHECK_ORDER_DETAIL_ID")
    private Long checkOrderDetailId;

    /**
     * 验收单ID
     */
    @TableField("CHECK_ORDER_ID")
    private Long checkOrderId;

    /**
     * 订单明细ID
     */
    @TableField("ORDER_DETAIL_ID")
    private Long orderDetailId;

    /**
     * 本次验收数量
     */
    @TableField("CHECK_QTY")
    private BigDecimal checkQty;

    /**
     * 开票数量
     */
    @TableField("INVOICE_QTY")
    private BigDecimal invoiceQty;


}
