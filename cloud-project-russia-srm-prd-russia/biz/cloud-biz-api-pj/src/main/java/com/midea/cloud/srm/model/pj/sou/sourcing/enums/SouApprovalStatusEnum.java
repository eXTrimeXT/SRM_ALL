package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源 - 审批状态
 * 字典值: SOU_APPROVAL_STATUS
 *
 * @author zhangwk12@midea.com
 * @since 2022/02/25
 */
public enum SouApprovalStatusEnum {

    /**
     * 拟定
     */
    DRAFT,
    /** 已审批 */
    APPROVED,


    /** 2 : 已提交 */
    SUBMITTED,
    /** 3 : 已驳回 */
    REJECTED,
    /** 4 : 已撤回 */
    WITHDRAW,
    /** 5 : 已废弃 */
    ABANDONED;


    public static String getEnumDictCode() {
        return "SOU_APPROVAL_STATUS";
    }

}
