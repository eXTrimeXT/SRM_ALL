package com.midea.cloud.srm.model.sou.enums;
/**
 * 备注
 * @author huangbf3
 */
public enum ColumnSourceEnum {
    /**
     * 采购商
     */
    BUYER("BUYER", "采购商"),
    VENDOR("VENDOR", "供应商")
    ;

    private String code;
    private String name;

    ColumnSourceEnum(String code, String name) {
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
