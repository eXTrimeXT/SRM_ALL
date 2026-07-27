package com.midea.cloud.srm.model.pj.sou.car.point.sign.enums;

/**
 * <pre>
 *  寻源-定点会签状态
 *  字典：
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/10 16:13
 *  修改内容:
 * </pre>
 */
public enum PointSignStatusEnum {
    /**
     * 拟定
     */
    DRAFT,
    /* 已提交*/
    SUBMITTED,
    /*已审批 */
    APPROVED,
    /*已驳回 */
    REJECTED,
    /*已作废 */
    CANCEL,
}
