package com.midea.cloud.srm.model.sou.expert.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专家库 - hr信息搜集
 * @author huangbf3
 */
@Data
public class ExtSouExpertHrImportVO {

    @ExcelProperty("姓名")
    private String applyByNickname;

    @ExcelProperty("工号")
    private String applyByCode;

    @ExcelProperty("最高学历")
    private String highestDegree;

    @ExcelProperty("毕业时间")
    private String studyDateTo;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("职务")
    private String job;

    @ExcelProperty("序列等级")
    private String jobRank;

    @ExcelProperty("在职状态")
    private String jobStatus;

    @ExcelProperty("手机号码")
    private String phone;

    @ExcelProperty("入厂时间")
    private String hireDate;

    @ExcelProperty("毕业学校")
    private String studyCollege;

    @ExcelProperty("所学专业")
    private String major;

    @ExcelProperty("申报等级")
    private String applyLevel;

}
