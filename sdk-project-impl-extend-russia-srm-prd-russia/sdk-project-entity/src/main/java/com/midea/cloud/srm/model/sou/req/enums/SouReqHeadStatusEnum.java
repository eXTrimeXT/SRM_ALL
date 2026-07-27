package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 寻源需求单状态
 * @author huangbf3
 */
public enum SouReqHeadStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),
    APPROVING("APPROVING", "审批中"),
    APPROVED("APPROVED", "接受报名中"),
    REJECTED("REJECTED", "已驳回"),
    WITHDRAW("WITHDRAW", "已撤回"),
    SIGNUP_DONE("SIGNUP_DONE", "报名截止"),
    CLOSED("CLOSED", "关闭"),
    ABANDON("ABANDON", "已废弃")
    ;
    private String code;
    private String name;

    SouReqHeadStatusEnum(String code, String name) {
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
