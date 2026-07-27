package com.midea.cloud.srm.biz.pj.sou.sourcing.init.validator.scorerule;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 寻源核心 - 评分规则 - 保存数据
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/22
 */
@Data
@NoArgsConstructor
public class SouScoreRuleEditPO {

    /** 评分规则 */
    private SouScoreRule scoreRule;
    /** 评分规则明细 */
    private List<SouScoreRuleLine> scoreRuleLineList;

}
