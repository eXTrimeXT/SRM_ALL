package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/03/07/ $
 * @Description:
 */
@Data
public class ExtPrSouRequirementVendorExcelDto {

    @ExcelProperty(value = "推荐单位名称", index = 0)
    private String vendorName;

    @ExcelProperty(value = "联系人名称", index = 1)
    private String contactName;

    @ExcelProperty(value = "联系方式", index = 2)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 3)
    private String email;

    @ExcelProperty(value = "推荐单位来源", index = 4)
    private String recommendFrom;
}
