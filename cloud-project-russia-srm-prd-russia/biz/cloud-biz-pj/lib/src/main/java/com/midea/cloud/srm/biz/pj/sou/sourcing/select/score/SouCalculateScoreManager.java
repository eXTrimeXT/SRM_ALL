package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 寻源模块 - 智能评分服务实现
 *
 * @author zhangwk12@midea.com
 * @since 2022/03/31
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouCalculateScoreManager implements ISouCalculateScoreManage {

    private static final BigDecimal B_100 = new BigDecimal(100);
    @Autowired
    private SouScoreRuleDAOImpl souScoreRuleDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;

    /**
     * 处理评选算分排名
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源模块{@link SouTypeEnum}
     * @param orderWay 报价方式(单项/组合)
     * @param scoreRuleType 评分规则(合理低价/高价/综合)
     * @param params 供应商报价信息
     * @param scoreRuleId 如果报价方式是综合评分法，则该参数必须有值{@link SouScoreRule#getScoreRuleId}
     */
    @Override
    public void calculateAndSort(long projectId, String souType,
                                 SouOrderWayEnum orderWay,  /* 单项/组合 */
                                 SouScoreRuleTypeEnum scoreRuleType, /* 合理高价/低价/综合 */
                                 List<? extends SouScoreDimensionContextData> params, /* 供应商 */
                                 @Nullable Long scoreRuleId) {
        AssertUtils.notNull(souType, "缺少souType");
        AssertUtils.notEmpty(params, "缺少供应商报价信息，无法进行智能评分计算");

        /* 1: 获取评分规则信息 */
        LinkedHashMap<SouScoreRuleDimensionEnum, BigDecimal/* 维度权重 */> availableDimensionMap = this.getDimensions(scoreRuleType, scoreRuleId);
        /* 2: 各个维度算分 维度权重*/
        availableDimensionMap.forEach((dimension, weight) -> {
            /* 获取最高优先级的维度算分实现 */
            SouCalculateType calculateType = new SouCalculateType(dimension, orderWay, scoreRuleType);
            SouCalculateScoreDimensionService service = this.getService(souType, calculateType);
            /* 计算维度得分 */
            service.calculateAndSet(projectId, params, weight);
        });
        /* 3: 获取最高优先级的排名实现 */
        ISouCalculateScoreRankService rankRule = SouActiveBeanUtils.getActiveBean(souType, ISouCalculateScoreRankService.class);
        rankRule.doRank(projectId, orderWay, params);
    }

    private SouCalculateScoreDimensionService getService(String souType, SouCalculateType calculateType) {
        /* 获取最高优先级的维度算分实现 */
        List<SouCalculateScoreDimensionService> serviceList = SouActiveBeanUtils.getActiveBeans(souType, SouCalculateScoreDimensionService.class)
                .stream()
                .filter(e -> e.match(calculateType))
                .collect(Collectors.toList());
        AssertUtils.notEmpty(serviceList, "[{0}]"+LocaleHandler.getLocaleMsg("维度找不到可用的ISouCalculateScoreDimensionService实现类"), calculateType.getDimension().name());
        if (serviceList.size() == 1) {
            return serviceList.get(0);
        } else {
            /* 优先获取 */
            serviceList.sort(Comparator.comparing(SouCalculateScoreDimensionService::getOrder));
            SouCalculateScoreDimensionService defaultService = null;
            for (SouCalculateScoreDimensionService service : serviceList) {
                if (SouTypeEnum.DEFAULT.name().equals(service.matchModule())) {
                    defaultService = service;
                } else {
                    return service;
                }
            }
            if (defaultService != null) {
                return defaultService;
            } else {
                throw new IllegalArgumentException(MessageFormat.format("[{0}]维度找不到可用的ISouCalculateScoreDimensionService实现类", calculateType.getDimension().name()));
            }
        }
    }

    /** 获取当前生效的维度及其权重 */
    private LinkedHashMap<SouScoreRuleDimensionEnum, BigDecimal/* 维度权重 */> getDimensions(SouScoreRuleTypeEnum scoreRuleType, @Nullable Long scoreRuleId) {
        LinkedHashMap<SouScoreRuleDimensionEnum, BigDecimal/* 维度权重 */> availableDimensionMap = new LinkedHashMap<>(50);

        if (SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(scoreRuleType)) {
            /* 综合评分法 */
            AssertUtils.notNull(scoreRuleId, "缺少scoreRuleId参数");
            SouScoreRule scoreRule = souScoreRuleDao.getById(scoreRuleId);
            AssertUtils.notNull(scoreRule, LocaleHandler.getLocaleMsg("评分规则")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), scoreRuleId);
            List<SouScoreRuleLine> ruleLineList = souScoreRuleLineDao.lambdaQuery()
                    .eq(SouScoreRuleLine::getScoreRuleId, scoreRuleId)
                    .orderByAsc(SouScoreRuleLine::getSortIndex)
                    .list();
            for (SouScoreRuleLine ruleLine : ruleLineList) {
                BigDecimal existWeight = availableDimensionMap.computeIfAbsent(ruleLine.getDimension(), k -> BigDecimal.ZERO);

                /* 评分规则设定中，可能存在同一维度多条数据，因此这里使用累加 */
                availableDimensionMap.put(ruleLine.getDimension(), ruleLine.getScoreWeight().add(existWeight));
            }
        } else {
            availableDimensionMap.put(SouScoreRuleDimensionEnum.PRICE, B_100);
        }
        availableDimensionMap.put(SouScoreRuleDimensionEnum.COMPOSITE, B_100);
        return availableDimensionMap;
    }

}
