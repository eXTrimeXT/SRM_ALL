package com.midea.cloud.srm.model.sou.sourcing.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouTypeEnum {
    /**
     * 备注
     */
    inq,
    bid,
    brg,
    comp,
    auct,
    /**
     * 供应商推荐
     */
    recomm,
    DEFAULT;

    private SouTypeEnum() {
    }
}
