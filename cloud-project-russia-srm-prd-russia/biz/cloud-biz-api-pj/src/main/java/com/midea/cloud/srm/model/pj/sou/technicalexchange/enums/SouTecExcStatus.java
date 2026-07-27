package com.midea.cloud.srm.model.pj.sou.technicalexchange.enums;

/**
 * 技术交流单据状态
 *
 * @author ex_nongtb
 * @date 2022/04/28
 */
public enum SouTecExcStatus {

    /**
     * 交流类型
     */
    EQUIPMENT("设备", "EQUIPMENT"),
    ENGINEERING("工程", "ENGINEERING"),
    SERVICE("服务", "SERVICE");


    private final String name;
    private final String value;

    SouTecExcStatus(String name, String value) {
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
    public static SouTecExcStatus get(String value) {
        for (SouTecExcStatus o : SouTecExcStatus.values()) {
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
