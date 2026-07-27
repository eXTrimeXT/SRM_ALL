package com.midea.cloud.srm.model.sou.enums;
/**
 * 备注
 * @author huangbf3
 */
public enum SouRecommvendorTypeEnum {
    /**
     * 追加供应商
     */
    ADD("ADD", "追加供应商"),
    RECOMM("RECOMM", "推荐供应商")
    ;

    private String code;
    private String name;

    SouRecommvendorTypeEnum(String code, String name) {
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
