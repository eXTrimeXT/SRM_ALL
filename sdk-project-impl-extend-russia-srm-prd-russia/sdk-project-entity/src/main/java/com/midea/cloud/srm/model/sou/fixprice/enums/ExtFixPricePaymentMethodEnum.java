package com.midea.cloud.srm.model.sou.fixprice.enums;

/**
 * 定价单 - 付款方式
 * PS: EXT_SOU_FIX_PRICE_PAYMENT_METHOD
 * @author huangbf3
 */
public enum ExtFixPricePaymentMethodEnum {
    /**
     * 电汇
     */
    TEL_MONEY,
    /**
     * 承兑
     */
    ACCEPT,
    /**
     * 预付款
     */
    ADVANCE_PAY;

    public static String getDictCode() {
        return "JC_PAYMENT_WAY";
    }

}
