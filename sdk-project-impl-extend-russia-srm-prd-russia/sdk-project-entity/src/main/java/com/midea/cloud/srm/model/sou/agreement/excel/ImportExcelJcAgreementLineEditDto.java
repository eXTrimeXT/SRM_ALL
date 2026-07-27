package com.midea.cloud.srm.model.sou.agreement.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ex_liuxy46
 */
@Data
public class ImportExcelJcAgreementLineEditDto implements Serializable {

    @ExcelProperty(value = "表格行号", index = 0)
    private Integer rowLine;

    @ExcelProperty(value = "物料编码", index = 1)
    private String materialCode;

    @ExcelProperty(value = "协议价类型", index = 2)
    private Integer xyjType;

    @ExcelProperty(value = "起订量", index = 3)
    private Integer startNum;

    @ExcelProperty(value = "整倍起售数量", index = 4)
    private Integer multipleStartNum;

    @ExcelProperty(value = "协议未税单价", index = 5)
    private BigDecimal priceTax;

    @ExcelProperty(value = "税率编码", index = 6)
    private String taxRateCode;

    @ExcelProperty(value = "参考价", index = 7)
    private BigDecimal referencePrice;

    @ExcelProperty(value = "交货周期(天)", index = 8)
    private Integer leadTime;

    @ExcelProperty(value = "质保期", index = 9)
    private Integer sellByDate;

    @ExcelProperty(value = "协议行说明", index = 10)
    private String agreementDes;

    @ExcelProperty(value = "品牌", index = 11)
    private String brand;

}
