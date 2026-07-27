package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum TechScoreStatusEnum {
    /**
     * 未完成
     */
    UNFINISHED("UNFINISHED", "未评分"),
    FINISHED("FINISHED", "已评分"),
    /**
     * 已完成
     */
    REJECT("REJECT", "已驳回");
    ;

    private String code;
    private String name;

    TechScoreStatusEnum(String code, String name) {
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
