package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.def.rank;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.ISouCalculateScoreRankService;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 智能评选排名实现
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/12
 */
@Service
public class DefaultSouCalculateScoreRankServiceImpl implements ISouCalculateScoreRankService {

    /**
     * 设置排名(同物料下，综合得分相同的，排名相同)
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param orderWay 报价方式(单项/组合)
     * @param params 供应商物料报价信息
     */
    @Override
    public void doRank(long projectId, SouOrderWayEnum orderWay, List<? extends SouScoreDimensionContextData> params) {
        Map<Long/* souItemId */, List<SouScoreDimensionContextData>> paramsMap = params.stream()
                .collect(Collectors.groupingBy(SouScoreDimensionContextData::getSouItemId));

        for (List<SouScoreDimensionContextData> dataList : paramsMap.values()) {
            /* 按照综合评分降序排列 */
            dataList.sort(Comparator.comparing(e -> e.getDimensionScores().get(SouScoreRuleDimensionEnum.COMPOSITE)));
            Collections.reverse(dataList);

            BigDecimal score = null;
            BigDecimal tempScore;
            int ranking = 0;
            for (SouScoreDimensionContextData data : dataList) {
                tempScore = data.getDimensionScores().get(SouScoreRuleDimensionEnum.COMPOSITE);
                AssertUtils.notNull(tempScore, "找不到综合得分信息");
                if (ranking == 0) {
                    score = tempScore;
                    data.setRanking(++ranking);
                } else {
//                    score.compareTo(tempScore) 由于score为空，可能会有空指针异常
                    if (tempScore.compareTo(score) == 0) {
                        data.setRanking(ranking);
                    } else if (tempScore.compareTo(score) < 0) {
                        score = tempScore;
                        data.setRanking(++ranking);
                    } else {
                        /* 由于列表已经是降序排列，不应该出现这种情况的 */
                        throw new IllegalArgumentException("排序错误");
                    }
                }
            }
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    /**
     * 获取优先级
     * PS: 如果该接口有多个实现类，则值越高的优先级越高，会覆盖优先级低的实现
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
