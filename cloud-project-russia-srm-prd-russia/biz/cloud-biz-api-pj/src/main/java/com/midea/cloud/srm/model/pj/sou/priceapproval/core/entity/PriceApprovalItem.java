package com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.model.entity.ExtPriceApprovalItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 价格审批单 - 中标行
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/18
 */
@Data
@ApiModel("价格审批单-中标行")
@TableName("scc_price_approval_item")
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalItem extends ExtPriceApprovalItem {

    @TableId("APPROVAL_ITEM_ID")
    @ApiModelProperty("ID")
    private Long approvalItemId;

    /** @see PriceApproval#getApprovalId */
    @TableField("APPROVAL_ID")
    @ApiModelProperty("价格审批单ID")
    private Long approvalId;

    /**
     * 组织信息
     */
    @TableField("ORG_OU_ID")
    @ApiModelProperty("业务实体ID")
    private Long orgOuId;

    @TableField("ORG_OU_CODE")
    @ApiModelProperty("业务实体编码")
    private String orgOuCode;

    @TableField("ORG_OU_NAME")
    @ApiModelProperty("业务实体名称")
    private String orgOuName;

    @TableField("ORG_INV_ID")
    @ApiModelProperty("库存组织ID")
    private Long orgInvId;

    @TableField("ORG_INV_CODE")
    @ApiModelProperty("库存组织编码")
    private String orgInvCode;

    @TableField("ORG_INV_NAME")
    @ApiModelProperty("库存组织名称")
    private String orgInvName;

    /**
     * 供应商信息
     */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * 物料信息
     */
    @TableField("NO_CODE_ITEM")
    @ApiModelProperty("是否无编码物料(Y/N)")
    private Enable noCodeItem;

    @TableField("HAS_REFRESH_NO_CODE_ITEM")
    @ApiModelProperty("是否已将无编码物料刷新成具体的实际物料(Y/N)")
    private Enable hasRefreshNoCodeItem;

    @TableField("ITEM_ID")
    @ApiModelProperty("物料ID")
    private Long itemId;

    @TableField("ITEM_CODE")
    @ApiModelProperty("物料编码")
    private String itemCode;

    @TableField("ITEM_DESC")
    @ApiModelProperty("物料名称")
    private String itemDesc;

    @TableField("CATEGORY_ID")
    @ApiModelProperty("物料小类ID(品类)")
    private Long categoryId;

    @TableField("CATEGORY_CODE")
    @ApiModelProperty("物料小类编码(品类)")
    private String categoryCode;

    @TableField("CATEGORY_NAME")
    @ApiModelProperty("物料小类名称(品类)")
    private String categoryName;

    @TableField("UNIT")
    @ApiModelProperty("物料单位")
    private String unit;

    @TableField("LADDER_PRICE")
    @ApiModelProperty("是否阶梯报价(Y/N)")
    private Enable ladderPrice;

    /**
     * 币种/价格
     */
    @TableField("ORDER_CURRENCY")
    @ApiModelProperty("报价币种")
    private String orderCurrency;

    @TableField("ORDER_PRICE_PRECISION")
    @ApiModelProperty("原币报价精确度")
    private Integer orderPricePrecision;

    @TableField("TAX_KEY")
    @ApiModelProperty("税率编码")
    private String taxKey;

    @TableField("TAX_RATE")
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    /** @see PurchaseExchangeRate#getExchangeRateId */
    @TableField("EXCHANGE_RATE_ID")
    @ApiModelProperty("汇率ID")
    private Long exchangeRateId;

    /** @see PurchaseExchangeRate#getPriceTax */
    @TableField("PRICE_TAX")
    @ApiModelProperty("汇率")
    private BigDecimal priceTax;

    @TableField("ORDER_NOTAX_PRICE")
    @ApiModelProperty("原币未税单价")
    private BigDecimal orderNotaxPrice;

    @TableField("ORDER_TAX_PRICE")
    @ApiModelProperty("原币含税单价")
    private BigDecimal orderTaxPrice;

    @TableField("STANDARD_NOTAX_PRICE")
    @ApiModelProperty("本币未税单价")
    private BigDecimal standardNotaxPrice;

    @TableField("STANDARD_TAX_PRICE")
    @ApiModelProperty("本币含税单价")
    private BigDecimal standardTaxPrice;

    /**
     * 其他信息
     */
    @TableField("PRICE_TYPE")
    @ApiModelProperty("价格类型(字典: DMAND_LINE_TYPE)")
    private String priceType;

    @TableField("ARRIVAL_PLACE")
    @ApiModelProperty("到货地点(交货地点)")
    private String arrivalPlace;

    @TableField("LEAD_TIME")
    @ApiModelProperty("供货周期(自然天)")
    private BigDecimal leadTime;

    @TableField("PRICE_START_TIME")
    @ApiModelProperty("价格有效期从")
    private LocalDate priceStartTime;

    @TableField("PRICE_END_TIME")
    @ApiModelProperty("价格有效期到")
    private LocalDate priceEndTime;

    @TableField("MOQ")
    @ApiModelProperty("最小起订量")
    private BigDecimal moq;

    @TableField("TRADE_TERM")
    @ApiModelProperty("贸易术语(字典: trade_clause)")
    private String tradeTerm;

    @TableField("WARRANTY_PERIOD")
    @ApiModelProperty("保质期")
    private BigDecimal warrantyPeriod;

    @TableField("NEED_NUM")
    @ApiModelProperty("需求数量")
    private BigDecimal needNum;

    @TableField("WIN_NUM")
    @ApiModelProperty("中标数量")
    private BigDecimal winNum;

    @TableField("REMARK")
    @ApiModelProperty("备注")
    private String remark;

    // --------------------------------------------------------- 来源信息 -----------------------------------------------------
    /** @see PriceApproval#getSourceFromType */
    @TableField("SOURCE_FROM_TYPE")
    @ApiModelProperty("来源单据类型")
    private String sourceFromType;

    /** @see PriceApproval#getSourceFromId */
    @TableField("SOURCE_FROM_ID")
    @ApiModelProperty("来源单据ID")
    private String sourceFromId;

    /** @see PriceApproval#getSourceFromNo */
    @TableField("SOURCE_FROM_NO")
    @ApiModelProperty("来源单据编号")
    private String sourceFromNo;

    /** @see PriceApproval#getSourceFromName */
    @TableField("SOURCE_FROM_NAME")
    @ApiModelProperty("来源单据名称")
    private String sourceFromName;

    @TableField("SOURCE_FROM_LINE_ID")
    @ApiModelProperty("来源单据行ID")
    private String sourceFromLineId;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
