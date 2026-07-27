package com.midea.cloud.srm.model.extapi.sou.purinq.dto.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqExcelPropertyValues;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class ExtPurInqSouOrderItemImportDTO {

    @ExcelProperty("ID(请勿改动)")
    private String souItemId;

    @ExcelProperty("序号")
    private String index;

    /** @see ExtPurInqSouItem#getArea */
    @ExcelProperty("供货范围")
    private String area;

    /** @see SouOrderItem#getItemCode */
    @ExcelProperty("物资编码")
    private String itemCode;

    /** @see SouOrderItem#getItemDesc */
    @ExcelProperty("物资名称")
    private String itemDesc;

    /** @see ExtPurInqSouItem#getModel */
    @ExcelProperty("规格型号")
    private String model;

    /** @see SouOrderItem#getUnit */
    @ExcelProperty("计量单位")
    @ExtInqExcelPropertyValues(useUnit = true)
    private String unit;

    /** @see SouOrderItem#getRequireQuantity */
    @ExcelProperty("数量")
    private String requireQuantity;

    /** @see ExtPurInqSouItem#getBrand */
    @ExcelProperty("品牌")
    private String brand;

    @ExcelProperty("*税率")
    @ExtInqExcelPropertyValues(useTax = true)
    private String taxKey;

    /** @see ExtPurInqSouOrderItem#getInvoiceType */
    @ExcelProperty("*发票类型")
    @ExtInqExcelPropertyValues(dictCode = "EXT_SOU_PURINQ_ORDER_INVOICE_TYPE")
    private String invoiceType;

    /** @see SouOrderItem#getOrderNotaxPrice */
    @ExcelProperty("*未税单价")
    private String orderNotaxPrice;

    /** @see SouOrderItem#getOrderTaxPrice */
    @ExcelProperty("含税单价")
    private String orderTaxPrice;

    /** @see ExtPurInqSouOrderItem#getPriceTaxTotal */
    @ExcelProperty("价税合计")
    private String priceTaxTotal;

    /** @see SouItem#getIsLadder */
    @ExcelProperty("是否阶梯报价")
    @ExtInqExcelPropertyValues(dictCode = "YES_OR_NO")
    private String isLadder;

    /** @see ExtPurInqSouOrderItem#getExtLeadTime */
    @ExcelProperty("*到货周期(自然日)")
    private String extLeadTime;

    /** @see ExtPurInqSouOrderItem#getExtWarrantyPeriod */
    @ExcelProperty("*质保期(自然日)")
    private String extWarrantyPeriod;

    /** @see SouOrderItem#getOrderRemark */
    @ExcelProperty("*备注")
    private String orderRemark;

    @ExcelProperty("错误信息")
    private String errMsg;

}
