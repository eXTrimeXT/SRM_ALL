package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * @author huangbf3
 * 竞价管理-供应商.报价状态
 * 字典值: SOU_QUOTATION_STATUS
 */
public enum quotationSatausStatusEnum {

    /**
     * 未报价
     */
    NO_QUOTATION_STATUS,
    /* 已报价 */

    QUOTATION_STATUS_DONE;

    public static String getEnumDictCode() {

        return "EXT_SOU_QUOTATION_STATUS";
    }

}
