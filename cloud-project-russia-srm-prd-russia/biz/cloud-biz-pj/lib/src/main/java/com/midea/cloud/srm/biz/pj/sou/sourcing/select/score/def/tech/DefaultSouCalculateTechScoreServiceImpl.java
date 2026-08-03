package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.def.tech;

import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.ISouCalculatePriceScoreService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateScoreDimensionService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateType;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadDAOImpl;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 技术维度算分
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/12
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultSouCalculateTechScoreServiceImpl implements SouCalculateScoreDimensionService, ISouCalculatePriceScoreService {

    @Autowired
    private SouTechScoreHeadDAOImpl souTechScoreHeadDao;

    /**
     * 计算维度得分
     *
     * @param params          供应商物料报价
     * @param dimensionWeight 维度权重
     */
    @Override
    public void calculateAndSet(long projectId, List<? extends SouScoreDimensionContextData> params, BigDecimal dimensionWeight) {
        /* 1: 查询供应商的技术评分信息 */
        Map<Long/* vendorId */, List<SouTechScoreHead>> techScoreMap = souTechScoreHeadDao.list(SouTechScoreHead::getProjectId, projectId)
                .stream().collect(Collectors.groupingBy(SouTechScoreHead::getVendorId));
        /* 2: 计算供应商们的技术得分(技术得分的平均分) */
        Map<Long/* vendorId */, BigDecimal> vendorScoreMap = new HashMap<>(techScoreMap.size());
        techScoreMap.forEach((vendorId, techScoreList) -> {
            BigDecimal totalScore = BigDecimal.ZERO;
            for (SouTechScoreHead score : techScoreList) {
                totalScore = totalScore.add(score.getTotalScore());
            }
            vendorScoreMap.put(vendorId, totalScore.divide(new BigDecimal(techScoreList.size()), 4, RoundingMode.HALF_UP));
        });
        /* 3: 返回结果 */
        params.forEach(param -> param.getDimensionScores().put(SouScoreRuleDimensionEnum.TECHNOLOGY,
                vendorScoreMap.get(Long.valueOf(param.getVendorTab().toString()))));
    }

    /**
     * 说明该维度算分实现类适用哪种情况
     */
    @Override
    public boolean match(SouCalculateType type) {
//        技术维度
        return type.getDimension().equals(SouScoreRuleDimensionEnum.TECHNOLOGY);
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
