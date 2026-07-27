package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 寻源模块 - 智能评分服务接口定义
 *
 * @author zhangwk12@midea.com
 * @since 2022/03/31
 */
public interface ISouCalculateScoreManage {


    /**
     * 处理评选算分排名
     *
     * @param projectId     寻源单ID{@link SouProject#getProjectId}
     * @param souType       寻源模块{@link SouTypeEnum}
     * @param orderWay      报价方式(单项/组合)
     * @param scoreRuleType 评分规则(合理低价/高价/综合)
     * @param params        供应商报价信息
     * @param scoreRuleId   如果报价方式是综合评分法，则该参数必须有值{@link SouScoreRule#getScoreRuleId}
     */
    void calculateAndSort(long projectId,
                          String souType,
                          SouOrderWayEnum orderWay,
                          SouScoreRuleTypeEnum scoreRuleType,
                          List<? extends SouScoreDimensionContextData> params,
                          @Nullable Long scoreRuleId);

}
