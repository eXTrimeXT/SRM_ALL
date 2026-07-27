package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家等级
 * PS: EXT_SOU_EXPERT_LEVEL
 * @author huangbf3
 */
public enum ExtSouExpertLevelEnum {
    /**
     * 普通
     */
    NORMAL,
    /**
     * 高级
     */
    SENIOR;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_LEVEL";
    }

}
