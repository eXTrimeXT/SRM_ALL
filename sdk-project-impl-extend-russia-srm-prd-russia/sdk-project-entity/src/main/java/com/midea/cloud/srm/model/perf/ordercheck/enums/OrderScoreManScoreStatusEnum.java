package com.midea.cloud.srm.model.perf.ordercheck.enums;

/**
 * 评分状态
 * @author huangbf3
 */
public enum OrderScoreManScoreStatusEnum {

    /**
     * 未提交
     */
    DRAFT,

    /**
     * 提交
     */
    SUBMITTED,

    /**
     * 驳回
     */
    REJECT,

    /**
     * 已计算得分
     */
    CALCULATED_SCORE

}
