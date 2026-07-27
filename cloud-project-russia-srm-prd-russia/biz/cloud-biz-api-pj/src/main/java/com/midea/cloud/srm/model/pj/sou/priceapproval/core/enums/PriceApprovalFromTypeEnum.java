package com.midea.cloud.srm.model.pj.sou.priceapproval.core.enums;

/**
 * 价格审批单 - 中标行后续单据状态
 * PS: 字典 PRICE_APPROVAL_FROM_TYPE
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/01
 */
public enum PriceApprovalFromTypeEnum {

    /**
     * 简易询价
     */
    inq,
    /* 招投标 */
    bid,
    /* 项目式询价 */
    brg,
    /* 竞价 */
    comp,
    /* 新版竞价(MQL)  */
    auct;

    public static String getEnumDictCode() {
        return "PRICE_APPROVAL_FROM_TYPE";
    }

}
