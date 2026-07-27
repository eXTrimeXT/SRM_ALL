package com.midea.cloud.srm.model.sou.inq.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ex_liuxy46
 */
@Data
public class ExportInqReqInfoDataVo implements Serializable {

    @ExcelProperty(value = "业务实体", index = 0)
    private String orgOuName;
    @ExcelProperty(value = "区域", index = 1)
    private String extAreaCode;
    @ExcelProperty(value = "物料编码", index = 2)
    private String itemCode;
    @ExcelProperty(value = "物料名称", index = 3)
    private String itemDesc;
    @ExcelProperty(value = "物料分类", index = 4)
    private String categoryName;
    @ExcelProperty(value = "规格型号", index = 5)
    private String extMaterialModel;
    @ExcelProperty(value = "数量", index = 6)
    private String requireQuantity;
    @ExcelProperty(value = "品牌", index = 7)
    private String extBrand;
    @ExcelProperty(value = "基本计量单位", index = 8)
    private String unit;
    @ExcelProperty(value = "备注", index = 9)
    private String remark;

}
