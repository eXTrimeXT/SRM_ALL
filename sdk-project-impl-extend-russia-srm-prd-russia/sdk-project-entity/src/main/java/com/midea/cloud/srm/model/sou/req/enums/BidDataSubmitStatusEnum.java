package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 招标资料递交头表单据状态枚举类
 *
 * @author fengdc3@midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-05-15 9:15:20
 *  修改内容:
 * </pre>
 */
public enum BidDataSubmitStatusEnum {
    /**
     * 拟定
     */
    DRAFT,
    /**
     * 审批中
     */
    APPROVING,
    /**
     * 已审批
     */
    APPROVED,
    /**
     * 已驳回
     */
    REJECTED,
    /**
     * 已撤回
     */
    WITHDRAW,
    //已废弃
    ABANDON;

}
