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
import java.time.LocalDate;

/**
 * <pre>
 *  寻寻源-定点会签-定点明细
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/10 16:54
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_point_sign_detail")
@ApiModel(description = "寻源-定点会签-定点明细")
public class PointSignDetail  extends BaseEntity<PointSignDetail> {
    /**
     * 定点明细ID
     */
    @ApiModelProperty("定点明细ID")
    @TableId("SIGN_DETAIL_ID")
    private Long signDetailId;

    /**
     * 序号
     */
    @ApiModelProperty("序号")
    @TableField("num")
    private Integer num;

    /**
     * 定点会签ID
     */
    @ApiModelProperty("定点会签ID")
    @TableField("SIGN_ID")
    private Long signId;

    /**
     * 工厂ID
     */
    @ApiModelProperty("工厂ID")
    @TableField("INV_ID")
    private Long invId;

    /**
     * 工厂编码
     */
    @ApiModelProperty("工厂编码")
    @TableField("INV_CODE")
    private String invCode;

    /**
     * 工厂名称
     */
    @ApiModelProperty("工厂名称")
    @TableField("INV_NAME")
    private String invName;

    /**
     * 专业组
     */
    @ApiModelProperty("专业组")
    @TableField("PROFESSIONAL_GROUP")
    private String professionalGroup;

    /**
     * 车型
     */
    @ApiModelProperty("车型")
    @TableField("CAR_TYPE")
    private String carType;

    /**
     * 物料ID
     */
    @ApiModelProperty("物料ID")
    @TableField("MATERIAL_ID")
    private Long materialId;

    /**
     * 物料编码
     */
    @ApiModelProperty("物料编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;

    /**
     * 物料描述
     */
    @ApiModelProperty("物料描述")
    @TableField("MATERIAL_NAME")
    private String materialName;

    /**
     * 计价单位
     */
    @ApiModelProperty("计价单位")
    @TableField("PRICING_UNIT")
    private String pricingUnit;

    /**
     * 计价数量
     */
    @ApiModelProperty("计价数量")
    @TableField("PRICING_QUANTITY")
    private BigDecimal pricingQuantity;

    /**
     * 定点决议
     */
    @ApiModelProperty("定点决议")
    @TableField("RESULT")
    private String result;

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
     * sop单价（元/件，含分摊）/到场单价（元/件，含分摊）/未税单价
     */
    @ApiModelProperty("sop单价（元/件，含分摊）/到场单价（元/件，含分摊）/未税单价")
    @TableField("SOP_SHARE_PRICE")
    private BigDecimal sopSharePrice;

    /**
     * sop单价（元/台，不含分摊）
     */
    @ApiModelProperty("sop单价（元/件，不含分摊）/到场单价（元/件，不含分摊）")
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
    private BigDecimal taxRate;

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
    @TableField("REMARK")
    private String remark;

    /**
     * 分摊年份
     */
    @ApiModelProperty("分摊年份")
    @TableField("SHARE_YEAR")
    private String shareYear;


    /**
     * 总分摊数（万台）
     */
    @ApiModelProperty("总分摊数（万台）")
    @TableField("SHARE_QUANTITY")
    private BigDecimal shareQuantity;

    /**
     * 价格生效时间
     */
    @ApiModelProperty("价格生效时间")
    @TableField("START_DATE")
    private LocalDate startDate;

    /**
     * 价格失效时间
     */
    @ApiModelProperty("价格失效时间")
    @TableField("END_DATE")
    private LocalDate endDate;

    /**
     * 结算方式
     */
    @ApiModelProperty("结算方式")
    @TableField("SETTLEMENT_MENTHOD")
    private String settlementMenthod;


    /**
     * 含税单价
     */
    @ApiModelProperty("含税单价")
    @TableField("TAX_PRICE")
    private BigDecimal taxPrice;


    /**
     * 过往合作车型及零件
     */
    @ApiModelProperty("过往合作车型及零件")
    @TableField("PCMAP")
    private String pcmap;

    /**
     * 交货日期
     */
    @ApiModelProperty("交货日期")
    @TableField("DELIVERY_DATE")
    private LocalDate deliveryDate;

    /**
     * 需求数量
     */
    @ApiModelProperty("需求数量")
    @TableField("demand_quantity")
    private BigDecimal demandQuantity;

    /**
     * 中标数量
     */
    @ApiModelProperty("中标数量")
    @TableField("BID_WIN_QUANTITY")
    private BigDecimal bidWinQuantity;

}
