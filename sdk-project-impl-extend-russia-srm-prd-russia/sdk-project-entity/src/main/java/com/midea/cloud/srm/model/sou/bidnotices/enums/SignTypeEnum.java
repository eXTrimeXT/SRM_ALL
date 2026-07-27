package com.midea.cloud.srm.model.sou.bidnotices.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SignTypeEnum {
    /**
     * 落标
     */
    LOST ("LOST","落标"),
    WIN ("WIN","中标"),
    INNER ("INNER","内部");


    private String code;
    private String name;

    SignTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
