package com.midea.cloud.srm.model.pj.enums;

/**
 * @Author: panmq
 * @Date: 2024/03/19/ $
 * @Description:
 */
public enum TodowithbpmStatusEnum {
    /**
     * 待办
     */
    TODO("todo", "待办"),
    /**
     * 已办
     */
    HAVEDONE("havedone", "已办");
    private String code;
    private String name;

    TodowithbpmStatusEnum(String code, String name) {
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
