package com.midea.cloud.srm.model.pj.ccapisettleacountings.enums;

/**
 * @Author: panmq
 * @Date: 2024/04/10/ $
 * @Description: 销售类型（01-服务销售，02-资产销售，10-其他）
 */
public enum ApiSettleAcountingSaleTypeEnum {
    /**
     * 服务销售
     */
    SERVICE("01", "服务销售"),
    /**
     * 资产销售
     */
    ASSETS("02", "资产销售"),
    /**
     * 其他
     */
    OHTER("10", "其他");

    private String code;
    private String name;

    ApiSettleAcountingSaleTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
