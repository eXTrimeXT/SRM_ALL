package com.midea.cloud.srm.model.supcooperate.enums;

import javax.annotation.Nullable;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public enum MallTypeEnum {
    /**
     * 内部供应商
     */
    CC("CC", "内部供应商"),
    /**
     * 京东平台
     */
    JD("JD", "京东平台"),
    /**
     * 淘宝
     */
    TB("TB", "淘宝"),
    // ...
    ;
    MallTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * 返回编码
     */
    private String type;

    /**
     * 返回描述
     */
    private String message;

    @Nullable
    public String getCode() {
        return this.type;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

}
