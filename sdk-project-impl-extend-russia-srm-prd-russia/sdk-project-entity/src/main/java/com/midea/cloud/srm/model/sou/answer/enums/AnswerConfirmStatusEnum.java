package com.midea.cloud.srm.model.sou.answer.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum AnswerConfirmStatusEnum {

    /**
     * 未确认
     */
    UNCOMFIRMED("UNCOMFIRMED", "未确认"),

    COMFIRMED("COMFIRMED", "已确认");
    private String code;
    private String name;

    AnswerConfirmStatusEnum(String code, String name) {
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
