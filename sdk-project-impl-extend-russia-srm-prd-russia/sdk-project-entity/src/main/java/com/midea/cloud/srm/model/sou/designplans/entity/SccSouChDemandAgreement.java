package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @description: 协议明细
 * @author: 100014337
 * @create: 2023-12-20 17:58
 * @version 1.0
 **/
@ApiModel(description = "调价申请-协议明细")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_agreement")
public class SccSouChDemandAgreement extends BaseEntity<SccSouChDemandAnalysis> {

    @ApiModelProperty("协议主键")
    @TableId("AGREEMENT_ID")
    private Long agreementId;

    @ApiModelProperty("关联提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("供货范围")
    @TableField("AREA")
    private String area;

    @ApiModelProperty("物资ID")
    @TableField("ITEM_ID")
    private Long itemId;

    @ApiModelProperty("物资编码")
    @TableField("ITEM_CODE")
    private String itemCode;

    @ApiModelProperty("物资名称")
    @TableField("ITEM_DESC")
    private String itemDesc;

    @ApiModelProperty("规格型号")
    @TableField("MODEL")
    private String model;

    @ApiModelProperty("计量单位")
    @TableField("UNIT")
    private String unit;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("含税单价")
    @TableField("TAX_PRICE")
    private BigDecimal taxPrice;

    @ApiModelProperty("未税价格")
    @TableField("NOTAX_PRICE")
    private BigDecimal notaxPrice;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("项目编码")
    @TableField("PROJECT_CODE")
    private String projectCode;
}

