package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 寻源模块 - 智能评选算分 - 计算类别
 *
 * @author zhangwk12@midea.com
 * @since 2022/04/01
 */
@Data
@AllArgsConstructor
public class SouCalculateType {

    /**
     * 维度
     */
    private SouScoreRuleDimensionEnum dimension;

    /**
     * 报价方式(单项/组合)
     */
    private SouOrderWayEnum orderWay;

    /**
     * 评分方法(低价/高价/综合)
     */
    private SouScoreRuleTypeEnum scoreRuleType;

}
