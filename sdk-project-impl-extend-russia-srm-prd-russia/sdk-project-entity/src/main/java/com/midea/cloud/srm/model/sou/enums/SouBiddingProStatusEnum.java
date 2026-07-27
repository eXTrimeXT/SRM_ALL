package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouBiddingProStatusEnum {
    /**
     * 拟定
     */
    DRAW_UP("DRAW_UP", "拟定"),
    TECH_BID("TECH_BID", "技术标投标中"),
    TECH_BID_END("TECH_BID_END", "技术标已截止"),
    TECH_BID_OPEN("TECH_BID_OPEN", "技术已开标"),
    TECH_BID_EVA("TECH_BID_EVA", "技术评标中"),
    TECH_BID_EVA_DONE("TECH_BID_EVA_DONE", "已技术评分"),
    BUS_BID("BUS_BID", "商务标投标中"),
    BUS_BID_END("BUS_BID_END", "商务标已截止"),
    BUS_BID_OPEN("BUS_BID_OPEN", "商务已开标"),
    CONFIRM_BID("CONFIRM_BID", "定标中"),
    WIN_LOSS_NOTICE("WIN_LOSS_NOTICE", "待中/落标通知"),
    NOTICE_ING("NOTICE_ING", "待中/落标通知处理中"),
    ARCHIVE_TODO("ARCHIVE_TODO", "待归档"),
    ARCHIVE_DONE("ARCHIVE_DONE", "已归档"),

    ABANDON("ABANDON", "已废弃")

    ;
    private String code;
    private String name;

    SouBiddingProStatusEnum(String code, String name) {
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
