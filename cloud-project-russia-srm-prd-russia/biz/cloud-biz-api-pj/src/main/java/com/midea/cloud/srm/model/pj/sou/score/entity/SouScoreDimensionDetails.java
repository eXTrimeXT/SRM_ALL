package com.midea.cloud.srm.model.pj.sou.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimension;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 评分维度明细
 * @author: hesl41
 * @Date: 2022/10/17 11:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_score_dimension_details")
@ApiModel("评分维度明细")
public class SouScoreDimensionDetails extends BaseEntity<SouScoreDimensionDetails> {

    @ApiModelProperty("ID")
    @TableId("SCORE_DIMENSION_DETAILS_ID")
    private Long scoreDimensionDeatailsId;

    /**
     * @see SouScoreDimension#getScoreDimensionId()
     */
    @ApiModelProperty("评分维度ID")
    @TableField("SCORE_DIMENSION_ID")
    private Long scoreDimensionId;

    /**
     * @see SouScoreTemplate#getScoreTemplateId()
     */
    @ApiModelProperty("评分模板ID")
    @TableField("SCORE_TEMPLATE_ID")
    private Long scoreTemplateId;

    @ApiModelProperty("评分项目")
    @TableField("SCORE_ITEM")
    private String scoreItem;

    @ApiModelProperty("评分标准")
    @TableField("DIMENSION_STANDARD")
    private String dimensionStandard;

    @ApiModelProperty("权重%")
    @TableField("WEIGHT")
    private BigDecimal weight;

    @ApiModelProperty("满分值")
    @TableField("FULL_SCORE")
    private BigDecimal fullScore;

}
