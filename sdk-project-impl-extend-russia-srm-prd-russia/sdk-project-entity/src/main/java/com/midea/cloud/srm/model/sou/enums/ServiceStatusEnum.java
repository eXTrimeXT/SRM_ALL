package com.midea.cloud.srm.model.sou.enums;

/**
 * @Author: panmq
 * @Date: 2024/03/06/ $
 * @Description:
 */
public enum ServiceStatusEnum {

    /**
     * 合格
     */
    QUALIFIED("QUALIFIED", "合格"),
    /**
     * 认证中
     */
    VERIFY("VERIFY", "认证中")
    ;
    private String code;
    private String name;

    ServiceStatusEnum(String code, String name) {
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
