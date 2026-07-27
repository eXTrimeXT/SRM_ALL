package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidpriceInvoiceTypeEnum {
    /**
     * 增值税专用发票
     */
    VAT_SPECIAL("VAT_SPECIAL", "增值税专用发票"),
    VAT_ORDINARY("VAT_ORDINARY", "增值税普通发票"),
    OFFICIAL("OFFICIAL", "正式商业发票");
    private String code;
    private String name;

    SouBidpriceInvoiceTypeEnum(String code, String name) {
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
