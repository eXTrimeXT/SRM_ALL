package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 项目式询价.供方必须上传报价 - 文件类型
 * 字典值: SOU_FILE_CONFIG_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/19
 */
public enum SouFileConfigTypeEnum {

    /**
     * 技术标
     */
    TECH_FILE,
    /**
     * 商务标
     */
    BUSINESS_FILE;

    public static String getEnumDictCode() {
        return "SOU_FILE_CONFIG_TYPE";
    }

}
