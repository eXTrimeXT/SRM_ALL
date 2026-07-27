package com.midea.cloud.srm.model.supcooperate.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/11
 */
@Data
public class InvoiceNoticeDetailExcel {
   /* @ExcelProperty("序号")
    private Integer invoiceDetailNum;*/


    /**
     * 类型为Receive，excel导出转化为入库 类型为Return，excel导出转化为出库
     */
    @ExcelProperty("入库类型")
    private String type;
    @ExcelProperty("处理日期")
    private String receiveNewDate;
    @ExcelProperty("采购订单号")
    private String orderNumber;



    @ExcelProperty("采购订单行号")
    private Integer lineNum;
    @ExcelProperty("物料编码")
    private String itemCode;
    @ExcelProperty("物料名称")
    private String itemName;

    @ExcelProperty("规格")
    private String extMaterialModel;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("入库数量")
    private BigDecimal receiveNum;
   @ExcelProperty("对账数量")
   private BigDecimal invoiceQuantity;
 @ExcelProperty("未税单价")
 private BigDecimal unitPriceExcludingTax;
 @ExcelProperty("税率")
 private BigDecimal taxRate;
 @ExcelProperty("含税单价")
 private BigDecimal unitPriceContainingTax;
 @ExcelProperty("未税总价")
 private BigDecimal noTaxAmount;
 @ExcelProperty("含税总价")
 private BigDecimal taxAmount;

 @ExcelProperty("币种")
 @TableField("CURRENCY_CODE")
 private String currencyCode;
}
