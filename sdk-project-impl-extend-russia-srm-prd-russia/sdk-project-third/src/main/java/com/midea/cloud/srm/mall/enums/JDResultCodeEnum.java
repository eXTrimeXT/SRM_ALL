package com.midea.cloud.srm.mall.enums;


import javax.annotation.Nullable;

/**
 * 京东接口返回码枚举
 */
public enum JDResultCodeEnum {
    B0000("0000", "操作成功"),
    CONFIRMATION("0001", "下单成功"),
    ORDER_CANCEL_SUCCESS("0002", "取消订单成功"),
    B0010("0010", "池中商品已查询完毕"),
    ;
    JDResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 返回编码
     */
    private String code;

    /**
     * 返回描述
     */
    private String message;

    @Nullable
    public String getCode() {
        return this.code;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }
}
