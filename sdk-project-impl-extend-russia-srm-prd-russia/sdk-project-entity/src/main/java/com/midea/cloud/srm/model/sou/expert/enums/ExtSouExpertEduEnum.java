package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家学历等级
 * PS: EXT_SOU_EXPERT_EDUCATION
 * @author huangbf3
 */
public enum ExtSouExpertEduEnum {
    /**
     * 博士
     */
    DOCTOR,
    /**
     * 硕士
     */
    MASTER,
    /**
     * 本科
     */
    UNDERGRADUATE,
    /**
     * 专科
     */
    SPECIALTY,
    /**
     * 高中
     */
    HIGH_SCHOOL;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_EDUCATION";
    }

}
