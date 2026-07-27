package com.midea.cloud.srm.model.pj.ccapipayments.enums;

import io.swagger.annotations.ApiModel;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 付款对象类型
 */
@ApiModel("付款对象类型")
public enum ApiCustomerSupplyEnum {

    /**
     * 员工
     */
    PERSONNEL("Personnel", "员工"),
    /**
     * 客户
     */
    CUSTOMER("Customer", "客户"),
    /**
     * 供应商
     */
    SUPPLY("Supply", "供应商"),
    /**
     * 一次性供应商
     */
    ONCE_SUPPLIER("OnceSupplier", "一次性供应商");

    private String code;
    private String name;

    ApiCustomerSupplyEnum(String code, String name) {
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
