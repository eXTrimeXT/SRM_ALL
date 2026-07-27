package com.midea.cloud.srm.perf.orderscoreman.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.perf.ordercheck.entity.PerfScoreItemsOrderCheckDetail;
import com.midea.cloud.srm.model.perf.orderscoreman.entity.PjOrderScoreManScoringV1;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 绩效评分项目评分人表 Mapper 接口
 * </p>
 *
 * @author luxc18@meiCloud.com
 * @since 2020-06-06
 */
public interface PjOrderScoreManScoringV1Mapper extends BaseMapper<PjOrderScoreManScoringV1> {
    /**
     * 备注
     * @param list 参数
     * @return 返回
     */
    List<PjOrderScoreManScoringV1> listByGroupList(@Param("list") List<PjOrderScoreManScoringV1> list);

    /**
     * 备注
     * @param list 参数
     * @return 返回
     */
    List<PerfScoreItemsOrderCheckDetail> listCheckDetailByGroupList(@Param("list") List<PjOrderScoreManScoringV1> list);

}
