package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.报名状态
 * 字典值: SOU_SIGN_UP_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouSignUpStatusEnum {

    /**
     * 未报名
     */
    NO_SIGN_UP,
    /**
     * 确认中
     */
    CONFIRM_ING,
    /**
     * 已报名
     */
    SIGN_UP_DONE,
    /**
     * 已驳回
     */
    REJECTED;

    public static String getEnumDictCode() {
        return "SOU_SIGN_UP_STATUS";
    }

}
