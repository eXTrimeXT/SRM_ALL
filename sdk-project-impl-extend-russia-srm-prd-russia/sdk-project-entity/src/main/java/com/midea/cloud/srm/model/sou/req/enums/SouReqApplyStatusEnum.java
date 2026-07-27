package com.midea.cloud.srm.model.sou.req.enums;

/**
 * 寻源需求单报名状态
 * @author huangbf3
 */
public enum SouReqApplyStatusEnum {
    /**
     * 未报名
     */
    NO_SIGNUP("NO_SIGNUP", "未报名"),
    SUCCESS_SIGNUP("SUCCESS_SIGNUP", "报名成功"),
    CONFIRMING_SIGNUP("CONFIRMING_SIGNUP", "报名确认中"),
    FAIL_SIGNUP("FAIL_SIGNUP", "报名失败"),
    WITHDRAW("WITHDRAW", "报名已撤回"),
    ;
    private String code;
    private String name;

    SouReqApplyStatusEnum(String code, String name) {
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
