package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums;

/**
 * 寻源MQL - 竞价 - 公开规则
 * PS: 字典 SOU_AUCT_SCOPE_RULE
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/08
 */
public enum AuctSouScopeRuleEnum {

    /**
     * 隐藏身份隐藏报价
     */
    HIDDEN_USER_HIDDEN_PRICE,
    /**
     * 隐藏身份公开报价
     */
    HIDDEN_USER_OPEN_PRICE,
    /**
     * 公开身份隐藏报价
     */
    OPEN_USER_HIDDEN_PRICE,
    /**
     * 公开身份公开报价
     */
    OPEN_USER_OPEN_PRICE;

    public static String getDictCode() {
        return "SOU_AUCT_SCOPE_RULE";
    }

}
