package com.midea.cloud.srm.model.sou.enums;

/**
 * MEIQL对象
 * @author huangbf3
 */
public enum TypeEnum {
    /**
     * 备注
     */
    Ca("Ca","定标申请"),
    CaSelectionResult("CaSelectionResult","定标申请"),

    CaSupplier("CaSupplier","供应商总体情况"),

    Dca("Dca","定标废弃申请"),

    BidNotice("BidNotice","中/落标通知"),

    BidNoticeDetail("BidNoticeDetail","中/落标通知明细"),

    BidNoticeInternal("BidNoticeInternal","内部通知明细"),
    BidNoticeAbandon("BidNoticeAbandon","中/落标废弃通知"),

    Replay("Replay","质疑回复"),

    ReplayFile("ReplayFile","质疑回复附件"),

    CaTenderTime("CaTenderTime","定标申请投标时间"),

    CaNegotiate("CaNegotiate","定标申请供应商谈判"),

    AnswerVendor("AnswerVendor","澄清供应商"),

    CaPrice("CaPrice","定标申请历史价格"),

    CaHistoryPrice("CaHistoryPrice","手动添加定标申请历史价格");


    private String code;
    private String name;

    TypeEnum(String code, String name) {
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
