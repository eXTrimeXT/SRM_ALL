package com.midea.cloud.srm.model.sou.designplans.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @description: 协议明细导入对象
 * @author: 100014337
 * @create: 2023-12-21 14:10
 * @version 1.0
 **/
@Getter
@Setter
public class AgreementExcelDto {
    @ExcelIgnore
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "协议主键")
    @ExcelProperty(value = {"协议主键"}, index = 0)
    private String agreementId;

    @ApiModelProperty(value = "供货范围")
    @ExcelProperty(value = {"供货范围"}, index = 1)
    private String area;

    @ApiModelProperty(value = "物资编码")
    @ExcelProperty(value = {"物资编码"}, index = 2)
    private String itemCode;

    @ApiModelProperty(value = "物资名称")
    @ExcelProperty(value = {"物资名称"}, index = 3)
    private String itemDesc;

    @ApiModelProperty(value = "规格型号")
    @ExcelProperty(value = {"规格型号"}, index = 4)
    private String model;

    @ApiModelProperty(value = "计量单位")
    @ExcelProperty(value = {"计量单位"}, index = 5)
    private String unit;

    @ApiModelProperty(value = "品牌")
    @ExcelProperty(value = {"品牌"}, index = 6)
    private String brand;

    @ApiModelProperty(value = "备注")
    @ExcelProperty(value = {"备注"}, index = 7)
    private String remark;

    @ApiModelProperty(value = "中标供应商")
    @ExcelProperty(value = {"中标供应商"}, index = 8)
    private String vendorName;

    @ApiModelProperty(value = "含税")
    @ExcelProperty(value = {"含税"}, index = 9)
    private BigDecimal taxPrice;

    @ApiModelProperty(value = "未税价格")
    @ExcelProperty(value = {"未税价格"}, index = 10)
    private BigDecimal notaxPrice;
}

