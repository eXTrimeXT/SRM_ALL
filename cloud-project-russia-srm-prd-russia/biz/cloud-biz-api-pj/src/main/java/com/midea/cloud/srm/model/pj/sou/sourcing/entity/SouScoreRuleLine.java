package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleSourceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源.核心表 - 评分规则明细
 * PS: 仅用于记录综合评分场景；对应合理低价/高价，纯价格维度，无需在这里记录
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_score_rule_line")
@ApiModel("寻源评分规则明细")
public class SouScoreRuleLine extends BaseEntity<SouScoreRuleLine> {

    @ApiModelProperty("ID")
    @TableId("SCORE_RULE_LINE_ID")
    private Long scoreRuleLineId;

    /** @see SouScoreRule#getScoreRuleId */
    @ApiModelProperty("评分规则ID")
    @TableField("SCORE_RULE_ID")
    private Long scoreRuleId;

    @ApiModelProperty("评分维度")
    @TableField("DIMENSION")
    private SouScoreRuleDimensionEnum dimension;

    @ApiModelProperty("评分项")
    @TableField("SCORE_ITEM")
    private String scoreItem;

    @ApiModelProperty("评分标准")
    @TableField("SCORE_STANDARD")
    private String scoreStandard;

    @ApiModelProperty(value = "取值来源")
    @TableField("SCORE_SOURCE")
    private SouScoreRuleSourceEnum scoreSource;

    @ApiModelProperty("权重")
    @TableField("SCORE_WEIGHT")
    private BigDecimal scoreWeight;

    @ApiModelProperty("满分值")
    @TableField("TOTAL_SCORE")
    private BigDecimal totalScore;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
