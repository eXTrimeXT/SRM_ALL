package com.midea.cloud.srm.model.sou.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum CaTenderTimeRoleEnum {
    /**
     * 技术负责人
     */
    TECH_PERSON("TECH_PERSON", "技术负责人"),
    /**
     * 招标负责人
     */
    BID_PERSON("BID_PERSON", "招标负责人"),
    /**
     * 评标组长
     */
    BID_LEADER("BID_LEADER", "评标组长")
    ;

    private String code;
    private String name;

    CaTenderTimeRoleEnum(String code, String name) {
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
