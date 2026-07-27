package com.midea.cloud.srm.model.sou.sourcing.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouProjectStatusEnum {
    /**
     * 备注
     */
    DRAFT,
    CANCEL,
    ACCEPT_SIGN_UP,
    SIGN_UP_END,
    ORDER_NOT_START,
    ACCEPT_ORDER,
    ORDER_END,
    TECH_EVAL,
    BUSINESS_EVAL,
    EVALUATING,
    PRICING,
    PRICE_REJECT,
    PRICE_END,
    DRAW_UP,
    TECH_BID,
    TECH_BID_END,
    TECH_BID_OPEN,
    TECH_BID_EVA,
    BUS_BID,
    BUS_BID_END,
    BUS_BID_OPEN,
    CONFIRM_BID,
    WIN_LOSS_NOTICE,
    ARCHIVE_TODO,
    ARCHIVE_DONE,
    ABANDON;
}
