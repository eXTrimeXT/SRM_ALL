package com.midea.cloud.srm.model.pj.ccapipayments.enums;

import io.swagger.annotations.ApiModel;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 付款方式
 */
@ApiModel("付款方式")
public enum ApiPaymentMethodEnum {

    /**
     * 电汇
     */
    TELEGRAPHIC("Telegraphic", "电汇"),
    /**
     * 商业承兑
     */
    COMMERCIAL("Commercial", "商业承兑"),
    /**
     * 银行承兑
     */
    BANK("Bank", "银行承兑"),
    /**
     * 信用证
     */
    CREDIT("Credit", "信用证"),
    /**
     * 被动扣款
     */
    PASSIVE_DEDUCTION("PassiveDeduction", "被动扣款"),
    /**
     * 信用证付款
     */
    CREDIT_PAYMENT("CreditPayment", "信用证付款"),
    /**
     * 开保函付款
     */
    GUARANTEE_PAYMENT("GuaranteePayment", "开保函付款"),
    /**
     * 电子债权凭证
     */
    ELECTRONIC_PAYMENT_VOUCHER("ElectronicPaymentVoucher", "电子债权凭证");
    private String code;
    private String name;

    ApiPaymentMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
