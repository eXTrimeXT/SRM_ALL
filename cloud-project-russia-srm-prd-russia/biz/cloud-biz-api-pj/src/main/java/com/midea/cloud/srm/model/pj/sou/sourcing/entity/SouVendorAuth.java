package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源.核心表 - 供应商报价权限
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_vendor_auth")
@ApiModel("寻源供应商报价权限")
public class SouVendorAuth extends BaseEntity<SouVendorAuth> {

    @ApiModelProperty("ID")
    @TableId("VENDOR_AUTH_ID")
    private Long vendorAuthId;

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("供应商表ID")
    @TableField("SOU_VENDOR_ID")
    private Long souVendorId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @ApiModelProperty("物料需求ID")
    @TableField("SOU_ITEM_ID")
    private Long souItemId;

    /** @see SouVendor#getVendorId */
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty("是否禁止报价")
    @TableField("FORBID_PRICE")
    private Enable forbidPrice;

    /** 冗余字段 */
    @TableField("ITEM_ID")
    @ApiModelProperty("物料ID(冗余字段)")
    private Long itemId;

    /** @see SouItem#getItemCode */
    @TableField("ITEM_CODE")
    @ApiModelProperty("物料编码(冗余字段)")
    private String itemCode;

    /** @see SouItem#getItemDesc */
    @TableField("ITEM_DESC")
    @ApiModelProperty("物料名称(冗余字段)")
    private String itemDesc;

    /** @see SouItem#getUnit */
    @TableField("UNIT")
    @ApiModelProperty("单位(冗余字段)")
    private String unit;

    /** @see SouItem#getCategoryId */
    @TableField("CATEGORY_ID")
    @ApiModelProperty("品类ID(冗余字段)")
    private Long categoryId;

    /** @see SouItem#getCategoryCode */
    @TableField("CATEGORY_CODE")
    @ApiModelProperty("品类编码(冗余字段)")
    private String categoryCode;

    /** @see SouItem#getCategoryName */
    @TableField("CATEGORY_NAME")
    @ApiModelProperty("品类名称(冗余字段)")
    private String categoryName;

}
