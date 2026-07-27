package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouWinStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源.核心表 - 供应商报价明细
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_order_item_his")
@ApiModel("寻源供应商报价明细(供应商报价行)")
public class SouOrderItemHis extends BaseEntity<SouOrderItemHis> {

    @TableId("ORDER_ITEM_ID")
    @ApiModelProperty("ID/供应商报价行ID")
    private Long orderItemId;

    /**
     * @see SouProject#getProjectId
     */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID(冗余字段)")
    private Long projectId;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getSouItemId
     */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    /**
     * @see SouVendor#getVendorId
     */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID(冗余字段)")
    private Long vendorId;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder#getOrderId
     */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    @ApiModelProperty("所属单位")
    @TableField("AFFILIATED_UNIT")
    private String affiliatedUnit;

    @ApiModelProperty("投标保证金")
    @TableField("BID_SECURITY")
    private BigDecimal bidSecurity;

    @ApiModelProperty("履约保证金")
    @TableField("PERFORMANCE_BOND")
    private BigDecimal performanceBond;

    @ApiModelProperty("预付款")
    @TableField("ADVANCE_CHARGE")
    private BigDecimal advanceCharge;

    @ApiModelProperty("月约产量")
    @TableField("MONTHLY_PRODUCTION")
    private BigDecimal monthlyProduction;

    @ApiModelProperty("计量单位")
    @TableField("METERING_UNIT")
    private String meteringUnit;

    @ApiModelProperty("起拍价格（元）")
    @TableField("START_PRICE")
    private BigDecimal startPrice;

    @ApiModelProperty("梯次价格（元）")
    @TableField("ECHELON_PRICE")
    private BigDecimal echelonPrice;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder#getRound
     */
    @TableField("ROUND")
    @ApiModelProperty("报价轮次(冗余字段)")
    private Integer round;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder#getOrderStatus
     */
    @TableField("ORDER_STATUS")
    @ApiModelProperty("报价单状态(冗余字段)")
    private SouOrderStatusEnum orderStatus;

    /**
     * 如果发生物料变更，则可能变为无效
     * 只有有效的报价行，才能入围/淘汰、中标/落标
     * <p>
     * PS: 物料更新操作，不允许发生在评标阶段，
     * 一般只会出现在报名、报价、发起新一轮时
     */
    @TableField("IS_VALID")
    @ApiModelProperty("报价行是否有效")
    private Enable isValid;

    // ---------------------------------------------------------- 冗余物料信息 ----------------------------------------------------------
    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getItemGroup
     */
    @TableField("ITEM_GROUP")
    @ApiModelProperty("物料组合(冗余字段)")
    private String itemGroup;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getNoCodeItem
     */
    @TableField("NO_CODE_ITEM")
    @ApiModelProperty("是否无料号物料")
    private Enable noCodeItem;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getItemId
     */
    @TableField("ITEM_ID")
    @ApiModelProperty("物料ID")
    private Long itemId;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getItemCode
     */
    @TableField("ITEM_CODE")
    @ApiModelProperty("物料编码")
    private String itemCode;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getItemDesc
     */
    @TableField("ITEM_DESC")
    @ApiModelProperty("物料名称")
    private String itemDesc;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getUnit
     */
    @TableField("UNIT")
    @ApiModelProperty("单位")
    private String unit;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getCategoryId
     */
    @TableField("CATEGORY_ID")
    @ApiModelProperty("品类ID")
    private Long categoryId;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getCategoryCode
     */
    @TableField("CATEGORY_CODE")
    @ApiModelProperty("品类编码")
    private String categoryCode;

    /**
     * @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getCategoryName
     */
    @TableField("CATEGORY_NAME")
    @ApiModelProperty("品类名称")
    private String categoryName;

    /**
     * @see SouItem#getRequireQuantity
     */
    @TableField("REQUIRE_QUANTITY")
    @ApiModelProperty("需求数量")
    private BigDecimal requireQuantity;

    /**
     * @see SouOrder#getIsProxy
     */
    @TableField("IS_PROXY")
    @ApiModelProperty("是否代理报价")
    private Enable isProxy;

    /** 报价信息 */
    @TableField("ORDER_CURRENCY")
    @ApiModelProperty("报价币种")
    private String orderCurrency;

    @TableField("TAX_KEY")
    @ApiModelProperty("税率编码")
    private String taxKey;

    @TableField("TAX_RATE")
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    @TableField("ORDER_NOTAX_PRICE")
    @ApiModelProperty("原币未税单价")
    private BigDecimal orderNotaxPrice;

    @TableField("ORDER_TAX_PRICE")
    @ApiModelProperty("原币含税单价")
    private BigDecimal orderTaxPrice;

    @TableField("ORDER_NOW_PRICE")
    @ApiModelProperty("本次报价金额")
    private BigDecimal orderNowPrice;

    @TableField("PRICE_START_TIME")
    @ApiModelProperty("价格有效期从")
    private Date priceStartTime;

    @TableField("PRICE_END_TIME")
    @ApiModelProperty("价格有效期到")
    private Date priceEndTime;

    @TableField("ORDER_REMARK")
    @ApiModelProperty("报价备注")
    private String orderRemark;

    @TableField("SELECT_REMARK")
    @ApiModelProperty("中标备注")
    private String bidRemark;

    /** 冗余价格信息 */
    @TableField("STANDARD_NOTAX_PRICE")
    @ApiModelProperty("本币未税单价")
    private BigDecimal standardNotaxPrice;

    @TableField("STANDARD_TAX_PRICE")
    @ApiModelProperty("本币含税单价")
    private BigDecimal standardTaxPrice;

    @TableField("STANDARD_NOTAX_MIN_PRICE")
    @ApiModelProperty("本币未税最低单价")
    private BigDecimal standardNotaxMinPrice;

    @TableField("STANDARD_TAX_MIN_PRICE")
    @ApiModelProperty("本币含税最低单价")
    private BigDecimal standardTaxMinPrice;

    @TableField("STANDARD_NOTAX_MAX_PRICE")
    @ApiModelProperty("本币未税最高单价")
    private BigDecimal standardNotaxMaxPrice;

    @TableField("STANDARD_TAX_MAX_PRICE")
    @ApiModelProperty("本币含税最高单价")
    private BigDecimal standardTaxMaxPrice;

    @TableField("ORDER_NOTAX_GROUP_PRICE")
    @ApiModelProperty("原币组合未税总价")
    private BigDecimal orderNotaxGroupPrice;

    @TableField("ORDER_TAX_GROUP_PRICE")
    @ApiModelProperty("原币组合含税总价")
    private BigDecimal orderTaxGroupPrice;

    @TableField("STANDARD_NOTAX_GROUP_PRICE")
    @ApiModelProperty("本币组合未税总价")
    private BigDecimal standardNotaxGroupPrice;

    @TableField("STANDARD_TAX_GROUP_PRICE")
    @ApiModelProperty("本币组合含税总价")
    private BigDecimal standardTaxGroupPrice;

    /** 评选信息 */
    @TableField("RANKING")
    @ApiModelProperty("排名")
    private Integer ranking;

    @TableField("WIN_STATUS")
    @ApiModelProperty("本轮入围情况")
    private SouWinStatusEnum winStatus;

    @TableField("SELECT_STATUS")
    @ApiModelProperty("评选结果")
    private SouSelectStatusEnum selectStatus;

    @TableField("WIN_AMOUNT")
    @ApiModelProperty("中标数量")
    private BigDecimal winAmount;

    @TableField("PRICE_SCORE")
    @ApiModelProperty("价格得分")
    private BigDecimal priceScore;

    @TableField("TECH_SCORE")
    @ApiModelProperty("技术得分")
    private BigDecimal techScore;

    @TableField("PERFORMANCE_SCORE")
    @ApiModelProperty("绩效得分")
    private BigDecimal performanceScore;

    @TableField("COMPOSITE_SCORE")
    @ApiModelProperty("综合得分")
    private BigDecimal compositeScore;


    @TableField("ORDER_ROUND")
    @ApiModelProperty("报价次数")
    private BigDecimal order_round;


}
