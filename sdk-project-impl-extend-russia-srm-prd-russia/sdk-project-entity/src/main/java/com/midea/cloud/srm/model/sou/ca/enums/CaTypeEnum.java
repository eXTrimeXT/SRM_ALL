package com.midea.cloud.srm.model.sou.ca.enums;
/**
 * 备注
 * @author huangbf3
 */
public enum CaTypeEnum {
    /**
     * 拟定
     */
    APPLY ("APPLY","定标申请"),
    DESTORY ("DESTORY","废标申请");

    private String code;
    private String name;

    CaTypeEnum(String code, String name) {
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
