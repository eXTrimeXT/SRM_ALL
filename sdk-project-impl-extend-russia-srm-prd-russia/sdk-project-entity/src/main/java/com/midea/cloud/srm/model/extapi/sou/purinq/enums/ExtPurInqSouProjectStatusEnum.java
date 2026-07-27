package com.midea.cloud.srm.model.extapi.sou.purinq.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum ExtPurInqSouProjectStatusEnum {

    /**
     * 拟定
     */
    DRAFT,
    /**
     * 已作废
     */
    CANCEL,
    /**
     * 接受报名中
     */
//    ACCEPT_SIGN_UP,
    /**
     * 报名截止
      */
//    SIGN_UP_END,
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
     * 技术评标
     */
//    TECH_EVAL,
    /**
     * 商务评标
      */
//    BUSINESS_EVAL,
    /**
     * 评选中
      */
    EVALUATING,
    /**
     * 定价中
     */
//    PRICING,
    /**
     * 定价驳回
      */
//    PRICE_REJECT,
    /**
     * 已定价(询价完成)
     */
    PRICE_END;

    public static String getEnumDictCode() {
        return "EXT_PUR_INQ_SOU_PROJECT_STATUS";
    }

}
