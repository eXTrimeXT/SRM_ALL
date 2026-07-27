package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.def.composite;

import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.ISouCalculatePriceScoreService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateScoreDimensionService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateType;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 寻源核心 - 用于计算综合得分
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/12
 */
@Service
public class DefaultSouCalculateCompositeScoreServiceImpl implements SouCalculateScoreDimensionService, ISouCalculatePriceScoreService {

    /**
     * 计算维度得分
     *
     * @param params          供应商物料报价
     * @param dimensionWeight 维度权重
     */
    @Override
    public void calculateAndSet(long projectId, List<? extends SouScoreDimensionContextData> params, BigDecimal dimensionWeight) {
        params.forEach(data -> {
            BigDecimal totalScore = BigDecimal.ZERO;
            for (Map.Entry<SouScoreRuleDimensionEnum, BigDecimal> entry : data.getDimensionScores().entrySet()) {
                if (!entry.getKey().equals(SouScoreRuleDimensionEnum.COMPOSITE)) {
                    totalScore = totalScore.add(entry.getValue());
                }
            }
            data.getDimensionScores().put(SouScoreRuleDimensionEnum.COMPOSITE, totalScore);
        });
    }

    /**
     * 说明该维度算分实现类适用哪种情况
     */
    @Override
    public boolean match(SouCalculateType type) {
//        综合得分
        return type.getDimension().equals(SouScoreRuleDimensionEnum.COMPOSITE);
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
