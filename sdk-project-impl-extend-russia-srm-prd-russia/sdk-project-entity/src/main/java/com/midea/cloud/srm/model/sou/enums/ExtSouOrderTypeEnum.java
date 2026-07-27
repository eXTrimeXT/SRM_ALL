package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum ExtSouOrderTypeEnum {
    /**
     * 商务
     */
    BUSINESS("BUSINESS", "商务"),
    TECHNOLOGY_BUSINESS("TECHNOLOGY_BUSINESS", "技术+商务");
    ;
    private String code;
    private String name;

    ExtSouOrderTypeEnum(String code, String name) {
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
