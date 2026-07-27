package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.寻源单据来源的上游类别
 * 字典值: SOU_SOURCE_FROM_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouSourceFromTypeEnum {

    /**
     * 手工创建
     */
    HAND_MAKE,
    /**
     * 寻源需求
     */
    SOU_REQ,
    /**
     * 采购需求
     */
    PURCHASE_REQ;

    public static String getEnumDictCode() {
        return "SOU_SOURCE_FROM_TYPE";
    }

}
