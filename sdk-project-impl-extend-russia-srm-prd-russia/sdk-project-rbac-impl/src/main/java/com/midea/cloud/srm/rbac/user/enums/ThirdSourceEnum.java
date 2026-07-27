package com.midea.cloud.srm.rbac.user.enums;

/**
 * @author: 100014337
 * @describe ThirdSourceEnum
 * @date: 2023/12/8
 * @param
 * @return
 **/
public enum ThirdSourceEnum {
    /**
     * 阳光诚信
     */
    SISS("SISS", "阳光诚信");

    private String code;
    private String name;

    ThirdSourceEnum(String code, String name) {
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
