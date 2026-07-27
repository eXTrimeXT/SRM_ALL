package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家评审状态
 * PS: EXT_SOU_EXPERT_SCORE_STATUS
 * @author huangbf3
 */
public enum ExtSouExpertScoreStatusEnum {
    /**
     * 未评价
     */
    DRAFT,
    /**
     * 寻源组长已评价
     */
    SOU_LEADER,
    /**
     * 寻源负责人已评价
     */
    SOU_MANAGER,
    /**
     * 已生成评价结果
     */
    FINISH;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_SCORE_STATUS";
    }

}
