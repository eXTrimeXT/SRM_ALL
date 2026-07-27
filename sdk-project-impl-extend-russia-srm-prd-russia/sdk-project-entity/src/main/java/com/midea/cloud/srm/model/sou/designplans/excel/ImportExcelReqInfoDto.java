package com.midea.cloud.srm.model.sou.designplans.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ex_liuxy46
 */
@Data
public class ImportExcelReqInfoDto implements Serializable {

    @NotEmpty(message = "区域不能为空")
    @ExcelProperty(value = "区域", index = 0)
    private String areaName;

    @NotEmpty(message = "物料编码不能为空")
    @ExcelProperty(value = "物料编码", index = 1)
    private String materialCode;

    @NotNull(message = "数量不能为空")
    @ExcelProperty(value = "数量", index = 2)
    private BigDecimal orderNum;

    @NotEmpty(message = "品牌不能为空")
    @ExcelProperty(value = "品牌", index = 3)
    private String brand;

    @NotNull(message = "未税单价不能为空")
    @ExcelProperty(value = "未税单价", index = 4)
    private BigDecimal priceTax;

    @NotEmpty(message = "税率编码不能为空")
    @ExcelProperty(value = "税率编码", index = 5)
    private String taxRateCode;

    @NotEmpty(message = "历史供应商编码不能为空")
    @ExcelProperty(value = "历史供应商编码", index = 6)
    private String historyVendorCode;

    @NotNull(message = "历史未税单价不能为空")
    @ExcelProperty(value = "历史未税单价", index = 7)
    private BigDecimal historyPriceTax;

    @NotNull(message = "历史含税单价不能为空")
    @ExcelProperty(value = "历史含税单价", index = 8)
    private BigDecimal historyRatePrice;

    @ExcelProperty(value = "错误提示", index = 9)
    private String errInfo;

}
