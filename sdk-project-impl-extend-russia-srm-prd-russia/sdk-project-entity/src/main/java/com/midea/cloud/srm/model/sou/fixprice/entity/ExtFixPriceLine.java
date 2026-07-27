package com.midea.cloud.srm.model.sou.fixprice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceLineStatusEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPricePaymentMethodEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单单 - 物料明细
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_fix_price_line")
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceLine extends BaseEntity<ExtFixPriceLine> {

    @TableId("FIX_PRICE_LINE_ID")
    @ApiModelProperty("ID")
    private Long fixPriceLineId;

    /** @see ExtFixPriceHead#getFixPriceHeadId */
    @TableField("FIX_PRICE_HEAD_ID")
    @ApiModelProperty("定价单ID")
    private Long fixPriceHeadId;

    /** @see ExtFixPriceHead#getFixPriceNo */
    @TableField("FIX_PRICE_NO")
    @ApiModelProperty("定价单号")
    private String fixPriceNo;

    @TableField("SOURCE_FROM_TYPE")
    @ApiModelProperty("来源类型")
    private ExtFixPriceSourceFromTypeEnum sourceFromType;

    @TableField("SOURCE_FROM_LINE_ID")
    @ApiModelProperty("来源单据明细ID")
    private String sourceFromLineId;

    @TableField("SOURCE_FROM_ID")
    @ApiModelProperty("来源单据ID")
    private String sourceFromId;

    @TableField("SOURCE_FROM_NO")
    @ApiModelProperty("来源单据编号")
    private String sourceFromNo;

    @TableField("REQUIREMENT_LINE_IDS")
    @ApiModelProperty("从询比价过来拿到的关联需求池ID集合(逗号隔开 - 仅来源类型为询比价才有)")
    private String requirementLineIds;

    @TableField("ORG_OU_ID")
    @ApiModelProperty("业务实体ID(申请单位)")
    private Long orgOuId;

    @TableField("ORG_OU_CODE")
    @ApiModelProperty("业务实体编码(申请单位)")
    private String orgOuCode;

    @TableField("ORG_OU_NAME")
    @ApiModelProperty("业务实体名称(申请单位)")
    private String orgOuName;

    @TableField("ITEM_ID")
    @ApiModelProperty("物料ID")
    private Long itemId;

    @TableField("ITEM_CODE")
    @ApiModelProperty("物料编码")
    private String itemCode;

    @TableField("ITEM_DESC")
    @ApiModelProperty("物料名称")
    private String itemDesc;
    /**
     * 来源采购需求
     */
    @TableField("EXT_MATERIAL_MODEL")
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    @TableField("UNIT")
    @ApiModelProperty("单位")
    private String unit;

    @TableField("QUANTITY")
    @ApiModelProperty("数量")
    private BigDecimal quantity;

    /**
     * 来源采购需求
     */
    @TableField("BRAND")
    @ApiModelProperty("品牌")
    private String brand;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see ExtFixPricePaymentMethodEnum */
    @TableField("PAYMENT_METHOD")
    @ApiModelProperty("付款方式")
    private String paymentMethod;

    @TableField("PAYMENT_TERM")
    @ApiModelProperty("付款条款")
    private String paymentTerm;

    @TableField("CURRENCY_CODE")
    @ApiModelProperty("币种")
    private String currencyCode;

    @TableField("NOTAX_PRICE")
    @ApiModelProperty("未税单价")
    private BigDecimal notaxPrice;

    @TableField("TAX_KEY")
    @ApiModelProperty("税率编码")
    private String taxKey;

    @TableField("TAX_RATE")
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    @TableField("NOTAX_TOTAL_PRICE")
    @ApiModelProperty("未税总价")
    private BigDecimal notaxTotalPrice;
    /**
     * 采购需求那边的最低价供应商
     */
    @TableField("LATEST_MIN_NOTAX_PRICE")
    @ApiModelProperty("近期最低价格(未税)")
    private BigDecimal latestMinNotaxPrice;

    /**
     * 最低价跟当前价的比较
     */
    @TableField("PRICE_FLOAT_SCALE")
    @ApiModelProperty("浮动比例")
    private BigDecimal priceFloatScale;

    /**
     * 采购需求那边的最低价供应商
     */
    @TableField("LATEST_MIN_VENDOR_ID")
    @ApiModelProperty("近期最低供应商ID")
    private Long latestMinVendorId;

    @TableField("LATEST_MIN_VENDOR_CODE")
    @ApiModelProperty("近期最低供应商编码")
    private String latestMinVendorCode;

    @TableField("LATEST_MIN_VENDOR_NAME")
    @ApiModelProperty("近期最低供应商名称")
    private String latestMinVendorName;

    /**
     * 采购需求那边的最低价的品牌
     */
    @TableField("LATEST_MIN_BRAND")
    @ApiModelProperty("近期最低价品牌")
    private String latestMinBrand;

    /** @see ExtInqSouOrderItem#getExtWinReason */
    @TableField("EXT_WIN_REASON")
    @ApiModelProperty("中标原因")
    private String extWinReason;

    /**  询价才有 @see SouOrderItem#getOrderRemark */
    @TableField("ORDER_REMARK")
    @ApiModelProperty("供应商备注")
    private String orderRemark;

    /** @see ExtInqSouOrderItem#getSpecialPaymentRemark */
    @TableField("SPECIAL_PAYMENT_REMARK")
    @ApiModelProperty("特殊付款说明")
    private String specialPaymentRemark;

    @TableField("ADVANCE_PAYMENT_REMARK")
    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;
    /** 供货周期 -- 询价 */
    @TableField("EXT_LEAD_TIME")
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    /** 采购员账号 -- 询价 */
    @TableField("BUYER_USER_NAME")
    @ApiModelProperty("采购员账号")
    private String buyerUsername;

    /** 采购员昵称 -- 询价 */
    @TableField("BUYER_NICKNAME")
    @ApiModelProperty("采购员昵称")
    private String buyerNickname;

    /** RequirementHead#ceeaPrType (application_form_type) */
    @TableField("APPLY_TYPE")
    @ApiModelProperty("申请类型")
    private String applyType;

    /** 质保期 -- 询价 */
    @TableField("EXT_WARRANTY_PERIOD")
    @ApiModelProperty("质保期")
    private Integer extWarrantyPeriod;

    /** 预估单价 -- 采购需求 */
    @TableField("EXT_PREDICT_PRICE")
    @ApiModelProperty("预估单价")
    private BigDecimal extPredictPrice;

    /** 预估总金额 -- 采购需求 */
    @TableField("EXT_PREDICT_AMOUNT")
    @ApiModelProperty("预估总价")
    private BigDecimal extPredictAmount;

    /** 购买类型 -- 采购需求 */
    @TableField("EXT_BUY_TYPE")
    @ApiModelProperty("购买类型(PR_BUY_TYPE)")
    private String extBuyType;

    /** 审批状态(通过/不通过) */
    @TableField("FIX_PRICE_LINE_STATUS")
    @ApiModelProperty("审批状态")
    private ExtFixPriceLineStatusEnum fixPriceLineStatus;

    @TableField("HAS_CLOSED")
    @ApiModelProperty("是否关闭")
    private Enable hasClosed;

    @TableField("CLOSE_REASON")
    @ApiModelProperty("关闭原因")
    private String closeReason;

    @TableField("HAS_CANCEL")
    @ApiModelProperty("是否取消")
    private Enable hasCancel;

    @TableField("CANCEL_REASON")
    @ApiModelProperty("取消原因")
    private String cancelReason;

    @TableField("HAS_SIGNED_CONTRACT")
    @ApiModelProperty("是否签订合同")
    private Enable hasSignedContract;

    @TableField("INVOICE_TYPE")
    @ApiModelProperty("发票类型(EXT_SOU_INQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    @TableField("EXT_ORDER_COUNT")
    @ApiModelProperty("报价次数")
    private Integer extOrderCount;

    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
