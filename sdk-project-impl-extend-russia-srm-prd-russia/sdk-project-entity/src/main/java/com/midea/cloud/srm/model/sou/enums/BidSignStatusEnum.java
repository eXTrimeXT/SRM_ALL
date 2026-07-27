package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum BidSignStatusEnum {
    /**
     * "已签署"
     */
    SIGN("SIGN", "已签署"),
    NOT_SIGN("NOT_SIGN", "已签署")
    ;
    private String code;
    private String name;

    BidSignStatusEnum(String code, String name) {
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
