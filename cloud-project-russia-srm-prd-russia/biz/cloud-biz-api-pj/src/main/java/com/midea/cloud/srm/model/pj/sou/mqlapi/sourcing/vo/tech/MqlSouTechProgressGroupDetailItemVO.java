package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreLine;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * MQL - 评分详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@ApiModel("评分详情")
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechProgressGroupDetailItemVO extends BaseObjectX {

    /** @see SouTechScoreLine#getTechScoreLineId */
    @ApiModelProperty("评分详情ID")
    private Long techScoreLineId;

    /** @see SouScoreRuleLine#getScoreRuleLineId */
    @ApiModelProperty("评分规则模板行ID")
    private Long scoreRuleLineId;

    /** @see SouScoreRuleLine#getScoreItem */
    @ApiModelProperty("评分项")
    private String scoreItem;

    /** @see SouScoreRuleLine#getScoreStandard */
    @ApiModelProperty("评分标准")
    private String scoreStandard;

    /** @see SouScoreRuleLine#getScoreWeight */
    @ApiModelProperty("权重")
    private BigDecimal scoreWeight;

    /** @see SouScoreRuleLine#getTotalScore */
    @ApiModelProperty("满分值")
    private BigDecimal totalScore;

    /** @see SouTechScoreLine#getScore */
    @ApiModelProperty("评分")
    private BigDecimal score;

}
