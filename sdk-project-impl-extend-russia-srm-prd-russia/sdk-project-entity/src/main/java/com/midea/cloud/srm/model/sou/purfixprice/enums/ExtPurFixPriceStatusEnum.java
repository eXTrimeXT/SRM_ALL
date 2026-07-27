package com.midea.cloud.srm.model.sou.purfixprice.enums;

/**
 * 集采定价 - 单据状态
 * @author huangbf3
 */
public enum ExtPurFixPriceStatusEnum {
    /**
     * 拟定
     */
    DRAFT,
    /**
     * 审批中
     */
    SUBMITTED,
    /**
     * 已驳回
     */
    REJECTED,
    /**
     * 已撤回
     */
    WITHDRAW,
    /**
     * 已废弃
     */
    ABANDONED,
    /**
     * 已审批
     */
    APPROVED;

    public static String getEnumDictCode() {
        return "EXT_PURFIX_PRICE_STATUS";
    }

}
