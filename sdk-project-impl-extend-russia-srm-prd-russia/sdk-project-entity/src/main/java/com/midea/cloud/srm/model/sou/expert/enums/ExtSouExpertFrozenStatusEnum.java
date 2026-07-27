package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家冻结状态
 * PS: EXT_SOU_EXPERT_FROZEN_STATUS
 * @author huangbf3
 */
public enum ExtSouExpertFrozenStatusEnum {
    /**
     * 冻结未确认
     */
    FROZEN_UN_CONFIRM,
    /**
     * 已冻结
     */
    FROZEN,
    /**
     * 解冻未确认
     */
    UNFROZEN_UN_CONFIRM,
    /**
     * 已确认解冻
     */
    UNFROZEN;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_FROZEN_STATUS";
    }

}
