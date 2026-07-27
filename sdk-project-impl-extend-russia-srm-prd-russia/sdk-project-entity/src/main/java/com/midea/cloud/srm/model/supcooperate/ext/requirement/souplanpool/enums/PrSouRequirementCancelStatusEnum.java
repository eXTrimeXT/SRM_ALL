package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums;

/**
 * 招标计划 - 计划取消单据 - 状态
 * PS: PR_SOU_REQUIREMENT_CANCEL_STATUS
 * @author huangbf3
 */
public enum PrSouRequirementCancelStatusEnum {
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
        return "PR_SOU_REQUIREMENT_CANCEL_STATUS";
    }

}
