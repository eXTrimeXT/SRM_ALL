package com.midea.cloud.srm.model.sou.req.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum PreBidFeedbackStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),
    /**
     * 待提交报告
     */
    ISSUED("ISSUED", "待提交报告"),
    /**
     * 已完成
     */
    FINISHED("FINISHED", "已完成"),
    /**
     * 已废弃
     */
    ABANDONED("ABANDONED", "已废弃"),
    ;
    private String code;
    private String name;

    PreBidFeedbackStatusEnum(String code, String name) {
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
