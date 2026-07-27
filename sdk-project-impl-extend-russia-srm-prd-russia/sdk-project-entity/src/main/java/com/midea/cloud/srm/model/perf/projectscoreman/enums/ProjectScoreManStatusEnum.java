package com.midea.cloud.srm.model.perf.projectscoreman.enums;

/**
 * 项目化评分状态枚举
 * 项目化绩效复核明细枚举通用
 * @author huangbf3
 */
public enum ProjectScoreManStatusEnum {

    /**
     * 未提交
     */
    DRAFT,

    /**
     * 提交
     */
    SUBMITTED,

    /**
     * 审批流-驳回
     */
    FLOW_REJECT,

    /**
     * 复核-驳回
     */
    CHECK_REJECT,

    /**
     * 已撤回
     */
    WITHDRAW,

    /**
     * 已废弃
     */
    ABANDONED,

    /**
     * 审批通过
     */
    APPROVED

}
