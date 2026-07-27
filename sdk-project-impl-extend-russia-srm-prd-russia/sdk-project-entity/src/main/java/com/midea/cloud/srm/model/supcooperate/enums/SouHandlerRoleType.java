package com.midea.cloud.srm.model.supcooperate.enums;

/**
 * 招标角色
 * @author 100014336 ganyh19
 */

public enum SouHandlerRoleType {

    /**
     * 集团招标负责人
     */
    RESPONSIBLE_PERSON_OF_GROUP_BIDDING("Responsible_person_of_group_bidding","集团招标负责人"),

    /**
     * 板块招标负责人
     */
    HEAD_OF_PLATE_BIDDING("Head_of_plate_bidding","板块招标负责人");

    private final String code;

    private final String name;

    SouHandlerRoleType(String code, String name) {
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
