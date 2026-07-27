package com.midea.cloud.srm.model.pj.enums;

/**
 * @author huangbf3
 */
public enum DictCodeEnum {
    /**
     * 寻源公示配置单据状态
     */
    SOURCE_PUBCONFIG_STATUS("SOURCE_PUBCONFIG_STATUS", "寻源公示配置单据状态"),
    /**
     * 评分配置打分项
     */
    SOU_SCORE_CONFIG_ITEM("SOU_SCORE_CONFIG_ITEM", "评分配置打分项")
    ;

    private String code;
    private String name;

    DictCodeEnum(String code, String name) {
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
