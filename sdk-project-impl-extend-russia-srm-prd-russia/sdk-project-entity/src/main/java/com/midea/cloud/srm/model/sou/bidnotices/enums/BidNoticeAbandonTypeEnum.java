package com.midea.cloud.srm.model.sou.bidnotices.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum BidNoticeAbandonTypeEnum {
    /**
     * 变更合同签署单位
     */
    CHANGE_CONTRACT ("CHANGE_CONTRACT","变更合同签署单位"),
    CHANGE_NOTICE ("CHANGE_NOTICE","变更中标通知书"),
    CHANGE_VENDOR ("CHANGE_VENDOR","变更中标供应商，重新定标"),
    OTHERS ("OTHERS","其他");


    private String code;
    private String name;

    BidNoticeAbandonTypeEnum(String code, String name) {
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
