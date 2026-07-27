package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums;

/**
 * 招标计划 - 需求来源
 * PS: PR_SOU_REQUIREMENT_FROM
 * @author huangbf3
 */
public enum PrSouRequirementFromEnum {
    /**
     * 年度
     */
    YEAR,
    /**
     * 月度
     */
    MONTH,
    /**
     * 计划外
     */
    WITHOUT_PLAN,
    /**
     * 特殊招标
     */
    SPECIAL_SOU;

    public static String getDictCode() {
        return "PR_SOU_REQUIREMENT_FROM";
    }

}
