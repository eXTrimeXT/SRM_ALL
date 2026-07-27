package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidPlanTypeEnum {
    /**
     * 计划时间
     */
    PLAN("PLAN", "计划时间"),
    ACTUAL("ACTUAL", "实际时间")
    ;
    private String code;
    private String name;

    SouBidPlanTypeEnum(String code, String name) {
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
