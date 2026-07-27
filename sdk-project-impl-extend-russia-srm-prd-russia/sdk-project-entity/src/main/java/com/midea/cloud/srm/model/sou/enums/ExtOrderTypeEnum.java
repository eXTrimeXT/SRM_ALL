package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum ExtOrderTypeEnum {
    /**
     * 技术投标
     */
    TECH("TECH", "技术投标"),
    BUS("BUS", "商务投标");

    private String code;
    private String name;

    ExtOrderTypeEnum(String code, String name) {
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
