package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 简易询价.询价附件类型
 * 字典值：SOU_FILE_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/05
 */
public enum SouFileTypeEnum {

    /**
     * 1 : 申请资料附件
     */
    INNER,
    /**
     * 2 : 保证金附件
     */
    BOND,
    /**
     * 3 : 评选附件
     */
    SELECT,
    /**
     * 4 : 归档附件
     */
    PLACEONFILE,
    /**
     * 5 : 招标资料附件
     */
    OUTER;

    public static String getEnumDictCode() {
        return "SOU_FILE_TYPE";
    }

}
