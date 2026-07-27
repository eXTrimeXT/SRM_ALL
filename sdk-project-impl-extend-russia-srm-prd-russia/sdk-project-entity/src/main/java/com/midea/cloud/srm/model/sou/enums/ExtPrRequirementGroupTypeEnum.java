package com.midea.cloud.srm.model.sou.enums;

/**
 * 招标计划池 负责人类型
 * @author panmq
 */
public enum ExtPrRequirementGroupTypeEnum {
    /**
     * 招标负责人
     */
    SOU("SOU", "招标负责人"),

    /**
     * 技术负责人
     */
    TECH("TECH", "技术负责人"),

    /**
     * 供应商负责人
     **/
    VENDOR("VENDOR", "供应商负责人")
    ;

    private String code;
    private String name;

    ExtPrRequirementGroupTypeEnum(String code, String name) {
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
