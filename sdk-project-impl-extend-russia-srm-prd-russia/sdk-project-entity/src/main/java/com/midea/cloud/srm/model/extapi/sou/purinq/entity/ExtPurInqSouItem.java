package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
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
@TableName("scc_npm_sou_purinq_item")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouItem extends BaseEntity<ExtPurInqSouItem> {

    /** @see SouItem#getSouItemId */
    @TableId("SOU_ITEM_ID")
    @ApiModelProperty("ID")
    private Long souItemId;

    /** @see SouItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("AREA")
    @ApiModelProperty("供货范围")
    private String area;

    @ApiModelProperty("规格型号")
    @TableField("MODEL")
    private String model;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    /** @see SccSouChDemandYearData#getSupId */
    @TableField("DESIGN_VENDOR_ID")
    @ApiModelProperty("历史供应商ID")
    private Long designVendorId;

    /** @see SccSouChDemandYearData#getSupCode */
    @TableField("DESIGN_VENDOR_CODE")
    @ApiModelProperty("历史供应商编码")
    private String designVendorCode;

    /** @see SccSouChDemandYearData#getSupName */
    @TableField("DESIGN_VENDOR_NAME")
    @ApiModelProperty("历史供应商名称")
    private String designVendorName;

    /** @see SccSouChDemandYearData#getPriceTax */
    @TableField("DESIGN_NOTAX_PRICE")
    @ApiModelProperty("历史未税价格")
    private BigDecimal designNotaxPrice;

}
