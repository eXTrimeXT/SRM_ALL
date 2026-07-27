package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum ExtSouFileConfigTypeEnum {
    /**
     * 技术文件
     */
    TECH_BID("TECH_BID", "投标文件"),
    TECH_SOLUTION_BID("TECH_SOLUTION_BID", "技术方案文件"),
    @Deprecated
    TECH_QUA_PERF("TECH_QUA_PERF", "资质业绩文件"),
    TECH_OTHER("TECH_OTHER", "其他文件"),
    TECH_BID_SECRET("TECH_BID_SECRET", "脱敏文件"),
    BUS_BID("BUS_BID", "报价单文件"),
    BUS_OTHER("BUS_OTHER", "商务其他文件"),
    SIGN_TODO("SIGN_TODO", "待签署文件")
    ;
    private String code;
    private String name;

    ExtSouFileConfigTypeEnum(String code, String name) {
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
