package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums;

/**
 * 招标计划 - 工作成员职责
 * PS: PR_SOU_REQUIREMENT_GROUP_TYPE
 * @author huangbf3
 */
public enum PrSouRequirementGroupTypeEnum {
    /**
     * 寻源负责人(招标负责人)
     */
    SOU,
    /**
     * 技术负责人
     */
    TECH,
    /**
     * 供应商负责人
     */
    VENDOR,
    /**
     * 商务负责人
     */
    BUSINESS;

    public static String getDictCode() {
        return "PR_SOU_REQUIREMENT_GROUP_TYPE";
    }

}
