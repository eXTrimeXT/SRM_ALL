package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums;

/**
 * 竞价.评选方式
 * 字典值: SOU_AUCT_SCORE_RULE_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2023/07/19
 */
public enum AuctSouScoreRuleTypeEnum {

    /**
     * 竞拍(价低者得)
     */
    MIN_PRICE,
    /**
     * 拍卖(价高者得)
     */
    MAX_PRICE;

    public static String getEnumDictCode() {
        return "SOU_AUCT_SCORE_RULE_TYPE";
    }

}
