package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 长城 - 询比价 - 报价单\
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_inq_order")
@EqualsAndHashCode(callSuper = true)
public class ExtPjInqSouOrder extends BaseEntity<ExtPjInqSouOrder> {

    /** @see SouOrder#getOrderId */
    @ApiModelProperty("寻源核心-供应商报价头ID")
    @TableId("ORDER_ID")
    private Long orderId;

    /** @see SouOrder#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @TableField("PRICE_ACTIVE_DAY")
    @ApiModelProperty("报价有效期(自然日)")
    private BigDecimal priceActiveDay;

    @ApiModelProperty("报价人")
    @TableField("EXT_ORDER_BY_NICKNAME")
    private String extOrderByNickname;

    @ApiModelProperty("报价联系方式")
    @TableField("EXT_ORDER_PHONE")
    private String extOrderPhone;

}
