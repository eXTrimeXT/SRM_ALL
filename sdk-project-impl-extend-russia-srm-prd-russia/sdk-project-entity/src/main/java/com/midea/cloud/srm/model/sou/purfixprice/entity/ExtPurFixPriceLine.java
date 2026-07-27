package com.midea.cloud.srm.model.sou.purfixprice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purfix_price_line")
@EqualsAndHashCode(callSuper = true)
public class ExtPurFixPriceLine extends BaseEntity<ExtPurFixPriceLine> {

    @TableId("PUR_FIX_PRICE_LINE_ID")
    @ApiModelProperty("ID")
    private Long purFixPriceLineId;

    /** @see ExtPurFixPriceHead#getPurFixPriceHeadId */
    @TableField("PUR_FIX_PRICE_HEAD_ID")
    @ApiModelProperty("定价ID")
    private Long purFixPriceHeadId;

    /** @see SouOrderItem#getRound */
    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    /** @see SouOrderItem#getItemId */
    @TableField("ITEM_ID")
    @ApiModelProperty("物料ID")
    private Long itemId;

    /** @see SouOrderItem#getItemCode */
    @TableField("ITEM_CODE")
    @ApiModelProperty("物料编码")
    private String itemCode;

    /** @see SouOrderItem#getItemDesc */
    @TableField("ITEM_DESC")
    @ApiModelProperty("物料名称")
    private String itemDesc;

    /** @see ExtPurInqSouItem#getBrand */
    @TableField("BRAND")
    @ApiModelProperty("品牌")
    private String brand;

    /** @see ExtPurInqSouItem#getArea */
    @TableField("AREA")
    @ApiModelProperty("供货区域")
    private String area;

    /** @see ExtPurInqSouItem#getModel */
    @TableField("MODEL")
    @ApiModelProperty("规格型号")
    private String model;

    /** @see SouOrderItem#getUnit */
    @TableField("UNIT")
    @ApiModelProperty("单位")
    private String unit;

    /** @see SouOrderItem#getRequireQuantity */
    @TableField("REQUIRE_QUANTITY")
    @ApiModelProperty("需求数量")
    private BigDecimal requireQuantity;

    /** @see SouItem#getRemark */
    @TableField("REMARK")
    @ApiModelProperty("备注")
    private String remark;

    /** @see SouOrderItem#getStandardNotaxPrice */
    @TableField("NOTAX_PRICE")
    @ApiModelProperty("未税单价")
    private BigDecimal notaxPrice;

    /** @see SouOrderItem#getStandardTaxPrice */
    @TableField("TAX_PRICE")
    @ApiModelProperty("含税单价")
    private BigDecimal taxPrice;

    /** @see SouOrderItem#getTaxKey */
    @TableField("TAX_KEY")
    @ApiModelProperty("税率编码")
    private String taxKey;

    /** @see SouOrderItem#getTaxRate */
    @TableField("TAX_RATE")
    @ApiModelProperty("税率值")
    private BigDecimal taxRate;

    /** @see ExtPurInqSouOrderItem#getExtWarrantyPeriod */
    @TableField("LEAD_TIME")
    @ApiModelProperty("质保期")
    private Integer extWarrantyPeriod;

    // -----------------------------------------------------------------------------------------------------------------
    /** @see SouOrderItem#getVendorId */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    // -----------------------------------------------------------------------------------------------------------------
    /** @see SouOrderItem#getProjectId */
    @TableField("SOU_PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long souProjectId;

    /** @see SouOrderItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("寻源物料需求ID")
    private Long souItemId;

    /** @see SouOrderItem#getOrderId */
    @TableField("SOU_ORDER_ID")
    @ApiModelProperty("寻源报价单ID")
    private Long souOrderId;

    /** @see SouOrderItem#getOrderItemId */
    @TableField("SOU_ORDER_ITEM_ID")
    @ApiModelProperty("寻源报价明细ID")
    private Long souOrderItemId;

    @TableField("FIX_PRICE_STATUS")
    @ApiModelProperty("单据状态")
    private ExtPurFixPriceStatusEnum fixPriceStatus;

    @TableField("SIGN_CONTRACT_FLAG")
    @ApiModelProperty("是否已签订合同")
    private Enable signContractFlag;




}
