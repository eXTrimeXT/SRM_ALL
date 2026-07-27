package com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.model.entity.ExtPriceApproval;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author huangbf3
 */
@Data
@ApiModel("价格审批单-头表")
@TableName("scc_price_approval")
@EqualsAndHashCode(callSuper = true)
public class PriceApproval extends ExtPriceApproval {

    public static final String HAND_MAKE = "handMake";

    @TableId("APPROVAL_ID")
    @ApiModelProperty("ID")
    private Long approvalId;

    @TableField("APPROVAL_NO")
    @ApiModelProperty("价格审批单编号")
    private String approvalNo;

    @TableField("APPROVAL_NAME")
    @ApiModelProperty("价格审批单名称")
    private String approvalName;

    /** @see PriceApprovalStatusEnum */
    @TableField("APPROVAL_STATUS")
    @ApiModelProperty("价格审批单状态")
    private String approvalStatus;

    @TableField("STANDARD_CURRENCY")
    @ApiModelProperty("本位币")
    private String standardCurrency;

    @TableField("PRICE_PRECISION")
    @ApiModelProperty("本位币价格精度")
    private Integer pricePrecision;

    @TableField("EXCHANGE_RATE_TYPE")
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    @TableField("EXCHANGE_RATE_DATE")
    @ApiModelProperty("币种转换日期")
    private LocalDate exchangeRateDate;

    @TableField("IS_PRICE_NOTAX")
    @ApiModelProperty("Y-供应商报价时使用未税价/N-供应商报价时使用含税价")
    private Enable isPriceNotax;

    @TableField("IS_SYNC_TO_PRICE_LIBRARY")
    @ApiModelProperty("是否同步至价格库")
    private Enable isSyncToPriceLibrary;

    @TableField("DEMAND_SUMMARY")
    @ApiModelProperty("需求概述")
    private String demandSummary;

    @TableField("REMARK")
    @ApiModelProperty("说明")
    private String remark;

    @TableField("NOTAX_BID_AMOUNT")
    @ApiModelProperty("本币未税中标总金额")
    private BigDecimal notaxBidAmount;

    @TableField("TAX_BID_AMOUNT")
    @ApiModelProperty("本币含税中标总金额")
    private BigDecimal taxBidAmount;

    @TableField("SUBMIT_TAG")
    @ApiModelProperty("编辑时是否已填完信息")
    private Enable submitTag;

    // ------------------------------------------------------- 来源信息 --------------------------------------------------------
    /** @see PriceApprovalFromTypeEnum */
    @TableField("SOURCE_FROM_TYPE")
    @ApiModelProperty("来源单据类型(字典: PRICE_APPROVAL_FROM_TYPE)")
    private String sourceFromType;

    @TableField("SOURCE_FROM_ID")
    @ApiModelProperty("来源单据ID(String类型-应对特殊情况)")
    private String sourceFromId;

    @TableField("SOURCE_FROM_NO")
    @ApiModelProperty("来源单据编号")
    private String sourceFromNo;

    @TableField("SOURCE_FROM_NAME")
    @ApiModelProperty("来源单据名称")
    private String sourceFromName;

}
