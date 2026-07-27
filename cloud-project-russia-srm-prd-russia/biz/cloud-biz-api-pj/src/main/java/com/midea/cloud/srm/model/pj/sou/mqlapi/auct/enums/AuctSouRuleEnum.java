package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums;

/**
 * 寻源MQL - 竞价 - 竞价规则
 * 字典: SOU_AUCT_RULE
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/08
 */
public enum AuctSouRuleEnum {

    /**
     * 允许相同价格
     */
    ALLOW_SAME_PRICE,
    /**
     * 不允许报相同价格
     */
    NO_ALLOW_SAME_PRICE,
    /**
     * 报价需超越第一名
     */
    EXCEED_FIRST_PRICE;

    public static String getDictCode() {
        return "SOU_AUCT_RULE";
    }

}
