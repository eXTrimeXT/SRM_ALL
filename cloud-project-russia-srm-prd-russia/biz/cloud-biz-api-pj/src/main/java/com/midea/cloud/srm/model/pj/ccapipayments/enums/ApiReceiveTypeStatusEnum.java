package com.midea.cloud.srm.model.pj.ccapipayments.enums;

import io.swagger.annotations.ApiModel;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 收款方类型
 */
@ApiModel("收款方类型")
public enum ApiReceiveTypeStatusEnum {
    /**
     * 客户-单位
     */
    CUSTOMER_UNIT("CustomerUnit", "客户-单位"),
    /**
     * 客户-个人
     */
    CUSTOMER_INDIVIDUAL("CustomerIndividual", "客户-个人"),
    /**
     * 供应商个人
     */
    SUPPLIER_INDIVIDUAL("SupplierIndividual", "供应商个人"),
    /**
     * 员工
     */
    PERSONNEL("Personnel", "员工"),
    /**
     * 个人
     */
    INDIVIDUAL("Individual", "个人"),
    /**
     * 一次性供应商-个人
     */
    ONCE_SUPPLIER_INDIVIDUAL("OnceSupplierIndividual", "一次性供应商-个人"),
    /**
     * 供应商-单位
     */
    SUPPLY_UNIT("SupplyUnit", "供应商-单位"),
    /**
     * 一次性供应商-单位
     */
    ONCE_SUPPLIER_UNIT("OnceSupplierUnit", "一次性供应商-单位");
    private String code;
    private String name;

    ApiReceiveTypeStatusEnum(String code, String name) {
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
