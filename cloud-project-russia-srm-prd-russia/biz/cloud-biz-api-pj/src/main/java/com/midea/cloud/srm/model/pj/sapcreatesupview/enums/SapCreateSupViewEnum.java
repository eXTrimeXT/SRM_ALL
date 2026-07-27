package com.midea.cloud.srm.model.pj.sapcreatesupview.enums;

import com.alibaba.excel.util.StringUtils;

/**
 * 供应商编码同步SAP枚举
 *
 * @author fubiao
 * @since 2024/03/21
 */
public enum SapCreateSupViewEnum {


    /**
     * 工厂编码
     */
    FACTORY_1000("FACTORY_1000", "1000"),
    FACTORY_N100("FACTORY_N100", "N100"),
    FACTORY_J100("FACTORY_J100", "J100"),
    FACTORY_B400("FACTORY_B400", "B400");
    private String code;
    private String name;

    private SapCreateSupViewEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
