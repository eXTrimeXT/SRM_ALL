package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum MarginHanderModeEnum {
    /**
     * 线上核对缴纳
     */
    ON_LINE("ON_LINE", "线上核对缴纳"),
    OFF_LINE("OFF_LINE", "线下核对缴纳"),
    CAN_NOTPAY("CAN_NOTPAY", "允许不缴纳"),
    ERROR_PAY("ERROR_PAY", "缴纳有误");

    private String code;
    private String name;

    MarginHanderModeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
