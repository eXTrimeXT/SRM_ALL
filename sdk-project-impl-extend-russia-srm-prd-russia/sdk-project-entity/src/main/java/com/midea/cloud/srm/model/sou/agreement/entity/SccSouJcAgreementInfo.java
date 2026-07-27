package com.midea.cloud.srm.model.sou.agreement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采协议管理信息")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_jc_agreement_info")
public class SccSouJcAgreementInfo extends BaseEntity<SccSouJcAgreementInfo> {

    @ApiModelProperty("协议信息id")
    @TableId("AGREEMENT_INFO_ID")
    private Long agreementInfoId;

    @ApiModelProperty("协议id")
    @TableField("AGREEMENT_ID")
    private Long agreementId;

    @ApiModelProperty("物料行号")
    @TableField("MATERIAL_LINE")
    private Integer materialLine;

    @ApiModelProperty("物料id")
    @TableField("MATERIAL_ID")
    private Long materialId;
    @ApiModelProperty("物料编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;
    @ApiModelProperty("物料名称")
    @TableField("MATERIAL_NAME")
    private String materialName;

    @ApiModelProperty("商品分类id")
    @TableField("GOODS_TYPE_ID")
    private Long goodsTypeId;
    @ApiModelProperty("商品分类编码")
    @TableField("GOODS_TYPE_CODE")
    private String goodsTypeCode;
    @ApiModelProperty("商品分类名称")
    @TableField("GOODS_TYPE_NAME")
    private String goodsTypeName;

    @ApiModelProperty("规格")
    @TableField("STANDARDS")
    private String standards;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("未税单价(数值,未税2位)")
    @TableField("PRICE_TAX")
    private BigDecimal priceTax;

    @ApiModelProperty("税率")
    @TableField("TAX_RATE")
    private BigDecimal taxRate;

    @ApiModelProperty("含税单价(系统计算,数值,未税4位)")
    @TableField("RATE_PRICE")
    private BigDecimal ratePrice;

    @ApiModelProperty("参考价(参考价不能与未税单价或含税单价一致,数值，未税4位)")
    @TableField("REFERENCE_PRICE")
    private BigDecimal referencePrice;

    @ApiModelProperty("交货周期")
    @TableField("LEAD_TIME")
    private Integer leadTime;

    @ApiModelProperty("质保期")
    @TableField("SELL_BY_DATE")
    private Integer sellByDate;

    @ApiModelProperty("起订量(默认1)")
    @TableField("START_NUM")
    private Integer startNum;

    @ApiModelProperty("整倍起售数量")
    @TableField("MULTIPLE_START_NUM")
    private Integer multipleStartNum;

    @ApiModelProperty("协议行说明")
    @TableField("AGREEMENT_DES")
    private String agreementDes;

    @ApiModelProperty("是否阶梯价1是0否")
    @TableField("IS_TIERED_PRICING")
    private Integer isTieredPricing;

    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;

    @ApiModelProperty("商城id")
    @TableField("CATALOG_ON_SHELVES_ID")
    private Long catalogOnShelvesId;

    @ApiModelProperty("阶梯价")
    @TableField(exist = false)
    private List<SccSouTieredPricing> sccSouTieredPricingList;
    
    @TableField(exist = false)
    private String description;
}
