package com.midea.cloud.srm.model.sou.designplans.enums;

/**
 * @author ex_liuxy46
 */

public enum PaaAdjustEnums {

    //状态
    DRAFT ("DRAFT","拟定"),
    SUBMIT ("SUBMIT","提交"),
    PASS ("PASS","审批通过"),
    REJECT ("REJECT","审批不通过"),
    ABANDONED("ABANDONED", "已废弃"),
    WITHDRAW("WITHDRAW", "已撤回");

    private String code;
    private String name;

    PaaAdjustEnums(String code, String name) {
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
