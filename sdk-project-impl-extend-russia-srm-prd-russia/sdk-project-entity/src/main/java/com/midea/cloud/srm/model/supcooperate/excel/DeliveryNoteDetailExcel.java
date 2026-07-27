package com.midea.cloud.srm.model.supcooperate.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqExcelPropertyValues;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/11
 */
@Data
public class DeliveryNoteDetailExcel {

    @ExcelProperty("送货单号")
    private String deliveryNumber;
    @ExcelProperty("送货单行状态")
    @ExtInqExcelPropertyValues(dictCode = "DELIVERY_NOTE_DETAIL_STATUS")
    private String extDetailStatus;
    @ExcelProperty("采购订单编号")
    private String extPurchaserNo;
    @ExcelProperty("订单行号")
    private Long lineNum;
    @ExcelProperty("采购品类")
    private String categoryName;
    @ExcelProperty("物料编码")
    private String materialCode;
    @ExcelProperty("物料名称")
    private String materialName;
    @ExcelProperty("规格型号")
    private String specification;
    @ExcelProperty("品牌")
    private String extBrand;
    @ExcelProperty("订单数量")
    private BigDecimal orderNum;
    @ExcelProperty("剩余未送货数量")
    private BigDecimal numberRemaining;
    @ExcelProperty("本次送货数量")
    private BigDecimal deliveryQuantity;
    @ExcelProperty("该送货单累计到货数量")
    private BigDecimal warehouseQuantity;
    @ExcelProperty("该送货单累计已取消数量")
    private BigDecimal extCancelQty;
    @ExcelProperty("需求日期")
    private String requirementDate;
    @ExcelProperty("使用人")
    private String extUserName;
    @ExcelProperty("使用部门")
    private String extUseDepartmentName;
    @ExcelProperty("供方承诺到货日期")
    private Date ceeaPromiseReceiveDate;
    @ExcelProperty("质保期（自然日）")
    private Integer extWarrantyPeriod;
    @ExcelProperty("明细备注")
    private String comments;
    @ExcelProperty("完成时间")
    private LocalDateTime extFinishTime;
}
