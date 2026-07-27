package com.midea.cloud.srm.model.pj.ccapisettleacountings.enums;

/**
 * @Author: panmq
 * @Date: 2024/04/10/ $
 * @Description: 客商类型（1-客户，2-供应商）
 */
public enum ApiSettleAcountingPartnerTypeEnum {
    /**
     * 客户
     */
    CLIENT("1", "客户"),
    /**
     * 供应商
     */
    VENDOR("2", "供应商");

    private String code;
    private String name;

    ApiSettleAcountingPartnerTypeEnum(String code, String name) {
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
