package com.midea.cloud.srm.model.perf.projectscore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_ind")
public class ProjectScoreInd extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("SCORE_IND_ID")
    private Long scoreIndId;

    @ApiModelProperty(value = "绩效评分ID")
    @TableField("SCORE_HEADER_ID")
    private Long scoreHeaderId;

    @ApiModelProperty(value = "维度ID")
    @TableField("SCORE_DIM_ID")
    private Long scoreDimId;

    @ApiModelProperty(value = "绩效模型-指标名称")
    @TableField("INDICATOR_NAME")
    private String indicatorName;

    @ApiModelProperty(value = "评价方式(SCORING_SYSTEM_VALUE:评分-系统取值,DEDUCTION_SYSTEM_VALUE:扣分-系统取值,SCORING_MANUAL:评分-手工,DEDUCTION_MANUAL:扣分-手工)")
    @TableField("EVALUATION")
    private String evaluation;

    @ApiModelProperty(value = "指标得分")
    @TableField("SCORE")
    private BigDecimal score;
}
