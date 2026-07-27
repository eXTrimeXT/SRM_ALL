package com.midea.cloud.srm.perf.overallscore.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.perf.overallscore.dto.PjPerfOverallScore;
import com.midea.cloud.srm.model.perf.scoring.PerfOverallScore;

import java.util.List;

/**
 * <pre>
 *  绩效评分项目评分人表 服务类
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:36
 *  修改内容:
 * </pre>
 */
public interface IPjPerfOverallScoreService extends BaseService<PjPerfOverallScore> {
    /**
     *  备注
     * @param perfOverallScore 参数
     * @return 返回
     */
    List<PjPerfOverallScore> listPage(PjPerfOverallScore perfOverallScore);

    /**
     * 备注
     * @param queryOverallScore 参数
     * @return 返回
     */
    List<PerfOverallScore> findOverallScoreAndSonList(PerfOverallScore queryOverallScore);
}
