package com.midea.cloud.srm.cm.contract.service;

import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.dto.PerPlanExt;
import com.midea.cloud.srm.model.contract.dto.PerPlanMilestoneExtDto;

import java.io.Serializable;
import java.util.List;

/**
 * @author 100014336 ganyh19
 */
public interface IContractPerPlanExtService {



    /**
     *
     * 通过里程碑ID查询履约计划是否需要评分
     * @param milestoneId 里程碑ID
     * @return
     */
    Boolean isNeedPerfEvalByMilestoneId(Long milestoneId);

    /**
     * 获取里程碑的履约计划
     * @param perPlanMilestone
     * @return
     */
    PerPlan getPerPanByMilestoneId(PerPlanMilestoneExtDto perPlanMilestone);

    /**
     * 写入是否已经创建项目
     * @param contractNo
     * @param milestoneType
     * @param enable
     * @return
     */
    List<Serializable> setHasCreatePerf(String contractNo, String milestoneType, Enable enable);

    /**
     *  是否需要评分
     * @param perPlan
     * @param milestone
     * @return
     */
    Boolean isNeedPerfEval(PerPlanExt perPlan, PerPlanMilestoneExtDto milestone);

    /**
     * cancel
     * @param perPlanExt
     */
    void cancel(PerPlanExt perPlanExt);
}
