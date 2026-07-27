package com.midea.cloud.srm.model.pj.enums;

/**
 * @author huangbf3
 */
public enum SouScoreConfigItemEnum {
    /**
     * 备注
     */
    QUA_REVIEW("QUA_REVIEW", "资格评审"),
    BUS_REVIEW("BUS_REVIEW", "商务评审"),
    TEH_REVIEW("TEH_REVIEW", "技术评审"),
    COM_REVIEW("COM_REVIEW", "综合评审");
    private String code;
    private String name;

    SouScoreConfigItemEnum(String code, String name) {
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
