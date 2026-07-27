package com.midea.cloud.srm.model.supcooperate.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum ExtRequireFromEnum {
    /**
     * 特殊招标
     */
    SPECIAL_SOU("SPECIAL_SOU", "特殊招标"),
    /**
     * 年度
     */
    YEAR("YEAR", "年度"),
    /**
     * 月度
     */
    MONTH("MONTH", "月度"),
    /**
     * 计划外
     */
    WITHOUT_PLAN("WITHOUT_PLAN", "计划外");

    private String code;
    private String name;

    ExtRequireFromEnum(String code, String name) {
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
