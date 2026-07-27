package com.midea.cloud.srm.sou.meiql.recruit.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum RecruitStatusEnum {
    /**
     * 拟定
     */
    DRAFT ("DRAFT","拟定"),
    APPROVING ("APPROVING","审批中"),
    APPROVED ("APPROVED","已审批"),
    REJECTED ("REJECTED","已驳回"),
    ABANDON ("ABANDON","已废弃"),
    WITHDRAW("WITHDRAW","已撤回");


    private String code;
    private String name;

    RecruitStatusEnum(String code, String name) {
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
