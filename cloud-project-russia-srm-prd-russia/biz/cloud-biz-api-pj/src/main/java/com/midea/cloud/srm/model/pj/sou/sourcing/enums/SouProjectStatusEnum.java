package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.单据状态
 * 字典值: SOU_PROJECT_STATUS
 * PS: 提炼出寻源模块中核心的状态
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouProjectStatusEnum {

    /**
     * 1 拟定
     */
    DRAFT,
    /**
     * 2 接受报名中
     */
    ACCEPT_SIGN_UP,
    /**
     * 3 报名截止
     */
    SIGN_UP,
    SIGN_UP_END,
    /**
     * 4 竞价未开始
     */
    ORDER_NOT_START,
    /**
     * 5 竞价中
     */
    ACCEPT_ORDER,
    /**
     * 6 竞价截止
     */
    ORDER_END,
    /**
     * 6 评选中
     */
    EVALUATING,
    /**
     * 7 定价中
     */
    PRICING,
    /**
     * 8 已定价
     */
    PRICE_END,
    /**
     * 9 中标通知
     */
    AWARD_NOTICE,
    /**
     * 10 已归档
     */
    PLACE_FILE,

    /** 2 已作废 */
    CANCEL,
    /** 8 技术评标 */
    TECH_EVAL,
    /** 9 商务评标 */
    BUSINESS_EVAL,
    /** 12 定价驳回 */
    PRICE_REJECT,

    /** 中标通知 */
    LOA,
    /** 归档 */
    FILE;

    public static String getEnumDictCode() {
        return "SOU_PROJECT_STATUS";
    }

}
