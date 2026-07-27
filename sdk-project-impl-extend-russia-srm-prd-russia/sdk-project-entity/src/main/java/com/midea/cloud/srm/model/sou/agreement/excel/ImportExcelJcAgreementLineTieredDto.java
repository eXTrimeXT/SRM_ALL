package com.midea.cloud.srm.model.sou.agreement.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ex_liuxy46
 */
@Data
public class ImportExcelJcAgreementLineTieredDto implements Serializable {

    @ExcelProperty(value = "表格行号", index = 0)
    private Integer rowLine;

    @ExcelProperty(value = "阶梯上限（<）", index = 1)
    private Integer lessNum;

    @ExcelProperty(value = "协议未税单价", index = 2)
    private BigDecimal priceTax;

    @ExcelProperty(value = "参考价", index = 3)
    private BigDecimal referPrice;

}
