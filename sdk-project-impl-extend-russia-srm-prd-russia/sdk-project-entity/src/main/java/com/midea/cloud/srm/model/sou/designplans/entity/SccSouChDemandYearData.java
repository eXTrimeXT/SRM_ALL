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
import java.time.LocalDate;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采台账-需求信息-拉取数据")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_year_data")
public class SccSouChDemandYearData extends BaseEntity<SccSouChDemandYearData> {

    @ApiModelProperty("拉取的数据id")
    @TableId("YEAR_ID")
    private Long yearId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("类型：1.上年数据，2.上上年数据，3.需求合并数据")
    @TableField("TYPE")
    private Integer type;


    @ApiModelProperty("四级品类id")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty("区域id")
    @TableField("AREA_ID")
    private Long areaId;
    @ApiModelProperty("区域编码")
    @TableField("AREA_CODE")
    private String areaCode;
    @ApiModelProperty("区域名称")
    @TableField("AREA_NAME")
    private String areaName;

    @ApiModelProperty("组织id")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;
    @ApiModelProperty("组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;
    @ApiModelProperty("组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;

    @ApiModelProperty("物资id")
    @TableField("MATERIAL_ID")
    private Long materialId;
    @ApiModelProperty("物资编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;
    @ApiModelProperty("物资名称")
    @TableField("MATERIAL_NAME")
    private String materialName;

    @ApiModelProperty("规格型号")
    @TableField("MODEL")
    private String model;

    @ApiModelProperty("计量单位")
    @TableField("UNIT")
    private String unit;
    @ApiModelProperty("计量单位编码")
    @TableField("UNIT_CODE")
    private String unitCode;

    @ApiModelProperty("订单数量")
    @TableField("ORDER_NUM")
    private BigDecimal orderNum;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("未税单价")
    @TableField("PRICE_TAX")
    private BigDecimal priceTax;

    @ApiModelProperty("税率编码")
    @TableField("TAX_RATE_CODE")
    private String taxRateCode;
    @ApiModelProperty("税率%")
    @TableField("TAX_RATE")
    private BigDecimal taxRate;

    @ApiModelProperty("含税单价")
    @TableField("RATE_PRICE")
    private BigDecimal ratePrice;

    @ApiModelProperty("金额")
    @TableField("MONEY_AMOUNT")
    private BigDecimal moneyAmount;

    @ApiModelProperty("价税合计")
    @TableField("PRICE_TOTAL")
    private BigDecimal priceTotal;

    @ApiModelProperty("供应商id")
    @TableField("SUP_ID")
    private Long supId;
    @ApiModelProperty("供应商编码")
    @TableField("SUP_CODE")
    private String supCode;
    @ApiModelProperty("供应商名称")
    @TableField("SUP_NAME")
    private String supName;

    @ApiModelProperty("一级分类id")
    @TableField("ONE_TYPE_ID")
    private Long oneTypeId;
    @ApiModelProperty("一级分类编码")
    @TableField("ONE_TYPE_CODE")
    private String oneTypeCode;
    @ApiModelProperty("一级分类名称")
    @TableField("ONE_TYPE_NAME")
    private String oneTypeName;

    @ApiModelProperty("二级分类id")
    @TableField("TWO_TYPE_ID")
    private Long twoTypeId;
    @ApiModelProperty("二级分类编码")
    @TableField("TWO_TYPE_CODE")
    private String twoTypeCode;
    @ApiModelProperty("二级分类名称")
    @TableField("TWO_TYPE_NAME")
    private String twoTypeName;

    @ApiModelProperty("订单日期")
    @TableField("ORDER_DATE")
    private LocalDate orderDate;

    @ApiModelProperty("采购员id")
    @TableField("BUY_USER_ID")
    private Long buyUserId;
    @ApiModelProperty("采购员名字")
    @TableField("BUY_USER_NAME")
    private String buyUserName;

    @ApiModelProperty("合同编号id")
    @TableField("CONTRACT_ID")
    private Long contractId;
    @ApiModelProperty("合同编号")
    @TableField("CONTRACT_CODE")
    private String contractCode;
    @ApiModelProperty("合同序号")
    @TableField("CONTRACT_NUM")
    private String contractNum;

    @ApiModelProperty("购买类型")
    @TableField("BUY_TYPE")
    private String buyType;

    @ApiModelProperty("创建单位(操作人员单位)")
    @TableField("CREATE_UNIT_ID")
    private Long createUnitId;
    @ApiModelProperty("创建单位(操作人员单位)")
    @TableField("CREATE_UNIT_CODE")
    private String createUnitCode;
    @ApiModelProperty("创建单位(操作人员单位)")
    @TableField("CREATE_UNIT_NAME")
    private String createUnitName;

    @ApiModelProperty("数据来源")
    @TableField("DATA_SOURCE")
    private String dataSource;

    @ApiModelProperty("历史供应商编码")
    @TableField("HISTORY_VENDOR_CODE")
    private String historyVendorCode;

    @ApiModelProperty("历史未税单价")
    @TableField("HISTORY_PRICE_TAX")
    private BigDecimal historyPriceTax;

    @ApiModelProperty("历史含税单价")
    @TableField("HISTORY_RATE_PRICE")
    private BigDecimal historyRatePrice;

}
