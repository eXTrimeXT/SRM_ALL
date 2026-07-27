package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums;

/**
 * 竞价.单据状态
 * 字典值: SOU_AUCT_PROJECT_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2023/07/18
 */
public enum AuctSouProjectStatusEnum {

    /**
     * 1 拟定
     */
    DRAFT,
    /**
     * 2 已作废
     */
    CANCEL,
    /**
     * 3 接受报名中
     */
    ACCEPT_SIGN_UP,
    /**
     * 4 报名截止
     */
    SIGN_UP_END,
    /**
     * 5 报价未开始
     */
    ORDER_NOT_START,
    /**
     * 6 接收报价中
     */
    ACCEPT_ORDER,
    /**
     * 7 已截止报价
     */
    ORDER_END,
    /**
     * 11 定价中
     */
    PRICING,
    /**
     * 12 定价驳回
     */
    PRICE_REJECT,
    /**
     * 13 已定价
     */
    PRICE_END,

    /**
     * 中标通知
     */
    LOA,
    /**
     * 归档
     */
    FILE;

    public static String getEnumDictCode() {
        return "SOU_AUCT_PROJECT_STATUS";
    }

}
