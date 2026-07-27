package com.midea.cloud.srm.model.pj.sou.car.meet.enums;

/**
 * @author huangbf3
 */
public enum TopicResolutionStatus {

    /**
     * 备注
     */
    APPROVAL("批准", "APPROVAL"),
    REJECT("驳回", "REJECT"),
    UPGRADE("升级", "UPGRADE"),
    DEFAULT("默认值", "DEFAULT");

    private String name;
    private String value;

    TopicResolutionStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    /**
     * 通过指定value值获取枚举
     *
     * @param value 价值
     * @return {@link String}
     */
    public static String get(String value) {
        for (TopicResolutionStatus o : TopicResolutionStatus.values()) {
            if (o.value.equals(value)) {
                return o.value;
            }
        }
        return null;
    }
}
