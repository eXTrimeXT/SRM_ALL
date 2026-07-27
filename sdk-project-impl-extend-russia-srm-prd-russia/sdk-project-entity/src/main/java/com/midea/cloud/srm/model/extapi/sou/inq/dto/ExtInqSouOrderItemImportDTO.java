package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ExtInqSouOrderItemImportDTO {

    @ExcelProperty("ID(请勿改动)")
    private String souItemId;

    @ExcelProperty("序号")
    private String index;

    @ExcelProperty("轮次")
    private String round;

    @ExcelProperty("业务实体")
    private String orgOuName;

    @ExcelProperty("区域")
    @ExtInqExcelPropertyValues(dictCode = "REGION")
    private String extAreaName;

    @ExcelProperty("是否无料号寻源")
    @ExtInqExcelPropertyValues(dictCode = "YES_OR_NO")
    private String noCodeItem;

    @ExcelProperty("物料编码")
    private String itemCode;

    @ExcelProperty("物料名称")
    private String itemDesc;

    @ExcelProperty("物料分类")
    private String categoryName;

    @ExcelProperty("规格型号")
    private String extMaterialModel;

    @ExcelProperty("数量")
    private String requireQuantity;

    @ExcelProperty("基本计量单位")
    @ExtInqExcelPropertyValues(useUnit = true)
    private String unit;

    @ExcelProperty("品牌")
    private String extBrand;

    @ExcelProperty("*税率")
    @ExtInqExcelPropertyValues(useTax = true)
    private String taxKey;

    @ExcelProperty("*发票类型")
    @ExtInqExcelPropertyValues(dictCode = "EXT_SOU_INQ_ORDER_INVOICE_TYPE")
    private String invoiceType;

    @ExcelProperty("*未税单价")
    private String orderNotaxPrice;

    @ExcelProperty("含税单价")
    private String orderTaxPrice;

    @ExcelProperty("价税合计")
    private String priceTaxTotal;

    @ExcelProperty("*预付款说明")
    @ExtInqExcelPropertyValues(dictCode = "YES_OR_NO")
    private String advancePaymentRemark;

    @ExcelProperty("是否阶梯报价")
    @ExtInqExcelPropertyValues(dictCode = "YES_OR_NO")
    private String isLadder;

    @ExcelProperty("到货周期(自然日)")
    private String extLeadTime;

    @ExcelProperty("质保期(自然日)")
    private String extWarrantyPeriod;

    @ExcelProperty("备注")
    private String orderRemark;

    @ExcelProperty("需求备注")
    private String requirementRemark;

    @ExcelProperty("错误信息")
    private String errMsg;

}
