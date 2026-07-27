package com.midea.cloud.srm.model.supcooperate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_sc_order_price_trends_line")
@EqualsAndHashCode(callSuper = true)
public class SccScOrderPriceTrendsLine extends BaseEntity<SccScOrderPriceTrendsLine> {

    @ApiModelProperty("价格趋势id")
    @TableId("PRICE_TRENDS_LINE_ID")
    private Long priceTrendsLineId;

    @ApiModelProperty("价格趋势id")
    @TableField("PRICE_TRENDS_ID")
    private Long priceTrendsId;

    @ApiModelProperty("物料id")
    @TableField("MATERIAL_ID")
    private Long materialId;

    @ApiModelProperty("物料编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;

    @ApiModelProperty("物料名称")
    @TableField("MATERIAL_NAME")
    private String materialName;

    @ApiModelProperty("物料描述")
    @TableField("MATERIAL_DESCRIBE")
    private String materialDescribe;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("区域")
    @TableField("AREA_CODE")
    private String areaCode;

    @ApiModelProperty("业务实体id")
    @TableField("ORG_ID")
    private Long orgId;

    @ApiModelProperty("业务实体编码")
    @TableField("ORG_CODE")
    private String orgCode;

    @ApiModelProperty("业务实体名称")
    @TableField("ORG_NAME")
    private String orgName;

    @ApiModelProperty("未税价格")
    @TableField("NO_TAX_PRICE")
    private BigDecimal noTaxPrice;

    @ApiModelProperty("税率")
    @TableField("TAX_RATE")
    private BigDecimal taxPrice;

    @ApiModelProperty("到货周期")
    @TableField("LEAD_TIME")
    private LocalDate leadTime;

    @ApiModelProperty("供应商id")
    @TableField("SUP_ID")
    private Long supId;

    @ApiModelProperty("供应商编码")
    @TableField("SUP_CODE")
    private String supCode;

    @ApiModelProperty("供应商名称")
    @TableField("SUP_NAME")
    private String supName;

    @ApiModelProperty("供应商联系方式")
    @TableField("SUP_TEL")
    private String supTel;

    @ApiModelProperty("订单日期")
    @TableField("ORDER_DATE")
    private Date orderDate;

    @ApiModelProperty("年月")
    @TableField(exist = false)
    private String ym;

    @ApiModelProperty("物料区域")
    @TableField(exist = false)
    private String materialArea;

}
