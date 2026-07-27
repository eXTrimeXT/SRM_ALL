package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.发布范围
 * 字典值: SOU_PUBLISH_SCOPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouPublishScopeEnum {

    /**
     * 公开
     */
    OPEN_TENDER,
    /**
     * 邀请
     */
    INVITE_TENDER;

    public static String getEnumDictCode() {
        return "SOU_PUBLISH_SCOPE";
    }

}
