package com.midea.cloud.srm.model.supcooperate.ext.requirement.pr.requirement.enums;

/**
 * 采购需求池 - 定价状态
 * PS: 字典 EXT_PR_REQ_FIX_PRICE_STATUS
 * @author huangbf3
 */
public enum PrRequirementFixPriceStatusEnum {
    /**
     * 未定价
     */
    DRAFT,
    /**
     * 定价中
     */
    PRICE_ING,
    /**
     * 已定价
     */
    PRICE_END,
    /**
     * 定价失败
     */
    PRICE_FAIL;

}
