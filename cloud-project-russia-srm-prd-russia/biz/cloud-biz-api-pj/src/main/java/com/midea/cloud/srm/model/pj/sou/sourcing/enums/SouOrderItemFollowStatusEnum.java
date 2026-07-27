package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源核心 - 报价明细后续单据情况状态
 * PS: 字典值 SOU_ORDER_ITEM_FOLLOW_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2023/09/05
 */
public enum SouOrderItemFollowStatusEnum {

    /**
     * 生效
     */
    VALID,
    /**
     * 失效
     */
    INVALID;

    public static String getEnumDictCode() {
        return "SOU_ORDER_ITEM_FOLLOW_STATUS";
    }

}
