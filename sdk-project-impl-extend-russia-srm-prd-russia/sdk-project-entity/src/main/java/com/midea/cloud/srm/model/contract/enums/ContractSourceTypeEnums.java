package com.midea.cloud.srm.model.contract.enums;

/**
 * @author: 100014337
 * @describe 合同来源通知书
 * @date: 2023/12/8
 * @version 1.0
 **/
public enum ContractSourceTypeEnums {
    /**
     * 手动创建
     **/

    MANUALLY_CREATED("MANUALLY_CREATED","手动创建"),
    /**
     * 中标通知书
     **/
    BID_NOTICE("MANUALLY_CREATED","中标通知书"),
    /**
     * 临时采集
     **/
    TEMP_PROCURE("TEMP_PROCURE","临采定价单"),
    /**
     * 集中采集
     **/
    CENT_PURCHASE("CENT_PURCHASE","集采定价单");

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    ContractSourceTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
