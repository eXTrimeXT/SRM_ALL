package com.midea.cloud.srm.emun;

/**
 * 供应商异常类型枚举
 * @author luxc18
 */
public enum PjSupplierExceptionTypeEmun {
    /**
     * 黑名单
     */
    BLACK,
    /**
     * 组织受限
     */
    POSITION_LIMIT_FLAG,
    /**
     * 重点监督
     */
    KEY_SUPERVISION_FLAG,
    /**
     * 时间受限
     */
    TIME_LIMIT_FLAG,
    /**
     * 品类受限
     */
    CATEGORY_LIMIT_FLAG,
    /**
     * 重点关注
     */
    FOCUS_FLAG
}
