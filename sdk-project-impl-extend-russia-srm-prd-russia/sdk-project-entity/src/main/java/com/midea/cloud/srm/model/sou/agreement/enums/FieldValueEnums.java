package com.midea.cloud.srm.model.sou.agreement.enums;
/**
 * 备注
 * @author huangbf3
 */
public enum FieldValueEnums {
    /**
     * 协议编码
     */
    AGREEMENT_CODE ("agreementCode","协议编码"),
    AGREEMENT_NAME ("agreementName","协议名称"),
    PAYMENT ("payment", "付款条款"),
    TRADING ("trading", "交易方式"),
    BUY_PERSON_NAME ("buyPersonName", "采购员"),
    INVOICE_TYPE ("invoiceType", "发票类型"),
    PAY_WAY ("payWay", "付款方式"),
    AGREEMENT_FILE_ID ("agreementFileId", "协议附件"),
    AGREEMENT_TYPE ("agreementType", "协议类型"),
    REMARK ("remark", "备注"),
    PRICE_TAX ("priceTax", "未税单价"),
    TAX_RATE ("taxRate", "税率"),
    REFERENCE_PRICE ("referencePrice", "参考价"),
    LEAD_TIME ("leadTime", "交货周期"),
    SELL_BY_DATE ("sellByDate", "质保期"),
    START_NUM ("startNum", "起订量"),
    MULTIPLE_START_NUM ("multipleStartNum", "整倍起售数量"),
    AGREEMENT_DES ("agreementDes", "协议行说明"),
    IS_TIERED_PRICING ("isTieredPricing", "是否阶梯价"),
    MORE_NUM ("moreNum", "数量从"),
    LESS_NUM ("lessNum", "数量到"),
    UNIT ("unit", "单位"),
    PRICE_TAX1 ("priceTax", "未税单价"),
    RATE_PRICE ("ratePrice", "含税单价"),
    REFER_PRICE ("referPrice", "参考价");

    private final String code;
    private final String name;

    FieldValueEnums(String code, String name) {
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
