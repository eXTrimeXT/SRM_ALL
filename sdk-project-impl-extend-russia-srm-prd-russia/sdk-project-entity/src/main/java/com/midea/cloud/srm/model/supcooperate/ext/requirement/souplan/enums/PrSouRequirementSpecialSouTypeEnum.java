package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums;

/**
 * 招标计划 - 特殊招标类型
 * PS: PR_SOU_REQUIREMENT_SPECIAL_TYPE
 * @author huangbf3
 */
public enum PrSouRequirementSpecialSouTypeEnum {
    /**
     * 特定原因使得供应商唯一
     */
    SPECIAL_VENDOR_ONE,
    /**
     * 时间紧急
     */
    TIME_URGENT,
    /**
     * 其他
     */
    OTHER;

    public static String getDictCode() {
        return "PR_SOU_REQUIREMENT_SPECIAL_TYPE";
    }

}
