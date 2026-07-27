package com.midea.cloud.srm.model.contract.enums;


/**
 * @author: 100014337
 * @describe 合同头状态
 * @date: 2023/12/8
 * @version 1.0
 **/
public enum ContractHeadPlanStatusEnums {

    /**
     * 未开始
     **/
    NEVER_START("NEVER_START","未开始"),
    /**
     * 履约中
     **/
    IN_PERFORMANCE("IN_PERFORMANCE", "履约中"),
    /**
     * 履约完成
     **/
    COMPLETE_PERFORMANCE("COMPLETE_PERFORMANCE", "履约完成");

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }



    ContractHeadPlanStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
