package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 发票类型
 * @author huangbf3
 */
public enum InvoiceTypeEnum {
    /**
     * 发票
     */
    INVOICE("INVOICE", "发票"),
    RED_INVOICE("RED_INVOICE", "红字发票"),

    ;
    private String code;
    private String name;

    InvoiceTypeEnum(String code, String name) {
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
