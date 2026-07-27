package com.midea.cloud.srm.sou.meiql.inspect.enums;
/**
 * 备注
 * @author huangbf3
 */
public enum InspectStatusEnum {
    /**
     * 拟定
     */
    DRAFT ("DRAFT","拟定"),
    APPLY_APPROVING ("APPLY_APPROVING","申请审批中"),
    APPLY_WITHDRAW ("APPLY_WITHDRAW","申请已撤回"),
    APPLY_REJECTED ("APPLY_REJECTED","申请已驳回"),
    APPLY_APPROVED ("APPLY_APPROVED","待提交报告"),
    REPORT_APPROVING ("REPORT_APPROVING","报告审批中"),
    REPORT_WITHDRAW ("REPORT_WITHDRAW","报告已撤回"),
    REPORT_REJECTED ("REPORT_REJECTED","报告已驳回"),
    REPORT_APPROVED ("REPORT_APPROVED","报告已审批"),
    ABANDON ("ABANDON","已废弃");


    private String code;
    private String name;

    InspectStatusEnum(String code, String name) {
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
