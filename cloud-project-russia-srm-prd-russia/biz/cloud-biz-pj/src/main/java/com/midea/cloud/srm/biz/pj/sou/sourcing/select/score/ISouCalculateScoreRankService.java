package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;

import java.util.List;

/**
 * 寻源模块 - 智能评选排名接口定义
 *
 * @author zhangwk12@midea.com
 * @since 2022/03/31
 */
public interface ISouCalculateScoreRankService extends ISouSpiBean {

    /**
     * 设置排名
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param orderWay 报价方式(单项/组合)
     * @param params 供应商物料报价信息
     */
    void doRank(long projectId, SouOrderWayEnum orderWay, List<? extends SouScoreDimensionContextData> params);

    /**
     * 获取优先级 PS: 如果该接口有多个实现类，则值越高的优先级越高，会覆盖优先级低的实现
     * @return
     */
    @Override
    int getOrder();

}
