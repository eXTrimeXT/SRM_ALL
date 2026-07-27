package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouScoreDimensionCodeEnum {
    /**
     * 技术评分
     */
    SOU_TECH("SOU_TECH", "技术评分");
    ;

    private String code;
    private String name;

    SouScoreDimensionCodeEnum(String code, String name) {
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
