package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.流程配置状态
 * 字典值: SOU_PROCESS_CONFIG_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouProcessConfigStatusEnum {

    /**
     * 1 :拟定
     */
    DRAFT,
    /**
     * 2 :生效
     */
    VALID,
    /**
     * 3 :失效
     */
    INVALID;

    public static String getEnumDictCode() {
        return "SOU_PROCESS_CONFIG_STATUS";
    }

}
