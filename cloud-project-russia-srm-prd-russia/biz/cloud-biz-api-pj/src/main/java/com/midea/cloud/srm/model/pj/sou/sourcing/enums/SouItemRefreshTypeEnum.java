package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源核心 - 物料需求刷新类型
 * 字典值：SOU_ITEM_REFRESH_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/11/14
 */
public enum SouItemRefreshTypeEnum {

    /**
     * 新增
     */
    NEW,
    /**
     * 现有
     */
    EXIST,
    /**
     * 删除
     */
    DELETE;

    public static String getDictCode() {
        return "SOU_ITEM_REFRESH_TYPE";
    }

}
