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
import java.util.ArrayList;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_project_score_dim")
public class ProjectScoreDim extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("SCORE_DIM_ID")
    private Long scoreDimId;

    @ApiModelProperty(value = "绩效评分ID")
    @TableField("SCORE_HEADER_ID")
    private Long scoreHeaderId;

    @ApiModelProperty(value = "维度权重(百分比)")
    @TableField("INDICATOR_DIMENSION_WEIGHT")
    private String indicatorDimensionWeight;

    @ApiModelProperty(value = "维度")
    @TableField("INDICATOR_DIMENSION_TYPE")
    private String indicatorDimensionType;
    @ApiModelProperty(value = "维度成绩")
    @TableField("SCORE")
    private BigDecimal score;

    @ApiModelProperty(value = "指标信息")
    @TableField(exist = false)
    private List<ProjectScoreInd> indList = new ArrayList<>();

}
