package com.midea.cloud.srm.model.perf.overallscore.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.perf.scoring.PerfOverallScore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * <pre>
 * 绩效评分主表DTO 导入DTO
 * @author huangbf3
 * </pre>
 *
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-11-06 10:00:05
 *  修改内容:
 * </pre>
 */
@Data
@ApiModel(description = "绩效评分主表DTO")
@TableName("scc_perf_overall_score")
public class PjPerfOverallScore extends PerfOverallScore {

    @ApiModelProperty(value = "预警单据ID")
    @TableField("WARNING_ID")
    private Long warningId;

    @ApiModelProperty(value = "预警单据号")
    @TableField("WARNING_CODE")
    private String warningCode;

    @ApiModelProperty(value = "等级名称查询条件")
    @TableField(exist = false)
    private List<String> levelNames = new ArrayList<>();

}
