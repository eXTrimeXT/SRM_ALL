package com.midea.cloud.srm.model.sou.fixprice.enums;

/**
 * 定价单 - 单据状态
 * @author huangbf3
 * PS: EXT_FIX_PRICE_STATUS
 */
public enum ExtFixPriceStatusEnum {
    /**
     * 拟定
     */
    DRAFT,
    /**
     * 审批中
     */
    SUBMITTED,
    /**
     * 已驳回
     */
    REJECTED,
    /**
     * 已撤回
     */
    WITHDRAW,
    /**
     * 已废弃
     */
    ABANDONED,
    /**
     * 已审批
     */
    APPROVED;

}
