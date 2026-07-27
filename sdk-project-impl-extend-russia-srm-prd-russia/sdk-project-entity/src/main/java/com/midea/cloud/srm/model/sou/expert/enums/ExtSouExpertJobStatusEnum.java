package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 在职状态
 * PS: EXT_SOU_EXPERT_JOB_STATUS
 * @author huangbf3
 */
public enum ExtSouExpertJobStatusEnum {
    /**
     * 在职
     */
    OB_JOB,
    /**
     * 离职
     */
    FEMALE;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_JOB_STATUS";
    }

}
