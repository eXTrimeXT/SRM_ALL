package com.midea.cloud.srm.model.sou.question.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum QuestionStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),

    SUBMITTED("SUBMITTED", "已提交"),

    REPLAYED("REPLAYED", "已回复"),

    ABANDON("ABANDON", "已废弃");

    private String code;
    private String name;

    QuestionStatusEnum(String code, String name) {
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
