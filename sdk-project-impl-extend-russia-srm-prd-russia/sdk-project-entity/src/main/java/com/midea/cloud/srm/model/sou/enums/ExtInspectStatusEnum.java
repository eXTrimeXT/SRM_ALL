package com.midea.cloud.srm.model.sou.enums;

/**
 * @author 100014337
 */

public enum ExtInspectStatusEnum {

    /**
     * 拟定
     */
    DRAFT ("DRAFT","拟定"),
    /**
     * 申请审批中
     */
    APPLY_APPROVING ("APPLY_APPROVING","申请审批中"),
    /**
     * 申请已撤回
     */
    APPLY_WITHDRAW ("APPLY_WITHDRAW","申请已撤回"),
    /**
     * 申请已驳回
     */
    APPLY_REJECTED ("APPLY_REJECTED","申请已驳回"),
    /**
     * 待提交报告
     */
    APPLY_APPROVED ("APPLY_APPROVED","待提交报告"),
    /**
     * 报告审批中
     */
    REPORT_APPROVING ("REPORT_APPROVING","报告审批中"),
    /**
     * 报告已撤回
     */
    REPORT_WITHDRAW ("REPORT_WITHDRAW","报告已撤回"),
    /**
     * 报告已驳回
     */
    REPORT_REJECTED ("REPORT_REJECTED","报告已驳回"),
    /**
     * 报告已审批
     */
    REPORT_APPROVED ("REPORT_APPROVED","报告已审批"),
    /**
     * 已废弃
     */
    ABANDON ("ABANDON","已废弃");


    private String code;
    private String name;

    ExtInspectStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
