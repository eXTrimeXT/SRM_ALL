package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score;

import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源模块 - 智能评分算分定义
 *
 * @author zhangwk12@midea.com
 * @since 2022/03/31
 */
public interface SouCalculateScoreDimensionService extends ISouSpiBean {

    /**
     *
     * 计算维度得分
     * @param projectId
     * @param params 供应商物料报价信息
     * @param dimensionWeight 维度权重
     */
    void calculateAndSet(long projectId, List<? extends SouScoreDimensionContextData> params, BigDecimal dimensionWeight);

    /**
     * 通过匹配情况判断应该使用哪个
     * @param type 类型匹配
     * @return true-该算分实现类与类型匹配，可以执行{@link #calculateAndSet}
     *         false-该算分实现类与类型不匹配，不可以执行{@link #calculateAndSet}
     */
    boolean match(SouCalculateType type);

}
