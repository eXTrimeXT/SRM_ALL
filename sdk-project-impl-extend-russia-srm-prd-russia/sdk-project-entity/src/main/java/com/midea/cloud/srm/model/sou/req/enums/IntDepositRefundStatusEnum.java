package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 意向金退款状态
 * @author huangbf3
 */
public enum IntDepositRefundStatusEnum {
    /**
     * 未退款
     */
    NOT_REFUNDED("NOT_REFUNDED", "未退款"),
    REFUNDING("REFUNDING", "退款中"),
    REFUNDED("REFUNDED", "已退款"),
    NOT_APPLICABLE("NOT_APPLICABLE", "不涉及"),
    REFUND_FAILED("REFUND_FAILED", "退款失败"),
    ;
    private String code;
    private String name;

    IntDepositRefundStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
