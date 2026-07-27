package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源核心 - 寻源生成价格审批单方式
 * 字典值: SOU_GENERATE_PRICE_APPROVAL_TYPE
 *
 * @author zhangwk12@midea.com
 * @since 2023/09/05
 */
public enum SouGeneratePriceApprovalTypeEnum {

    /**
     * 根据整单
     */
    BY_TOTAL,
    /**
     * 根据报价明细
     */
    BY_ROWS;

    public static String getEnumDictCode() {
        return "SOU_GENERATE_PRICE_APPROVAL_TYPE";
    }

}
