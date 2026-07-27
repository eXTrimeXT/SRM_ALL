package com.midea.cloud.srm.model.sou.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouRecommvendorStatusEnum {
    /**
     * 拟定
     */
    DRAFT("DRAFT", "拟定"),
    APPROVING("APPROVING", "审批中"),
    APPROVED("APPROVED", "已审批"),
    REJECT("REJECT", "已驳回"),
    WITHDRAW("WITHDRAW", "已撤回"),
    ABANDON("ABANDON", "已废弃")
    ;

    private String code;
    private String name;

    SouRecommvendorStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
