package com.midea.cloud.srm.model.pj.sou.car.meet.enums;

/**
 * 议题决议状态
 *
 * @author ex_nongtb
 * @date 2022/06/21
 */
public enum TopicVoteStatus {

    /**
     * 备注
     */
    APPROVAL("批准", "APPROVAL"),
    REJECT("驳回", "REJECT"),
    WAIT_APPROVAL("待决策", "WAIT_APPROVAL");

    private String name;
    private String value;

    TopicVoteStatus(String name, String value) {
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
        for (TopicVoteStatus o : TopicVoteStatus.values()) {
            if (o.value.equals(value)) {
                return o.value;
            }
        }
        return null;
    }
}
