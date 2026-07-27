package com.midea.cloud.srm.model.sou.req.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum VendorFeedbackStatusEnum {
    /**
     * 未反馈
     */
    NO_FEEDBACK("NO_FEEDBACK", "未反馈"),
    /**
     * 已反馈
     */
    ALREADY_FEEDBACK("ALREADY_FEEDBACK", "已反馈"),
    /**
     * 驳回
     */
    REJECT("REJECT", "驳回"),
    ;
    private String code;
    private String name;

    VendorFeedbackStatusEnum(String code, String name) {
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
