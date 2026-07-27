package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidSouModeEnum {
    /**
     * 先收技术后收商务
     */
    TECH_THEN_BUS("TECH_THEN_BUS", "先收技术后收商务"),
    SAME_TIME("SAME_TIME", "同时收标")

    ;
    private String code;
    private String name;

    SouBidSouModeEnum(String code, String name) {
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
