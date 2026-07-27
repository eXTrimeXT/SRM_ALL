package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 寻源单意向金开票状态
 * @author huangbf3
 */
public enum IntDepositInvoiceStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),
    INVOICING("INVOICING", "开票中"),
    INVOICED("INVOICED", "已开票"),
    FAIL_INVOICED("FAIL_INVOICED", "开票失败"),

    ;
    private String code;
    private String name;

    IntDepositInvoiceStatusEnum(String code, String name) {
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
