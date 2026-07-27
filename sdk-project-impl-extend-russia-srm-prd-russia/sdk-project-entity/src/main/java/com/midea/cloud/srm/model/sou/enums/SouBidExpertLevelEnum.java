package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidExpertLevelEnum {
    /**
     * 高级
     */
    SENIOR("SENIOR", "高级"),
    NORMAL("NORMAL", "普通"),
    ;
    private String code;
    private String name;

    SouBidExpertLevelEnum(String code, String name) {
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
