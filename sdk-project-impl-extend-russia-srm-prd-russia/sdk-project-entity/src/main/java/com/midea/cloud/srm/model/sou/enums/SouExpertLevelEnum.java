package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouExpertLevelEnum {
    /**
     * 高级
     */
    HIGHER("HIGHER", "高级"),
    COMMON("COMMON", "普通");
    ;

    private String code;
    private String name;

    SouExpertLevelEnum(String code, String name) {
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
