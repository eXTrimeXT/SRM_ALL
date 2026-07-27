package com.midea.cloud.srm.model.pj.sou.car.point.notice.enums;

/**
 * <pre>
 *  定点通知审批状态 字典：SOU_POINT_NOTICE_AUDIT_STATUS
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/13 11:18
 *  修改内容:
 * </pre>
 */
public enum PointNoticeAuditStatusEnum {
    /**
     * 未审批
     */
    NOT_APPROVED,
    /*审批中*/
    APPROVING,
    /*已审批*/
    APPROVED,
    /*已驳回 */
    REJECTED,
    /*已作废 */
    CANCEL
}
