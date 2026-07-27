package com.midea.cloud.srm.model.pj.sou.inq.enums;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;

/**
 * 简易询价.单据状态
 * 字典值: SOU_PROJECT_STATUS
 * PS: 来源于 {@link SouProjectStatusEnum}，但没有"商务评标"状态，因为简易询价是报价截止后自动做商务开标的
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum InqSouProjectStatusEnum {

    /**
     * 拟定
     */
    DRAFT,
    /**
     * 已作废
     */
    CANCEL,
    /**
     * 报价未开始
     */
    ORDER_NOT_START,
    /**
     * 接收报价中
     */
    ACCEPT_ORDER,
    /**
     * 已截止报价
     */
    ORDER_END,
    /**
     * 评选中
     */
    EVALUATING,
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
    PRICE_END;

    public static String getEnumDictCode() {
        return "SOU_PROJECT_STATUS";
    }

}
