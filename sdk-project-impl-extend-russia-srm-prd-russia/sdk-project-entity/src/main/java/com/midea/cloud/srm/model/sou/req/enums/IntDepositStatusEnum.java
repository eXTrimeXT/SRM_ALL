package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 意向金缴纳状态
 * @author huangbf3
 */
public enum IntDepositStatusEnum {
    /**
     * 未缴纳
     */
    UNPAID("UNPAID", "未缴纳"),
    /**
     * 已缴纳
     */
    PAID("PAID", "已缴纳"),
    /**
     * 不涉及
     */
    NOT_APPLICABLE("NOT_APPLICABLE", "不涉及"),
    /**
     * 待确认
     */
    TO_CONFIRM("TO_CONFIRM", "待确认"),
    ;
    private String code;
    private String name;

    IntDepositStatusEnum(String code, String name) {
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
