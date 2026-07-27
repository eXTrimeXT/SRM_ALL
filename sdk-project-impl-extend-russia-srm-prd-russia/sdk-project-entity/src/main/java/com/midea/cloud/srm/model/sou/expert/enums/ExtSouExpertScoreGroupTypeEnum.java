package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家评审 - 寻源工作成员职责
 * PS: EXT_SOU_EXPERT_SCORE_GROUP_TYPE
 * @author huangbf3
 */
public enum ExtSouExpertScoreGroupTypeEnum {
    /**
     * 寻源组长
     */
    SOU_LEADER,
    /**
     * 寻源负责人
     */
    SOU_MANAGER;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_SCORE_GROUP_TYPE";
    }

}
