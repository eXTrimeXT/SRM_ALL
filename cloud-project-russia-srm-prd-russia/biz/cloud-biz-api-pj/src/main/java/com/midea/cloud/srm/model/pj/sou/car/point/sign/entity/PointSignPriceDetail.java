package com.midea.cloud.srm.model.pj.sou.car.point.sign.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <pre>
 *  报价结果表
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/10 16:28
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_point_sign_price_detail")
@ApiModel(description = "寻源-定点会签-总体报价结果")
public class PointSignPriceDetail extends BaseEntity<PointSignPriceDetail> {
    /**
     * 总体报价结果ID
     */
    @ApiModelProperty("总体报价结果ID")
    @TableId("SIGN_DETAIL_ID")
    private Long signDetailId;

    /**
     * 定点会签ID
     */
    @ApiModelProperty("定点会签ID")
    @TableField("SIGN_ID")
    private Long signId;

    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    /**
     * 供应商编码
     */
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;

    /**
     * 研发评价
     */
    @ApiModelProperty("研发评价")
    @TableField("DEV_EVALUATION")
    private String devEvaluation;

    /**
     * 质量评价
     */
    @ApiModelProperty("质量评价")
    @TableField("QUA_EVALUATION")
    private String quaEvaluation;

    /**
     * sop单价（元/台，含分摊）
     */
    @ApiModelProperty("sop单价（元/件，含分摊）")
    @TableField("SOP_SHARE_PRICE")
    private BigDecimal sopSharePrice;

    /**
     * sop单价（元/台，不含分摊）
     */
    @ApiModelProperty("sop单价（元/件，不含分摊）")
    @TableField("SOP_PRICE")
    private BigDecimal sopPrice;

    /**
     * ET1单价
     */
    @ApiModelProperty("ET1单价")
    @TableField("ET1_PRICE")
    private BigDecimal et1Price;

    /**
     * ET2单价
     */
    @ApiModelProperty("ET2单价")
    @TableField("ET2_PRICE")
    private BigDecimal et2Price;

    /**
     * PT单价
     */
    @ApiModelProperty("PT单价")
    @TableField("PT_PRICE")
    private BigDecimal ptPrice;

    /**
     * 备件单价
     */
    @ApiModelProperty("备件单价")
    @TableField("PREPARE_PRICE")
    private BigDecimal preparePrice;

    /**
     * 总开发费（万元）
     */
    @ApiModelProperty("总开发费（万元）")
    @TableField("TOTAL_DEVELOPMENT_COST")
    private BigDecimal totalDevelopmentCost;

    /**
     * 总工装费（万元）
     */
    @ApiModelProperty("总工装费（万元）")
    @TableField("TOTAL_TOOLING_COST")
    private BigDecimal totalToolingCost;

    /**
     * 单独支付费用（万元）
     */
    @ApiModelProperty("单独支付费用（万元）")
    @TableField("SEPARATE_PAYMENT")
    private BigDecimal separatePayment;

    /**
     * 分摊费用（万元）
     */
    @ApiModelProperty("分摊费用（万元）")
    @TableField("COST_SHARING")
    private BigDecimal costSharing;

    /**
     * 税率
     */
    @ApiModelProperty("税率")
    @TableField("TAX_RATE")
    private String taxRate;

    /**
     * 币种名称
     */
    @ApiModelProperty("币种名称")
    @TableField("CURRENCY")
    private String currency;

    /**
     * 币种编码
     */
    @ApiModelProperty("币种编码")
    @TableField("CURRENCY_CODE")
    private String currencyCode;

    /**
     * 未税总价
     */
    @ApiModelProperty("未税总价")
    @TableField("TOTAL_PRICE")
    private BigDecimal totalPrice;

    /**
     * 试验费
     */
    @ApiModelProperty("试验费")
    @TableField("TEST_FEE")
    private BigDecimal testFee;

    /**
     * 工装费
     */
    @ApiModelProperty("工装费")
    @TableField("TOOLING_FEE")
    private BigDecimal toolingFee;

    /**
     * 外包费
     */
    @ApiModelProperty("外包费")
    @TableField("OUTSOURCE_FEE")
    private BigDecimal outsourceFee;

    /**
     * 其他费
     */
    @ApiModelProperty("其他费")
    @TableField("OTHER_FEE")
    private BigDecimal otherFee;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

}
