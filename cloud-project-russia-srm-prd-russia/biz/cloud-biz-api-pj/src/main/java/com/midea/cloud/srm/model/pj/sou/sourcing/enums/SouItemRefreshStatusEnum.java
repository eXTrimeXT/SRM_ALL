package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源核心 - 物料需求刷新状态
 * PS: 字典值 SOU_ITEM_REFRESH_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/11/14
 */
public enum SouItemRefreshStatusEnum {

    /**
     * 未刷新
     */
    DRAFT,
    /**
     * 作废(如果是多次刷新，被覆盖的就作废)
     */
    CANCEL,
    /**
     * 已刷新
     */
    DONE,
    /**
     * 刷新失败(刷新中途出现意外错误)
     */
    FAIL;

    public static String getEnumDictCode() {
        return "SOU_ITEM_REFRESH_STATUS";
    }

}
