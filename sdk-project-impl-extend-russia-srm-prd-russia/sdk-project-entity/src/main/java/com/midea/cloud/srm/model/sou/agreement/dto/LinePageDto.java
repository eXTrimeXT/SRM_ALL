package com.midea.cloud.srm.model.sou.agreement.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LinePageDto extends SccSouJcAgreement {

    @ApiModelProperty("协议信息id")
    @TableId("AGREEMENT_INFO_ID")
    private Long agreementInfoId;

    @ApiModelProperty("协议id")
    @TableField("AGREEMENT_ID")
    private Long agreementId;

    @ApiModelProperty("物料行号")
    @TableField("MATERIAL_LINE")
    private String materialLine;

    @ApiModelProperty("物料id")
    @TableField("MATERIAL_ID")
    private Long materialId;

    @ApiModelProperty("物料编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;

    @ApiModelProperty("物料名称")
    @TableField("MATERIAL_NAME")
    private String materialName;

   /* @ApiModelProperty("商品分类")
    @TableField("GOODS_TYPE")
    private String goodsType;*/

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
    private String priceTax;

    @ApiModelProperty("税率")
    @TableField("TAX_RATE")
    private String taxRate;

    @ApiModelProperty("含税单价(系统计算,数值,未税4位)")
    @TableField("RATE_PRICE")
    private String ratePrice;

    @ApiModelProperty("参考价(参考价不能与未税单价或含税单价一致,数值，未税4位)")
    @TableField("REFERENCE_PRICE")
    private String referencePrice;

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
    private String isTieredPricing;

    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;

    @ApiModelProperty("商城id")
    @TableField("CATALOG_ON_SHELVES_ID")
    private Long catalogOnShelvesId;

    private List<DictItemDTO> areaList;
    @ApiModelProperty("物料类型")
    @TableField(exist = false)
    private String materialType;
}
