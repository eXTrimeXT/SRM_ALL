package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.评选方式
 * 字典值: SOU_SCORE_RULE_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouScoreRuleTypeEnum {

    /**
     * 合理低价
     */
    MIN_PRICE,
    /**
     * 合理高价
     */
    MAX_PRICE,
    /**
     * 综合评分
     */
    COMPOSITE_PRICE;

    public static String getEnumDictCode() {
        return "SOU_SCORE_RULE_TYPE";
    }

}
