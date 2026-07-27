package com.midea.cloud.srm.emun;

/**
 * <pre>
 *  功能名称描述: 二开-供应商控制单据控制类型 参考字典码:SUPPLIER_CONTROL_TYPE2
 * </pre>
 *
 * @author luxc18@meicloud.com
 * @version 1.00.00
 * <p>
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-10-12 14:08
 *  修改内容:
 * </pre>
 */
public enum PjSupplierControlType {
    /**
     * 组织受限*
     */
    POSITION_LIMIT_FLAG,
    /**
     * 组织受限解除*
     */
    POSITION_LIMIT_FLAG_REMOVE,
    /**
     * 时间受限*
     */
    TIME_LIMIT_FLAG,
    /**
     * 时间限制解除*
     */
    TIME_LIMIT_FLAG_REMOVE,
    /**
     * 品类受限*
     */
    CATEGORY_LIMIT_FLAG,
    /**
     * 品类受限解除*
     */
    CATEGORY_LIMIT_FLAG_REMOVE

}
