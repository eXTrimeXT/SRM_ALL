package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouExpertRangeEnum {
    /**
     * 按集团抽取
     */
    GROUP("GROUP", "按集团抽取"),
    BU("BU", "按板块抽取"),
    OU("OU", "按公司抽取")
    ;
    private String code;
    private String name;

    SouExpertRangeEnum(String code, String name) {
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
