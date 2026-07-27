package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源核心 - 报价明细转价格审批单状态
 * 字典值: SOU_ORDER_ITEM_PRICE_APPROVAL_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2023/09/05
 */
public enum SouOrderItemPriceApprovalStatusEnum {

    /**
     * 拟定
     */
    DRAFT,
    /**
     * 定价中
     */
    PRICING,
    /**
     * 定价驳回
     */
    PRICE_REJECT,
    /**
     * 已定价
     */
    PRICED;

    public static String getEnumDictCode() {
        return "SOU_ORDER_ITEM_PRICE_APPROVAL_STATUS";
    }

}
