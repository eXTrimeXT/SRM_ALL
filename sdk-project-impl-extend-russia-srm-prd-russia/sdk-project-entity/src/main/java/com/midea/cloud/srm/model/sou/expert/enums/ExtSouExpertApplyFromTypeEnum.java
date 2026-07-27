package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家申请数据来源
 * PS: EXT_SOU_EXPERT_APPLY_FROM_TYPE
 * @author huangbf3
 */
public enum ExtSouExpertApplyFromTypeEnum {
    /**
     * 自主申请
     */
    INDEPENDENT,
    /**
     * 绿色通道
     */
    GREEN_CHANNEL,
    /**
     * 升级申请
     */
    UPGRADE,
    /**
     * 变更申请(变更申请和升级申请两者是不能共存的，变更可以很随意的修改申请等级，而升降级是有严格限制的)
     */
    CHANGE;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_APPLY_FROM_TYPE";
    }

}
