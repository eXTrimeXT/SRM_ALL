package com.midea.cloud.srm.model.sou.question.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum QuestionTypeEnum {
    /**
     * 寻源
     */
    SOU("SOU", "寻源"),

    REQ("REQ", "采购申请");

    private String code;
    private String name;

    QuestionTypeEnum(String code, String name) {
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
