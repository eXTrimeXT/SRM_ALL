package com.midea.cloud.srm.model.pj.sou.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreTemplate;
import com.midea.cloud.srm.model.pj.sou.score.enums.ScoreDimensionRuleEnum;
import com.midea.cloud.srm.model.pj.sou.score.enums.SouScoreDimensionCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 评分维度
 * @author: hesl41
 * @Date: 2022/10/17 10:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_score_dimension")
@ApiModel("评分维度")
public class SouScoreDimension extends BaseEntity<SouScoreDimension> {

    @ApiModelProperty("ID")
    @TableId("SCORE_DIMENSION_ID")
    private Long scoreDimensionId;
    /**
     * @see SouScoreTemplate#getScoreTemplateId()
     */
    @ApiModelProperty("评分模板ID")
    @TableField("SCORE_TEMPLATE_ID")
    private Long scoreTemplateId;

    /**
     * @see SouScoreDimensionCodeEnum
     */
    @ApiModelProperty("评分维度编码")
    @TableField("DIMENSION_CODE")
    private String dimensionCode;
    /**
     * @see ScoreDimensionRuleEnum
     */
    @ApiModelProperty("评分规则:仅一次评分,多轮评分,首轮需评分")
    @TableField("SCORE_DIMENSION_RULE")
    private String scoreDimensionRule;

    /**
     * 权重值, 不包含 % 号,计算时要除以100
     * 50% ,则 weight=50
     */
    @ApiModelProperty("权重%")
    @TableField("WEIGHT")
    private BigDecimal weight;

    @ApiModelProperty("满分值")
    @TableField("FULL_SCORE")
    private BigDecimal fullScore;

    @ApiModelProperty("评分说明")
    @TableField("SCORE_INSTRUCTIONS")
    private String scoreInstructions;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

}
