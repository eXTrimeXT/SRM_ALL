package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 发票类型
 * @author huangbf3
 */
public enum SouInviteTypeEnum {
    /**
     * 寻源单
     */
    RFP("RFP", "寻源单"),
    PR("PR", "申请单"),

    ;
    private String code;
    private String name;

    SouInviteTypeEnum(String code, String name) {
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
