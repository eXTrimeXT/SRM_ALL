package com.midea.cloud.srm.model.sou.agreement.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ex_liuxy46
 */
@Data
public class ExportJcHtHeadLineData implements Serializable {

    @ExcelProperty(value = "协议信息编码", index = 0)
    private String agreementCode;
    @ExcelProperty(value = "协议信息名称", index = 1)
    private String agreementName;
    @ExcelProperty(value = "版本", index = 2)
    private String changeVersion;
    @ExcelProperty(value = "公司主体", index = 3)
    private String companyName;
    @ExcelProperty(value = "供应商名称", index = 4)
    private String supName;
    @ExcelProperty(value = "有效开始日期", index = 5)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String effectiveStartDate;
    @ExcelProperty(value = "有效结束日期", index = 6)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String effectiveEndDate;
    @ExcelProperty(value = "操作时间", index = 7)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String creationDate;
    @ExcelProperty(value = "采购组织", index = 8)
    private String orgName;
    @ExcelProperty(value = "采购员", index = 9)
    private String buyPersonName;
    @ExcelProperty(value = "操作人", index = 10)
    private String createdFullName;
    @ExcelProperty(value = "状态", index = 11)
    private String agreementStatus;
    @ExcelProperty(value = "发票类型", index = 12)
    private String invoiceType;

    @ExcelProperty(value = "表格行号", index = 13)
    private String materialLine;
    @ExcelProperty(value = "物料编码", index = 14)
    private String materialCode;
    @ExcelProperty(value = "物料名称", index = 15)
    private String materialName;
    @ExcelProperty(value = "商品分类", index = 16)
    private String goodsTypeName;
    @ExcelProperty(value = "规格", index = 17)
    private String standards;
    @ExcelProperty(value = "单位", index = 18)
    private String unit;
    @ExcelProperty(value = "品牌", index = 19)
    private String brand;
    @ExcelProperty(value = "税率", index = 20)
    private String taxRate;
    @ExcelProperty(value = "是否阶梯价", index = 21)
    private String isTieredPricing;
    @ExcelProperty(value = "未税单价", index = 22)
    private String priceTax;
    @ExcelProperty(value = "含税单价", index = 23)
    private String ratePrice;
    @ExcelProperty(value = "参考价", index = 24)
    private String referencePrice;
    @ExcelProperty(value = "交货周期", index = 25)
    private String leadTime;
    @ExcelProperty(value = "质保期", index = 26)
    private String sellByDate;
    @ExcelProperty(value = "起订量", index = 27)
    private String startNum;
    @ExcelProperty(value = "整倍起售数量", index = 28)
    private String multipleStartNum;
    @ExcelProperty(value = "协议行说明", index = 29)
    private String agreementDes;
    @ExcelProperty(value = "供应区域", index = 30)
    private String supplyArea;
}
