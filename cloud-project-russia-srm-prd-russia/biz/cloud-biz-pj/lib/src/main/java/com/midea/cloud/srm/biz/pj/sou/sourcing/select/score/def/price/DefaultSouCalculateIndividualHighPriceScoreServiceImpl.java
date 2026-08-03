package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.def.price;

import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.ISouCalculatePriceScoreService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateScoreDimensionService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateType;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 寻源核心 - 价格维度算分[单项 + 合理高价法]
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/12
 */
@Service
public class DefaultSouCalculateIndividualHighPriceScoreServiceImpl implements SouCalculateScoreDimensionService, ISouCalculatePriceScoreService {

    /**
     * 计算维度得分
     * @param params 供应商物料报价
     * @param dimensionWeight 维度权重
     */
    @Override
    public void calculateAndSet(long projectId, List<? extends SouScoreDimensionContextData> params, BigDecimal dimensionWeight) {
        /* 计算每个物料的最高供应商报价 */
        Map<Long/* souItemId */, BigDecimal/* 最高报价 */> maxPriceMap = getIndividualMaxPriceMap(params);

        params.forEach(data -> {
            BigDecimal priceScore = calculateHighPriceScore(data.getPrice(), maxPriceMap.get(data.getSouItemId()));
            /* 按维度权重进行换算 */
            priceScore = dimensionWeight.divide(ISouCalculatePriceScoreService.B_100, 4, RoundingMode.HALF_UP)
                    .multiply(priceScore)
                    .setScale(2, RoundingMode.HALF_UP);

            data.getDimensionScores().put(SouScoreRuleDimensionEnum.PRICE, priceScore);
        });
    }

    /**
     * 说明该维度算分实现类适用哪种情况
     */
    @Override
    public boolean match(SouCalculateType type) {
//        价格维度
        return type.getDimension().equals(SouScoreRuleDimensionEnum.PRICE)
//        单项
                && type.getOrderWay().equals(SouOrderWayEnum.SINGLE)
//        合理高价
                && type.getScoreRuleType().equals(SouScoreRuleTypeEnum.MAX_PRICE);
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
