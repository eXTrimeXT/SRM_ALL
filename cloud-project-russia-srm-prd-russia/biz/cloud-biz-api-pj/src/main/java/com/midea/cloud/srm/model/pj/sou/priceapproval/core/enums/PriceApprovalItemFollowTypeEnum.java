package com.midea.cloud.srm.model.pj.sou.priceapproval.core.enums;

/**
 * 价格审批单 - 中标行后续单据类型
 * PS: 字典 SOU_PRICE_APPROVAL_ITEM_FOLLOW_TYPE
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/01
 */
public enum PriceApprovalItemFollowTypeEnum {

    /**
     * 合同
     */
    contract;

    public static String getEnumDictCode() {
        return "SOU_PRICE_APPROVAL_ITEM_FOLLOW_TYPE";
    }

}
