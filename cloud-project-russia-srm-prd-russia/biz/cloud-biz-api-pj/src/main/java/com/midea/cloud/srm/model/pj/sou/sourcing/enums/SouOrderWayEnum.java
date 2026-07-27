package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.报价方式
 * 字典值: SOU_ORDER_WAY
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouOrderWayEnum {

    /**
     * 单项
     */
    SINGLE,
    /**
     * 组合
     */
    COMBINED;

    public static String getEnumDictCode() {
        return "SOU_ORDER_WAY";
    }

}
