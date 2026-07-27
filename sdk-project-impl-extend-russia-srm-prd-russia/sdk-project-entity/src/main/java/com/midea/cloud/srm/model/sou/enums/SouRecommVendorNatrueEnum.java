package com.midea.cloud.srm.model.sou.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum SouRecommVendorNatrueEnum {

    /**
     * ①申请部门推荐单位
     */
    DEPART_RECOMM("DEPART_RECOMM", "①申请部门推荐单位"),
    /**
     * ②该标的物前期中标单位
     */
    BEFORE_UNITE("BEFORE_UNITE", "②该标的物前期中标单位"),
    /**
     * ③其他标的物前期中标单位
     */
    OTHER_UNITE("OTHER_UNITE", "③其他标的物前期中标单位"),
    /**
     * ④主动报名单位
     */
    ACTIVE_SIGNUP("ACTIVE_SIGNUP", "④主动报名单位"),
    /**
     * ⑤新开发单位
     */
    NEW_UNITE("NEW_UNITE", "⑤新开发单位"),
    /**
     * ⑥参与过投标但未曾中标的单位
     */
    PART_IN_NOT_WIN("PART_IN_NOT_WIN", "⑥参与过投标但未曾中标的单位")
    ;

    private String code;
    private String name;

    SouRecommVendorNatrueEnum(String code, String name) {
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
