package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 报名处理方式
 * @author huangbf3
 */
public enum SouApplyHandleTypeEnum {
    /**
     * 可延期缴纳意向金
     */
    APPLY_CAN_DELAY_INT_DEPOSIT("APPLY_CAN_DELAY_INT_DEPOSIT", "允许报名，可延期缴纳意向金。"),
    APPLY_CAN_NO_INT_DEPOSIT("APPLY_CAN_NO_INT_DEPOSIT", "允许报名，可不缴纳意向金"),
    CAN_NOT_APPLY("CAN_NOT_APPLY", "不允许报名"),
    ALREADY_PAID("ALREADY_PAID", "已缴纳，直接报名"),
    ;
    private String code;
    private String name;

    SouApplyHandleTypeEnum(String code, String name) {
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
