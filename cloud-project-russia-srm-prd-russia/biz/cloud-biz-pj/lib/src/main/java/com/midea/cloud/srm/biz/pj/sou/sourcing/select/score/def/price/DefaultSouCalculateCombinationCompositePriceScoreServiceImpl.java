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
 * 寻源核心 - 价格维度算分[组合 + 综合评分法]
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/12
 */
@Service
public class DefaultSouCalculateCombinationCompositePriceScoreServiceImpl implements SouCalculateScoreDimensionService, ISouCalculatePriceScoreService {

    /**
     * 计算维度得分
     * @param params 供应商物料报价
     * @param dimensionWeight 维度权重
     */
    @Override
    public void calculateAndSet(long projectId, List<? extends SouScoreDimensionContextData> params, BigDecimal dimensionWeight) {
        /* 1. 计算每个供应商的每个组合的的总报价 */
        Map<String/* vendorTab_itemGroup */, BigDecimal> vendorGroupPriceMap = getVendorGroupPriceMap(params);
        /* 2. 收集每个组合的最低报价 */
        Map<String/* itemGroup */, BigDecimal> groupMinPriceMap = getCombinationMinPriceMap(params);
        /* 3. 计算价格得分 */
        params.forEach(data -> {
            /* 当前供应商对组合的总报价 */
            BigDecimal p = vendorGroupPriceMap.get(data.getVendorTab() + "_" + data.getItemGroup());
            /* 同组合不同供应商中的最低报价 */
            BigDecimal pMin = groupMinPriceMap.get(data.getItemGroup());
            /* 计算得到价格得分 */
            BigDecimal priceScore = calculateLowPriceScore(p, pMin);
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
//        组合
                && type.getOrderWay().equals(SouOrderWayEnum.COMBINED)
//        综合评分法
                && type.getScoreRuleType().equals(SouScoreRuleTypeEnum.COMPOSITE_PRICE);
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
