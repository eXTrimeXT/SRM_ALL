package com.midea.cloud.srm.biz.pj.contractlock.enums;

/**
 *
 * @author gw00311146
 */
public enum OrderFileSignTypeEnum {
    /**
     * 投标文件签署类型
     */
    TYPE_BID_TECH("BID_TECH", "投标文件线上签署"),
    TYPE_BID_BUSINESS("BID_BUSINESS", "商务报价线上签署");

    private final String code;
    private final String name;


    OrderFileSignTypeEnum(String code, String name) {
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
