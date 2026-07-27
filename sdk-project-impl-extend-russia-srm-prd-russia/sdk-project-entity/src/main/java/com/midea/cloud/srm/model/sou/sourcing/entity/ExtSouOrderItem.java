package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("寻源核心-报价行表")
@TableName("scc_sou_order_item")
public class ExtSouOrderItem extends SouOrderItem{

    /**
     * 发票类型
     */
    @ApiModelProperty("发票类型")
    private String extInvoiceType;

    @ApiModelProperty("投标时间")
    private Date extSubmitTime;

    @ApiModelProperty("汇率")
    private BigDecimal extExchangeRate;

    @ApiModelProperty("含税总价")
    @TableField(exist = false)
    private BigDecimal extTaxAmount;
}
