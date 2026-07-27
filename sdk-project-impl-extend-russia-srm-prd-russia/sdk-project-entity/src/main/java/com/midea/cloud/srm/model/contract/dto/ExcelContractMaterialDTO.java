package com.midea.cloud.srm.model.contract.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExcelContractMaterialDTO {



    @ApiModelProperty("交货地点")
    @ExcelProperty("收货地点名称")
    private String tradingLocations;

    @ApiModelProperty("物料ID")
    @ExcelProperty("物料ID")
    private Long materialId;

    @ExcelProperty("物料编码")
    @ApiModelProperty("物料编码")
    private String materialCode;

    @ApiModelProperty("物料名称")
    @ExcelProperty("物料名称*")
    private String materialName;

    @ExcelProperty("品类编码")
    @ApiModelProperty("品类编码")
    private String categoryCode;

    @ExcelProperty("品类名称")
    @ApiModelProperty("品类名称")
    private String categoryName;

    @ApiModelProperty("品类ID")
    private Long categoryId;

    @ApiModelProperty("未税单价*")
    @ExcelProperty("未税单价*")
    private BigDecimal untaxedPrice;

    @ApiModelProperty("含税单价")
    @ExcelProperty("含税单价*")
    private BigDecimal taxedPrice;


    @ApiModelProperty("合同数量")
    @ExcelProperty("数量*")
    private BigDecimal contractQuantity;


    @ApiModelProperty("含税金额")
    @ExcelProperty("含税金额*")
    private BigDecimal amount;

    @ApiModelProperty("未税金额")
    @ExcelProperty("未税金额")
    private BigDecimal unAmount;


    @ApiModelProperty("单位")
    @ExcelProperty("单位")
    private String unitName;


    @ApiModelProperty("*税率")
    private BigDecimal taxRate;

    private String taxKey;

    @ApiModelProperty("")
    @ExcelProperty("*税率")
    private String taxRateDesc;


    @ApiModelProperty("税额")
    @ExcelProperty("税额")
    private BigDecimal taxQuota;


    @ApiModelProperty("价格执行有效期从")
    @ExcelProperty("价格执行有效期从")
    private Date startDate;


    @ApiModelProperty("价格执行有效期至")
    @ExcelProperty("价格执行有效期至")
    private Date endDate;

    @ApiModelProperty("发票类型")
    @ExcelProperty("发票类型")
    private String extInvoiceTypeName;

    @ApiModelProperty("发票类型")
    private String extInvoiceType;

    @ApiModelProperty("制造商")
    @ExcelProperty("制造商")
    private String manufacturer;

    @ApiModelProperty("质保期（自然日）")
    @ExcelProperty("质保期（自然日）")
    private BigDecimal shelfLife;

    @ApiModelProperty("行备注")
    @ExcelProperty("行备注")
    private String lineRemark;

    @ApiModelProperty("项目编号")
    @ExcelProperty("项目编号")
    private String itemNumber;


    @ApiModelProperty("规格类型")
    @ExcelProperty("规格型号")
    private String specification;

















}
