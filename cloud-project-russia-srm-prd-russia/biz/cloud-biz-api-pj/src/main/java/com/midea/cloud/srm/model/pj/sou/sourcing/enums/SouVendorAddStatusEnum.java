package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源核心 - 追加供应商记录执行状态
 * PS: 字典值 SOU_VENDOR_ADD_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/11/24
 */
public enum SouVendorAddStatusEnum {

    /**
     * 未追加
     */
    DRAFT,
    /**
     * 已追加
     */
    DONE,
    /**
     * 追加失败
     */
    FAIL,
    /**
     * 已废弃
     */
    CANCEL;

    public static String getEnumDictCode() {
        return "SOU_VENDOR_ADD_STATUS";
    }

}
