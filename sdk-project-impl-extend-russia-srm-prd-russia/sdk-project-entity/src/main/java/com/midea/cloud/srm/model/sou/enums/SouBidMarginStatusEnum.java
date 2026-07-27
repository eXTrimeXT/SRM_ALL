package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBidMarginStatusEnum {
    /**
     * 未缴纳
     */
    NOT_PAY("NOT_PAY", "未缴纳"),
    PAY("PAY", "已缴纳"),
    FAIL_PAY("FAIL_PAY", "缴纳失败"),
    NOT_CONVER("NOT_CONVER", "未涉及"),
    CONFIRM_TODO("CONFIRM_TODO", "待确认")
    ;
    private String code;
    private String name;

    SouBidMarginStatusEnum(String code, String name) {
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
