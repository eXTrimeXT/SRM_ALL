package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.model.bid.enums.ScoreRuleConfigStatusEnum;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源openAPI - 评分规则查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouScoreRuleQueryDTO extends BasePage {

    /** @see SouScoreRule#getScoreRuleNo */
    @ApiModelProperty("评分规则编码(模糊查询)")
    private String scoreRuleNo;

    /** @see SouScoreRule#getScoreRuleName */
    @ApiModelProperty("评分规则名称(模糊查询)")
    private String scoreRuleName;

    /** @see SouScoreRule#getScoreRuleStatus */
    @ApiModelProperty("状态(等值查询)")
    private ScoreRuleConfigStatusEnum status;

    /** @see SouScoreRule#getSouType */
    @ApiModelProperty("寻源类型(等值查询)")
    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        // 评分规则编码
        scoreRuleNo = StringUtils.trimToNull(scoreRuleNo);
        // 评分规则名称
        scoreRuleName = StringUtils.trimToNull(scoreRuleName);
        // 寻源类型
        souType = StringUtils.trimToNull(souType);
    }

    /**
     * 转化
     */
    private LambdaQueryWrapper<SouScoreRule> convertLambda() {
        LambdaQueryWrapper<SouScoreRule> lambda = new LambdaQueryWrapper<>();

        // 评分规则编码
        lambda.like(scoreRuleNo != null, SouScoreRule::getScoreRuleNo, scoreRuleNo);
        // 评分规则名称
        lambda.like(scoreRuleName != null, SouScoreRule::getScoreRuleName, scoreRuleName);
        // 状态
        lambda.eq(status != null, SouScoreRule::getScoreRuleStatus, status);
        // 寻源类型
        lambda.eq(souType != null, SouScoreRule::getSouType, souType);

        return lambda;
    }

}
