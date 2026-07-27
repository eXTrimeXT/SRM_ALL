package com.midea.cloud.srm.model.perf.orderscoreman.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * <pre>
 *  评分人绩效评分 导入DTO
 * </pre>
 *
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-11-04 10:00:05
 *  修改内容:
 * </pre>
 * @author huangbf3
 */
@Data
@ColumnWidth(40)
@HeadRowHeight(30)
@ApiModel(description = "评分人绩效评分表导入")
public class PjScoreManScoringV1Import implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评分项目名称")
    @ExcelProperty(value = "评分项目名称", index = 0 )
    private String projectName;

    @ApiModelProperty(value = "供应商名称")
    @ExcelProperty(value = "供应商名称" , index = 1)
    private String companyName;

    @ApiModelProperty(value = "指标维度")
    @ExcelProperty(value = "指标维度" , index = 2)
    private String indicatorDimensionType;

    @ApiModelProperty(value = "指标名称")
    @ExcelProperty(value = "指标名称" , index = 3)
    private String indicatorName;

    @ApiModelProperty(value = "模型品类")
    @ExcelProperty(value = "模型品类" , index = 4)
    private String templateCategoryNames;

    @ApiModelProperty(value = "评分状态")
    @ExcelProperty(value = "评分状态" , index = 5)
    private String scoringStatus;

    @ApiModelProperty(value = "打分逻辑")
    @ExcelProperty(value = "打分逻辑" , index = 6)
    private String indicatorLogic;

    @ApiModelProperty(value = "评价方式(SCORING_SYSTEM_VALUE:评分-系统取值,DEDUCTION_SYSTEM_VALUE:扣分-系统取值,SCORING_MANUAL:评分-手工,DEDUCTION_MANUAL:扣分-手工)")
    @ExcelProperty(value = "评分方式" , index = 7)
    private String evaluation;

    @ApiModelProperty(value = "*绩效得分")
    @ExcelProperty(value = "*绩效得分" , index = 8)
    private String score;

    @ApiModelProperty(value = "*打分说明")
    @ExcelProperty(value = "*打分说明" , index = 9)
    private String comments;

    @ApiModelProperty(value = "绩效开始月份")
    @ExcelProperty(value = "绩效开始月份" , index = 10)
    private String perStartMonth;

    @ApiModelProperty(value = "绩效结束月份")
    @ExcelProperty(value = "绩效结束月份" , index = 11)
    private String perEndMonth;

    @ApiModelProperty(value = "公司")
    @ExcelProperty(value = "公司" , index = 12)
    private String organizationName;

    @ApiModelProperty(value = "评分人")
    @ExcelProperty(value = "评分人" , index = 13)
    private String scoreNickName;

    @ApiModelProperty(value = "评分人工号")
    @ExcelProperty(value = "评分人工号" , index = 14)
    private String scoreAccount;

    @ApiModelProperty(value = "评分时间")
    @ExcelProperty(value = "评分时间" , index = 15)
    private Date scoreDate;

    @ApiModelProperty(value = "驳回说明")
    @ExcelProperty(value = "驳回说明" , index = 16)
    private String approveRejectDesc;

    @ApiModelProperty(value = "唯一标识")
    @ExcelProperty(value = "唯一标识" , index = 17)
    private String scoreManScoringId;

    @ApiModelProperty(value = "错误信息提示")
    @ExcelProperty(value = "错误信息提示",index = 18)
    private String errorMsg;

}
