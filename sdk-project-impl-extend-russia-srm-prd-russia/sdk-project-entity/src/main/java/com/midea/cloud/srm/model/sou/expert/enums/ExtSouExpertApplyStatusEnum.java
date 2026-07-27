package com.midea.cloud.srm.model.sou.expert.enums;

/**
 * 专家库 - 专家申请状态
 * PS: EXT_SOU_EXPERT_APPLY_STATUS
 * @author huangbf3
 */
public enum ExtSouExpertApplyStatusEnum {
    /**
     * 拟定
     */
    DRAFT,
    /**
     * 已提交
     */
    SUBMITTED,
    /**
     * 审批中
     */
    APPROVING,
    /**
     * 已驳回
     */
    REJECTED,
    /**
     * 已审批
     */
    APPROVED,
    /**
     * 已废弃
     */
    ABANDONED,
    /**
     * 已撤回
     */
    WITHDRAW;

    public static String getDictCode() {
        return "EXT_SOU_EXPERT_APPLY_STATUS";
    }

}
