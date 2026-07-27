package com.midea.cloud.srm.model.pj.sou.car.meet.enums;

/**
 * @author huangbf3
 */
public enum MeetTopicStatus {

    /**
     *
     */
    DRAFT("拟定", "DRAFT"),
    APPLY("上会申请中", "APPLY"),
    MEETING("上会中", "MEETING"),
    FINISH("上会完成", "FINISH");


    private String name;
    private String value;

    MeetTopicStatus(String name, String value) {
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
        for (MeetTopicStatus o : MeetTopicStatus.values()) {
            if (o.value.equals(value)) {
                return o.value;
            }
        }
        return null;
    }
}
