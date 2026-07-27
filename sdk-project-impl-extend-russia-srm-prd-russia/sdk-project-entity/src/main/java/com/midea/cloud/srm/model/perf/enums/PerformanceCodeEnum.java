package com.midea.cloud.srm.model.perf.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum PerformanceCodeEnum {
    /**
     * 履约项目
     */
    PROJECT("PROJECT", "履约项目"),
    /**
     * 履约节点
     */
    NODE("NODE", "履约节点");

    private String code;
    private String name;

    PerformanceCodeEnum(String code, String name) {
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
