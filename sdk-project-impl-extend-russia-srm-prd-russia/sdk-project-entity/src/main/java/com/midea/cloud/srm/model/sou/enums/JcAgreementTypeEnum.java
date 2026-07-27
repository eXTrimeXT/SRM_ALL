package com.midea.cloud.srm.model.sou.enums;

/**
 * @author 100014336 ganyh19
 */
public enum JcAgreementTypeEnum {

    /**
     * 合同协议
     */
    CONTRACT("合同协议","合同协议"),
    /**
     * 集采协议
     */
    CENT_PURCHASE("集采协议","集采协议");

    private final String code;

    private final String desc;

    JcAgreementTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
