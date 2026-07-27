package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源 - 评选结果
 * 字典值: SOU_SELECT_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouSelectStatusEnum {

    /**
     * 待定
     */
    DRAFT,
    /**
     * 中标
     */
    WIN,
    /**
     * 落标
     */
    FAIL;

    public static String getEnumDictCode() {
        return "SOU_SELECT_STATUS";
    }

}
