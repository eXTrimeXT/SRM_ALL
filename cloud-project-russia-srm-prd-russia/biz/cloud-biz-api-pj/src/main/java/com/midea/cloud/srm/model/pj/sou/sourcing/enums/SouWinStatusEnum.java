package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源 - 本轮入围情况
 * 字典值: SOU_WIN_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouWinStatusEnum {

    /**
     * 入围
     */
    Y,
    /**
     * 淘汰
     */
    N,
    /**
     * 待定
     */
    D;

    public static String getEnumDictCode() {
        return "SOU_WIN_STATUS";
    }

}
