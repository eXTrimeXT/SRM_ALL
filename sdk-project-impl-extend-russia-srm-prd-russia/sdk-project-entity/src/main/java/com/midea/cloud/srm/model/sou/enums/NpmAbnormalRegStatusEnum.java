package com.midea.cloud.srm.model.sou.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum NpmAbnormalRegStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),
    /**
     * 提交
     */
    SUBMIT("SUBMIT", "提交");

    private String code;
    private String name;

    NpmAbnormalRegStatusEnum(String code, String name) {
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
