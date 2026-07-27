package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums;

/**
 * 招标计划 - 文件类型
 * PS: PR_SOU_REQUIREMENT_FILE_TYPE
 * @author huangbf3
 */
public enum PrSouRequirementFileTypeEnum {
    /**
     * 其他
     */
    OTHER,
    /**
     * 限制供应商
     */
    RESTRICT_VENDOR,
    /**
     * 限制品类
     */
    RESTRICT_BRAND;

    public static String getDictCode() {
        return "PR_SOU_REQUIREMENT_FILE_TYPE";
    }

}
