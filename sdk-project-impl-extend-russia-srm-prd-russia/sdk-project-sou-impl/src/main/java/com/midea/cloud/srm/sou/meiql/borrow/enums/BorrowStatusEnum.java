package com.midea.cloud.srm.sou.meiql.borrow.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum BorrowStatusEnum {
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

    BorrowStatusEnum(String code, String name) {
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
