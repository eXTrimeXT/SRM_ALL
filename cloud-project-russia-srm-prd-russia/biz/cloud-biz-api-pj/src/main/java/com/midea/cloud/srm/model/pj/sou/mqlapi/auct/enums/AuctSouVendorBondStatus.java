package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums;

/**
 * 竞价 MQL - 保证金缴纳状态
 * 字典值: SOU_AUCT_VENDOR_BOND_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2023/07/10
 */
public enum AuctSouVendorBondStatus {

    /**
     * 拟定
     */
    DRAFT,
    /**
     * 已提交
     */
    SUBMIT,
    /**
     * 已确认
     */
    PASS,
    /**
     * 已驳回
     */
    REJECT;

    public static String getDictCode() {
        return "SOU_AUCT_VENDOR_BOND_STATUS";
    }

}
