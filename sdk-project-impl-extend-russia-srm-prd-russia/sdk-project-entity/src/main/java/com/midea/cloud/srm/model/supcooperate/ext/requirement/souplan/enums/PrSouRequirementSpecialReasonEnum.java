package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums;

/**
 * 招标计划 - 特殊原因
 * PS: PR_SOU_REQUIREMENT_SPECIAL_REASON
 * @author huangbf3
 */
public enum PrSouRequirementSpecialReasonEnum {
    /**
     * 垄断
     */
    MONOPOLY,
    /**
     * 政府定制
     */
    GOVERNMENT,
    /**
     * 原厂合作
     */
    FACTORY_COOP,
    /**
     * 其他
     */
    OTHER;

    public static String getDictCode() {
        return "PR_SOU_REQUIREMENT_SPECIAL_REASON";
    }

}
