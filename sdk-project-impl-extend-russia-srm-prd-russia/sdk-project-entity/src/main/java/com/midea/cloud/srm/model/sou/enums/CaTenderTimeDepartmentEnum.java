package com.midea.cloud.srm.model.sou.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum CaTenderTimeDepartmentEnum {
    /**
     * 申请部门
     */
    APPLY("APPLY", "申请部门"),
    /**
     * 招标部
     */
    BID("BID", "招标部")
    ;

    private String code;
    private String name;

    CaTenderTimeDepartmentEnum(String code, String name) {
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
