package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouMarginRecordTypeEnum {
    /**
     * 扣款
     */
    CHARGE("CHARGE", "扣款"),
    REFUND("REFUND", "退款")

    ;
    private String code;
    private String name;

    SouMarginRecordTypeEnum(String code, String name) {
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
