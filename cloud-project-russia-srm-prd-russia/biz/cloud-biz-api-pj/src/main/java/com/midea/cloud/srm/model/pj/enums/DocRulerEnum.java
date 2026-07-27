package com.midea.cloud.srm.model.pj.enums;

/**
 * @author huangbf3
 */
public enum DocRulerEnum {
    /**
     * 寻源公示配置
     */
    SOURCE_PUBCONFIG("SOURCE_PUBCONFIG", "寻源公示配置"),

    /**
     * 寻源评分配置
     */
    SOURCE_SCORE_CONFIG("SOURCE_SCORE_CONFIG", "寻源评分配置");

    private String code;
    private String name;

    DocRulerEnum(String code, String name) {
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
