package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.报价单状态
 * 字典值: SOU_ORDER_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouOrderStatusEnum {

    /**
     * 未投标
     */
    DRAFT,
    /**
     * 已投标
     */
    SUBMISSION,
    /**
     * 已撤回
     */
    WITHDRAW,
    /**
     * 作废
     */
    CANCEL;

    public static String getEnumDictCode() {
        return "SOU_ORDER_STATUS";
    }

}
