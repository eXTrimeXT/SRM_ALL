package com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 招标计划 - 项目计划 - excel导入数据
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouProjectPlanImportExcelDTO extends BaseObjectX {

    /** @see ExtPrSouProjectPlan#getProjectName */
    @ExcelProperty("项目名称(必填)")
    private String projectName;

    /** @see ExtPrSouProjectPlan#getInitDate */
    @ExcelProperty("立项时间(必填 年/月/日)")
    private String initDate;

    /** @see ExtPrSouProjectPlan#getDepartmentId */
    @ExcelProperty("投资部门ID(必填)")
    private String departmentId;

    /** @see ExtPrSouProjectPlan#getDepartmentName */
    @ExcelProperty("投资部门名称(必填)")
    private String departmentName;

    /** @see ExtPrSouProjectPlan#getInitAmount */
    @ExcelProperty("金额(必填)")
    private BigDecimal initAmount;

    /** @see ExtPrSouProjectPlan#getPlanNo */
    @ExcelProperty("计划编号(必填)")
    private String planNo;

    /** @see ExtPrSouProjectPlan#getPlanAddress */
    @ExcelProperty("投资地点(必填)")
    private String planAddress;

    /** @see ExtPrSouProjectPlan#getPlanLevel */
    @ExcelProperty("项目级别(必填)")
    private String planLevel;

    /** @see ExtPrSouProjectPlan#getPlanStatus */
    @ExcelProperty("项目状态")
    private String planStatus;

    /** @see ExtPrSouProjectPlan#getCreatedBy */
    @ExcelProperty("创建人")
    private String createdBy;

    /** @see ExtPrSouProjectPlan#getCreationDate */
    @ExcelProperty("创建时间")
    private Date creationDate;

    @ExcelProperty("错误信息")
    private String errMsg;

}
