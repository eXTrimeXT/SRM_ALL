package com.midea.cloud.srm.biz.pj.sou.sourcing.init.validator.scorerule;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.competition.utils.DecimalUtil;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 寻源核心 - 评分规则 - 校验实现
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/22
 */
public abstract class AbstractSouScoreRuleValidator implements ISouSpiBean {

    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private BaseClient baseClient;

    public SouScoreRuleEditPO formatValidateAndConvert(ApiSouScoreRuleDTO param, boolean isTempSave) {
        // 2: 数据格式化及校验
        this.formatAndValidate(param, isTempSave);
        // 3: 数据转换
        return this.convert(param, isTempSave);
    }

    protected void formatAndValidate(ApiSouScoreRuleDTO param, boolean isTempSave) {
        // 1: 格式化及校验评分规则
        this.doFormatAndValidateScoreRule(param, isTempSave);
        // 2: 格式化及校验评分规则明细
        this.doFormatAndValidateScoreRuleLines(param, isTempSave);
    }

    protected SouScoreRuleEditPO convert(ApiSouScoreRuleDTO param, boolean isTempSave) {
        SouScoreRuleEditPO po = new SouScoreRuleEditPO();
        po.setScoreRule(this.doConvertScoreRule(param, isTempSave));
        po.setScoreRuleLineList(this.doConvertScoreRuleLines(po.getScoreRule().getScoreRuleId(), param));
        return po;
    }

    private void doFormatAndValidateScoreRule(ApiSouScoreRuleDTO param, boolean isTempSave) {
        // 1: ID(略)
        // 2: 评分规则编码(置空 - 后端处理)
        // 3: 寻源方式
        AssertUtils.notNull(param.getSouType(), "请选择寻源类型");
        // 4: 评分规则名称
        param.setScoreRuleName(StringUtils.trimToNull(param.getScoreRuleName()));
        AssertUtils.notNull(param.getScoreRuleName(), "请输入评分规则名称");
        long existCount = souScoreRuleDao.lambdaQuery()
                .ne(param.getScoreRuleId() != null, SouScoreRule::getScoreRuleId, param.getScoreRuleId())
                .eq(SouScoreRule::getScoreRuleName, param.getScoreRuleName())
                .eq(SouScoreRule::getSouType, param.getSouType())
                .count();
        AssertUtils.isTrue(existCount <= 0, "同一寻源方式下评分规则名称不能重复");
        // 5: 总分
        AssertUtils.isTrue(param.getTotalScore() != null || isTempSave, "请输入评分规则总分");
        if (param.getTotalScore() != null) {
            AssertUtils.isTrue(param.getTotalScore().compareTo(BigDecimal.ZERO) > 0, "评分规则总分必须大于0");
            param.setTotalScore(param.getTotalScore().stripTrailingZeros());
        }
        // 6: 评选方法(置空 - 后端处理)
        param.setScoreRuleType(null);
        // 7: 评分精度
        if (param.getScorePrecision() == null || param.getScorePrecision() < 0) {
            param.setScorePrecision(0);
        }
        // 8: 状态(置空 - 后端处理)
        param.setScoreRuleStatus(null);
    }

    private void doFormatAndValidateScoreRuleLines(ApiSouScoreRuleDTO param, boolean isTempSave) {
        if (CollectionUtils.isEmpty(param.getRuleLineList())) {
            AssertUtils.isTrue(isTempSave, "请添加评分规则明细");
            return;
        }
        int index = 0;
        BigDecimal totalWeight = BigDecimal.ZERO;
        boolean containsPriceDimension = false;
        Set<String> scoreItems = new HashSet<>(param.getRuleLineList().size());
        for (SouScoreRuleLine ruleLine : param.getRuleLineList()) {
            index++;
            // 1: ID(略)
            // 2: 评分规则ID(置空 - 后端处理)
            ruleLine.setScoreRuleId(null);
            // 3: 评分维度
            AssertUtils.isTrue(ruleLine.getDimension() != null  , LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择评分维度"), index);
            if (ruleLine.getDimension() != null && SouScoreRuleDimensionEnum.PRICE.equals(ruleLine.getDimension())) {
                containsPriceDimension = true;
            }
            // 4: 评分项
            ruleLine.setScoreItem(StringUtils.trimToNull(ruleLine.getScoreItem()));
            AssertUtils.isTrue(ruleLine.getScoreItem() != null  , LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入评分项"), index);
            if (ruleLine.getScoreItem() != null) {
                AssertUtils.isTrue(ruleLine.getScoreItem().length() <= 50, LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行评分项的长度不能超过50"), index);
                AssertUtils.isTrue(scoreItems.add(ruleLine.getScoreItem()), LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行评分项不能重复"), index);
            }
            // 5: 评分标准
            ruleLine.setScoreStandard(StringUtils.trimToNull(ruleLine.getScoreStandard()));
            if (ruleLine.getScoreStandard() != null) {
                AssertUtils.isTrue(ruleLine.getScoreStandard().length() <= 100, LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行评分标准的长度不能超过100"), index);
            }
            // 6: 取值来源
            AssertUtils.isTrue(ruleLine.getScoreSource() != null || isTempSave, LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择取值来源"), index);
            // 7: 权重
            AssertUtils.isTrue(ruleLine.getScoreWeight() != null , LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入权重"), index);
            if (ruleLine.getScoreWeight() != null) {
                AssertUtils.isTrue(ruleLine.getScoreWeight().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行权重必须大于0%"), index);
                AssertUtils.isTrue(ruleLine.getScoreWeight().compareTo(DecimalUtil.B_100) <= 0, LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行权重不能大于100%"), index);
                ruleLine.setScoreWeight(ruleLine.getScoreWeight().stripTrailingZeros());
                totalWeight = totalWeight.add(ruleLine.getScoreWeight());
            }
            // 8: 满分值(不限制必须与评分规则上的满分值相同)
            if (ruleLine.getTotalScore() == null) {
                ruleLine.setTotalScore(param.getTotalScore());
            }
            AssertUtils.isTrue(ruleLine.getTotalScore() != null , LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入满分值"), index);
            if (ruleLine.getTotalScore() != null) {
                AssertUtils.isTrue(ruleLine.getTotalScore().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("评分规则明细第")+"{0}"+LocaleHandler.getLocaleMsg("行满分值必须大于0"), index);
            }
        }
        AssertUtils.isTrue(containsPriceDimension || isTempSave, "评分规则必须包含价格维度的评分明细");
        AssertUtils.isTrue(totalWeight.compareTo(DecimalUtil.B_100) == 0 || isTempSave, "评分规则权重的总和必须为100%");
    }

    @SuppressWarnings("unchecked")
    private SouScoreRule doConvertScoreRule(ApiSouScoreRuleDTO param, boolean isTempSave) {
        SouScoreRule entity;
        if (param.getScoreRuleId() == null) {
            entity = new SouScoreRule();
            BeanUtils.copyProperties(param, entity);
            // ID
            entity.setScoreRuleId(IdGenrator.generate());
            // 编码
            entity.setScoreRuleNo(baseClient.seqGen(SequenceCodeConstant.SOU.SEQ_SCORE_RULE_NO));
            // 评选方法
            entity.setScoreRuleType(SouScoreRuleTypeEnum.COMPOSITE_PRICE);
        } else {
            entity = souScoreRuleDao.getById(param.getScoreRuleId());
            //评分规则名称
            BaseEntity.copyFieldValuesOnlySpecified(entity, param,
                    SouScoreRule::getScoreRuleName,
                    SouScoreRule::getSouType,
                    SouScoreRule::getTotalScore,
                    SouScoreRule::getScorePrecision);
        }
        // 状态
        entity.setScoreRuleStatus(isTempSave ? SouScoreRuleStatusEnum.DRAFT : SouScoreRuleStatusEnum.VALID);

        return entity;
    }

    private List<SouScoreRuleLine> doConvertScoreRuleLines(long scoreRuleId, ApiSouScoreRuleDTO param) {
        List<SouScoreRuleLine> ruleLineList = param.getRuleLineList();
        if (CollectionUtils.isEmpty(ruleLineList)) {
            return Collections.emptyList();
        }

        int index = 0;
        for (SouScoreRuleLine ruleLine : ruleLineList) {
            index++;
            // ID
            if (ruleLine.getScoreRuleLineId() == null) {
                ruleLine.setScoreRuleLineId(IdGenrator.generate());
            }
            // 评分规则ID
            ruleLine.setScoreRuleId(scoreRuleId);
            // 排序
            ruleLine.setSortIndex(index);
        }
        return ruleLineList;
    }

}
