package com.midea.cloud.srm.model.pj.enums;

/**
 * @author huangbf3
 */
public enum SourcePubconfigStatusEnum {
    /**
     * 备注
     */
    DRAFT("DRAFT", "拟定"),
    VALID("VALID", "生效"),
    INVALID("INVALID", "失效"),
    ;
    private String code;
    private String name;

    SourcePubconfigStatusEnum(String code, String name) {
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
