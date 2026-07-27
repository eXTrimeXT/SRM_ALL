package com.midea.cloud.srm.model.sou.enums;

/**
 * @Author: panmq
 * @Date: 2024/04/16/ $
 * @Description: 意向金开票：服务编码FW0907，业务编码FW0907-01
 * 意向金冲销：服务编码FW9901，业务编码FW9901-01
 * 保证金扣款：服务编码FW9902，业务编码FW9902-01
 */
public enum CaApiAcountServiceEnum {
    /**
     * 意向金开票
     */
    EARNEST_INVOICE("FW0907", "FW0907-01", "意向金开票"),
    /**
     * 意向金冲销
     */
    EARNEST_AGINST("FW9901", "FW9901-01", "意向金冲销"),
    /**
     * 保证金扣款
     */
    MARGIN_DEDUCTION("FW9902", "FW9902-01", "保证金扣款"),
    ;

    private String serviceCode;

    private String businessCode;

    private String desc;

    CaApiAcountServiceEnum(String serviceCode, String businessCode, String desc) {
        this.serviceCode = serviceCode;
        this.businessCode = businessCode;
        this.desc = desc;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public String getDesc() {
        return desc;
    }
}
