package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.def.performance;

import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.ISouCalculatePriceScoreService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateScoreDimensionService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateType;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源核心 - 绩效维度算分
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/12
 */
@Service
public class DefaultSouCalculatePerformanceScoreServiceImpl implements SouCalculateScoreDimensionService, ISouCalculatePriceScoreService {

    /**
     * 计算维度得分
     * @param params 供应商物料报价
     * @param dimensionWeight 维度权重
     */
    @Override
    public void calculateAndSet(long projectId, List<? extends SouScoreDimensionContextData> params, BigDecimal dimensionWeight) {
        // 空实现，目前招投标没有引入绩效功能
        params.forEach(data ->
            data.getDimensionScores().put(SouScoreRuleDimensionEnum.ACHIEVEMENT, BigDecimal.ZERO)
        );
    }

    /**
     * 说明该维度算分实现类适用哪种情况
     */
    @Override
    public boolean match(SouCalculateType type) {
//        绩效维度
        return type.getDimension().equals(SouScoreRuleDimensionEnum.ACHIEVEMENT);
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
