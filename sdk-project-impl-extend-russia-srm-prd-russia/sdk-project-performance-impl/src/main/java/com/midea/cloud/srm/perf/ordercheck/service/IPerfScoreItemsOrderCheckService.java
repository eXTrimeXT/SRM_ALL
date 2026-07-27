package com.midea.cloud.srm.perf.scoreproject.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.perf.level.entity.PerfLevel;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckDTO;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckQueryDTO;
import com.midea.cloud.srm.model.perf.ordercheck.entity.PerfScoreItemsOrderCheckDetail;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsOrderCheck;

import java.math.BigDecimal;
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
public interface IPerfScoreItemsOrderCheckService extends BaseService<PerfScoreItemsOrderCheck> {


    /**
     * 批量创建复核数据
     * @param perfScoreItemsOrderCheckDTOList
     */
    void batchSaveOrderCheckList(List<PerfScoreItemsOrderCheckDTO> perfScoreItemsOrderCheckDTOList);

    /**
     * 列表查询接口
     * @param queryDTO
     * @return
     */
    List<PerfScoreItemsOrderCheck> listPage(PerfScoreItemsOrderCheckQueryDTO queryDTO);

    /**
     * 、备注
     * @param orderCheckId 参数
     * @return 返回
     */
    PerfScoreItemsOrderCheckDTO getDetailById(Long orderCheckId);

    /**
     * 备注
     * @param dto 参数
     */
    void reject(PerfScoreItemsOrderCheckDTO dto);

    /**
     * 备注
     * @param checkId 参数
     */
    void calcScore(Long checkId);

    /**
     * 备注
     * @param overallScoreDecimal 参数
     * @param levelList 参数
     * @return 返回
     */
    PerfLevel getLevelByScore(BigDecimal overallScoreDecimal, List<PerfLevel> levelList);
}
