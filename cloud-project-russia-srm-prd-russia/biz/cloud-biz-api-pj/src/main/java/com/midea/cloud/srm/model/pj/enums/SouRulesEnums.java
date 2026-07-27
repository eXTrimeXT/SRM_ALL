package com.midea.cloud.srm.model.pj.enums;

/**
 * @author ex_yipeng
 */
public enum SouRulesEnums {

    /**
     * 备注
     */
    FORWARD_RULE("FORWARD_RULE", "正向规则"),
    REVERSE_RULE("REVERSE_RULE", "反向规则");

    private String code;
    private String name;

    private SouRulesEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
