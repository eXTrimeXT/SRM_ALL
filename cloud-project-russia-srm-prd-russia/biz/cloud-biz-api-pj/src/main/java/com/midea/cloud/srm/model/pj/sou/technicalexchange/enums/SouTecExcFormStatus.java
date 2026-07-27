package com.midea.cloud.srm.model.pj.sou.technicalexchange.enums;

/**
 * 技术交流单据状态
 *
 * @author ex_nongtb
 * @date 2022/04/28
 */
public enum SouTecExcFormStatus {

    /**
     * 单据状态：拟定/已发布/已结束/已取消
     */
    DRAFT("拟定", "DRAFT"),
    PUBLISHED("已发布", "PUBLISHED"),
    CANCELLED("已取消", "CANCELLED"),
    HAS_ENDED("已结束", "HAS_ENDED");


    private final String name;
    private final String value;

    SouTecExcFormStatus(String name, String value) {
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
     * @param value
     * @return
     */
    public static SouTecExcFormStatus get(String value) {
        for (SouTecExcFormStatus o : SouTecExcFormStatus.values()) {
            if (o.value.equals(value)) {
                return o;
            }
        }
        return null;
    }

    /**
     * 枚举值列表是否包含指定code
     *
     * @param code
     * @return true or false
     */
    public static boolean isContain(String code) {
        return (get(code) != null);
    }

}
