package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.报价类型
 * 字典值: SOU_ORDER_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouOrderTypeEnum {

    /**
     * 普通
     */
    SIMPLE,
    /**
     * 公式
     */
    FORMULA,
    /**
     * 模型
     */
    TEMPLATE,
    /**
     * 料费分离
     */
    MATERIAL_COST_SEPARATION;

    public static String getEnumDictCode() {
        return "SOU_ORDER_TYPE";
    }

}
