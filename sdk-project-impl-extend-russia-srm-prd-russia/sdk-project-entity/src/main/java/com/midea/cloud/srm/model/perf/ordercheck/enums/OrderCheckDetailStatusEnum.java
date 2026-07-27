package com.midea.cloud.srm.model.perf.ordercheck.enums;

/**
 * 订单化复核-明细状态
 * @author huangbf3
 */
public enum OrderCheckDetailStatusEnum {

    /**
     * 未提交
     */
    DRAFT,

    /**
     * 部分提交
     */
    PART_SUBMITTED,

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
