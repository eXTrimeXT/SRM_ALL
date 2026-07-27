package com.midea.cloud.srm.model.pj.sou.car.meet.enums;

/**
 * @author huangbf3
 */
public enum MeetStatus {
    /**
     * 拟定
     */
    DRAFT("拟定", "DRAFT"),
    PUBLISHED("已发布", "PUBLISHED"),
    IN_PROGRESS("进行中", "IN_PROGRESS"),
    END("结束", "END");

    private final String name;
    private final String value;

    MeetStatus(String name, String value) {
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
        for (MeetStatus o : MeetStatus.values()) {
            if (o.value.equals(value)) {
                return o.value;
            }
        }
        return null;
    }
}
