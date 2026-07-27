package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum ExtSouGroupRoleEnum {
    /**
     * 招标负责人
     */
    PRINCIPAL("PRINCIPAL", "招标负责人"),
    MINISTER("MINISTER", "招标部长"),
    LEADER("LEADER", "招标组长"),
    MEMBER("MEMBER", "招标组员");
    ;

    private String code;
    private String name;

    ExtSouGroupRoleEnum(String code, String name) {
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
