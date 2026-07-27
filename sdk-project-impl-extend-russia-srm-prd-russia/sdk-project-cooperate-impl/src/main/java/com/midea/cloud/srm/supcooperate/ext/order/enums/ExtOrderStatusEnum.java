package com.midea.cloud.srm.supcooperate.ext.order.enums;

/**
 * 备注
 * @author zenghx2
 */
public enum ExtOrderStatusEnum {
    /**
     * 待提交（采购商撤回）
     */
    WAIT_SUBMIT,
    /**
     * 未开始（前端不展示，orderStatus）
     */
    NOT_STARTED,
    /**
     * 执行中(在途)
     */
    ONGOING,
    /**
     * 已完成
     */
    FINISHED

}
