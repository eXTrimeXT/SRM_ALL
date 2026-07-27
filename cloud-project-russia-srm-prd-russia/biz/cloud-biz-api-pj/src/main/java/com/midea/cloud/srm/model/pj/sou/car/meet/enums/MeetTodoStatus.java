package com.midea.cloud.srm.model.pj.sou.car.meet.enums;

/**
 * 待办状态
 *
 * @author ex_nongtb
 * @date 2022/06/15
 */
public enum MeetTodoStatus {

    /**
     * 拟定
     */
    DRAFT("拟定", "DRAFT"),
    SUBMITTED("已提交", "SUBMITTED"),
    COMPLETED("已完成", "COMPLETED");

    private final String name;
    private final String value;

    MeetTodoStatus(String name, String value) {
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
        for (MeetTodoStatus o : MeetTodoStatus.values()) {
            if (o.value.equals(value)) {
                return o.value;
            }
        }
        return null;
    }
}
