package com.midea.cloud.srm.model.pj.sou.priceapproval.core.enums;

/**
 * 价格审批单 - 单据状态
 * PS: 字典 SOU_PRICE_APPROVAL_STATUS
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/22
 */
public enum PriceApprovalStatusEnum {

    /**
     * 拟定
     */
    DRAFT,
    /* 审批中 */
    SUBMITTED,
    /* 已驳回 */
    REJECTED,
    /* 已撤回 */
    WITHDRAW,
    /* 已废弃 */
    ABANDONED,
    /* 已审批 */
    APPROVED;

    public static String getEnumDictCode() {
        return "SOU_PRICE_APPROVAL_STATUS";
    }

}
