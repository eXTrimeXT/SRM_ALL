package com.midea.cloud.srm.biz.pj.sou.sourcing.init.judge;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouScoreRuleEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

/**
 * 寻源 - 评分规则 - 接口权限/条件控制
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/21
 */
public abstract class AbstractSouScoreRuleJudge implements ISouSpiBean {

    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;
    @Autowired
    private SouScoreRuleEventService souScoreRuleEventService;

    /**
     * 当前操作人是否可以查看评分规则详情
     * @param scoreRuleId {@link SouScoreRule#getScoreRuleId}
     */
    public SouScoreRule judgeGetScoreRuleAuth(long scoreRuleId) {
        SouScoreRule scoreRuleConfig = souScoreRuleDao.getById(scoreRuleId);
        AssertUtils.notNull(scoreRuleConfig, "评分规则不存在");
        return scoreRuleConfig;
    }

    /**
     * 当前操作人是否可以编辑评分规则信息
     */
    public void judgeEditScoreRuleAuth(@Nullable Long scoreRuleId) {
        if (scoreRuleId != null) {
            SouScoreRule scoreRule = souScoreRuleDao.getById(scoreRuleId);
            AssertUtils.notNull(scoreRule, "评分规则不存在", scoreRuleId);
            AssertUtils.isTrue(SouScoreRuleStatusEnum.DRAFT.equals(scoreRule.getScoreRuleStatus()), "评分规则非拟定状态，不能编辑");
        }
    }

    /**
     * 当前操作人是否可以生效评分规则
     */
    public void judgeValidScoreRuleAuth(long scoreRuleId) {
        SouScoreRule scoreRule = souScoreRuleDao.getById(scoreRuleId);
        AssertUtils.notNull(scoreRule, LocaleHandler.getLocaleMsg("评分规则")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), scoreRuleId);
        if (SouScoreRuleStatusEnum.DRAFT.equals(scoreRule.getScoreRuleStatus())) {
            ApiSouScoreRuleDTO param = new ApiSouScoreRuleDTO();
            BeanUtils.copyProperties(scoreRule, param);
            param.setRuleLineList(souScoreRuleLineDao.lambdaQuery()
                    .eq(SouScoreRuleLine::getScoreRuleId, scoreRuleId)
                    .orderByAsc(SouScoreRuleLine::getSortIndex)
                    .list());
            souScoreRuleEventService.editScoreRule(param, false);
        }
    }

    /**
     * 当前操作人是否可以失效评分规则
     */
    public void judgeInvalidScoreRuleAuth(long scoreRuleId) {
        SouScoreRule scoreRule = souScoreRuleDao.getById(scoreRuleId);
        AssertUtils.notNull(scoreRule, LocaleHandler.getLocaleMsg("评分规则")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), scoreRuleId);
        AssertUtils.isFalse(SouScoreRuleStatusEnum.DRAFT.equals(scoreRule.getScoreRuleStatus()), "不能失效处于拟定状态的评分规则");
    }

    /**
     * 当前操作人是否可以删除评分规则
     */
    public void judgeRemoveScoreRuleAuth(long scoreRuleId) {
        SouScoreRule scoreRule = souScoreRuleDao.getById(scoreRuleId);
        AssertUtils.notNull(scoreRule, LocaleHandler.getLocaleMsg("评分规则")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), scoreRuleId);
        AssertUtils.isTrue(SouScoreRuleStatusEnum.DRAFT.equals(scoreRule.getScoreRuleStatus()), "只能删除拟定状态的评分规则");
    }

}
