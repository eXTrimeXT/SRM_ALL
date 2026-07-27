package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源.核心表 - 供应商报价明细
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_order_result")
@ApiModel("寻源供应商报价结果(供应商报价结果)")
public class SouOrderResult extends BaseEntity<SouOrderResult> {


    @TableId("ORDER_RESULT_ID")
    private Long orderResultId;

    @TableField("PROJECT_ID")
    private Long projectId;

    @TableField("ORDER_ID")
    private Long orderId;

    @TableField("SOU_ITEM_ID")
    private Long souItemId;

    @TableField("AFFILIATED_UNIT")
    private String affiliatedUnit;

    @TableField("MONTHLY_PRODUCTION")
    private BigDecimal monthlyProduction;

    @TableField("MAX_VENDOR_NAME")
    @ApiModelProperty("最高价供应商名称")
    private String maxVendorName;

    @TableField("MAX_VENDOR_ID")
    @ApiModelProperty("最高价供应商ID")
    private Long maxVendorId;

    @TableField("MAX_PRICE")
    @ApiModelProperty("最高价金额")
    private BigDecimal maxPrice;

    @TableField("SECOND_VENDOR_NAME")
    @ApiModelProperty("次高价供应商名称")
    private String secondVendorName;

    @TableField("SECOND_VENDOR_ID")
    @ApiModelProperty("次高价供应商ID")
    private Long secondVendorId;

    @TableField("SECOND_PRICE")
    @ApiModelProperty("次高价金额")
    private BigDecimal secondPrice;

    @TableField("THIRD_VENDOR_NAME")
    @ApiModelProperty("第三高价供应商名称")
    private String thirdVendorName;

    @TableField("THIRD_VENDOR_ID")
    @ApiModelProperty("第三高价供应商ID")
    private Long thirdVendorId;

    @TableField("THIRD_PRICE")
    @ApiModelProperty("第三高价金额")
    private BigDecimal thirdPrice;
    /** 通过物料名称查询最近一次的竞价中标供应商 */
    @TableField("PERIOD_VENDOR_NAME")
    @ApiModelProperty("上期中标供应商名称")
    private String periodVendorName;

    @TableField("PERIOD_VENDOR_ID")
    @ApiModelProperty("上期中标供应商ID")
    private Long periodVendorId;

    @TableField("PERIOD_PRICE")
    @ApiModelProperty("上期中标金额")
    private BigDecimal periodPrice;

    /** 本期最高价-上期中标单价）上期中标单价 */
    @TableField("DIFFERENCE_RATE")
    @ApiModelProperty("价格差异率")
    private BigDecimal differenceRate;

    /** 单价*月产量 */
    @TableField("MONTH_TOTAL_AMOUNT")
    @ApiModelProperty("月总金额")
    private BigDecimal monthTotalAmount;

    @TableField("ORDER_ROUND")
    private BigDecimal orderRound;

    @TableField("RESULT_STATUS")
    private SouApprovalStatusEnum resultStatus;

    @TableField("WIN_NOTICE_STATUS")
    private SouApprovalStatusEnum winNoticeStatus;

    @TableField("ORDER_REMARK")
    private String orderRemark;

    @TableField("WIN_STATUS")
    private String winStatus;

    @TableField("select_status")
    private String selectStatus;

    @TableField("SELECT_REMARK")
    private String selectRemark;

    @TableField("WIN_NOTICE_REMARK")
    private String winNoticeRemark;

    @TableField("ITEM_DESC")
    private String itemDesc;

    @TableField("WIN_VENDOR_ID")
    private Long winVendorId;

    @TableField("WIN_VENDOR_NAME")
    private String winVendorName;

    @TableField("WIN_VENDOR_PRICE")
    @ApiModelProperty("中标金额")
    private BigDecimal winVendorPrice;

    @TableField("START_BPM_USERNAME")
    @ApiModelProperty(value = "bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty(value = "bpm发起人名称")
    private String startBpmNickname;

    @TableField("START_BPM_NOTICE_USERNAME")
    @ApiModelProperty(value = "中标通知bpm发起人账号")
    private String startBpmNoticeUsername;

    @TableField("START_BPM_NOTICE_NICKNAME")
    @ApiModelProperty(value = "中标通知bpm发起人名称")
    private String startBpmNoticeNickname;

    @TableField("WIN_REASON")
    @ApiModelProperty(value = "中标原因")
    private String winReason;

    @TableField("FAILURE_REASON")
    @ApiModelProperty(value = "流标原因")
    private String failureReason;

    @TableField("FAILURE_BID_FLAG")
    @ApiModelProperty(value = "是否流标")
    private String failureBidFlag;



}
