package com.midea.cloud.srm.model.sou.req.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum PreBidNoticeStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),
    /**
     * 已发布
     */
    ISSUED("ISSUED", "已发布")
    ;
    private String code;
    private String name;

    PreBidNoticeStatusEnum(String code, String name) {
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
