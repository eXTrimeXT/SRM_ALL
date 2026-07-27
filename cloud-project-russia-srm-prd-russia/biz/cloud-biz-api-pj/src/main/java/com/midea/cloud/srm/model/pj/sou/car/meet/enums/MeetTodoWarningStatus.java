package com.midea.cloud.srm.model.pj.sou.car.meet.enums;

/**
 * 待办预警状态
 *
 * @author ex_nongtb
 * @date 2022/05/19
 */
public enum MeetTodoWarningStatus {

    /**
     *
     */
    PENDING("待处理", "PENDING"),
    DELAYED("已延迟", "DELAYED"),
    COMPLETED("已完成", "COMPLETED");

    private final String name;
    private final String value;

    MeetTodoWarningStatus(String name, String value) {
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
        for (MeetTodoWarningStatus o : MeetTodoWarningStatus.values()) {
            if (o.value.equals(value)) {
                return o.value;
            }
        }
        return null;
    }
}
