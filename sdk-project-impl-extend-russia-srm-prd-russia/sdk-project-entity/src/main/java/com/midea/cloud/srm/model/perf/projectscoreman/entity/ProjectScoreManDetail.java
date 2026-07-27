package com.midea.cloud.srm.model.perf.projectscoreman.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateIndsLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_man_detail")
public class ProjectScoreManDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键ID")
    @TableId("SCORE_MAN_DETAIL_ID")
    private Long scoreManDetailId;

    @ApiModelProperty(value = "项目化绩效评分主表ID")
    @TableField("PROJECT_SCORE_MAN_ID")
    private Long projectScoreManId;

    @ApiModelProperty(value = "绩效模型指标行ID")
    @TableField("TEMPLATE_LINE_ID")
    private Long templateLineId;

    @ApiModelProperty(value = "评价方式(SCORING_SYSTEM_VALUE:评分-系统取值,DEDUCTION_SYSTEM_VALUE:扣分-系统取值,SCORING_MANUAL:评分-手工,DEDUCTION_MANUAL:扣分-手工)")
    @TableField("EVALUATION")
    private String evaluation;

    @ApiModelProperty(value = "绩效模型-指标维度(QUALITY-品质,SERVICE-服务,DELIVER-交付,COST-成本,TECHNOLOGY-技术))")
    @TableField("INDICATOR_DIMENSION_TYPE")
    private String indicatorDimensionType;

    @ApiModelProperty(value = "绩效模型-维度权重(百分比)")
    @TableField("INDICATOR_DIMENSION_WEIGHT")
    private String indicatorDimensionWeight;

    @ApiModelProperty(value = "绩效模型-指标名称")
    @TableField("INDICATOR_NAME")
    private String indicatorName;

    @ApiModelProperty(value = "指标行类型(TEXT-文本，NUMBER-数子，PERCENTAGE-百分比)")
    @TableField("INDICATOR_LINE_TYPE")
    private String indicatorLineType;

    @ApiModelProperty(value = "折算方式(DIRECT_QUOTE-直接取值，TEXT_CONVERSION-按文本折算，INTERVAL_CONVERSION-按区间折算)")
    @TableField("QUOTE_MODE")
    private String quoteMode;

    @ApiModelProperty(value = "指标逻辑")
    @TableField("INDICATOR_LOGIC")
    private String indicatorLogic;

    @ApiModelProperty(value = "绩效得分")
    @TableField("SCORE")
    private BigDecimal score;

    @ApiModelProperty(value = "备注")
    @TableField("COMMENTS")
    private String comments;

    @ApiModelProperty(value = "绩效模型-指标权重(百分比)")
    @TableField("DIMENSION_WEIGHT")
    private BigDecimal dimensionWeight;

    @ApiModelProperty(value = "附件ID")
    @TableField("FILE_ID")
    private Long fileId;

    @ApiModelProperty(value = "附件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty(value = "绩效模型指标行表-ID(文本取值时保存)")
    @TableField("TEMPLATE_INDS_LINE_ID")
    private Long templateIndsLineId;

    @ApiModelProperty(value = "绩效模型指标行表-取值方式值(保存操作‘直接取值’和‘按区间折算’时必填)")
    @TableField("PEF_SCORE")
    private BigDecimal pefScore;

    @ApiModelProperty(value = "评分时间")
    @TableField("SCORE_DATE")
    private Date scoreDate;

    @ApiModelProperty(value = "指标明细信息")
    @TableField(exist = false)
    List<PerfTemplateIndsLine> indicatorsLines = new ArrayList<>();
}
