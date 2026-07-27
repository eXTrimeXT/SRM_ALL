package com.midea.cloud.srm.model.pj.sou.technicalexchange.enums;

/**
 * <pre>
 * 功能名称
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/4/28 14:01
 * 修改内容:
 * </pre>
 */
public enum SouVendorFbStatus {


    /**
     * 供应商单据反馈状态：
     */
    NO_FEEDBACK("未反馈", "NO_FEEDBACK"),
    FEEDBACK_ALREADY("已反馈", "FEEDBACK_ALREADY"),
    WITHDRAWN("已撤回", "WITHDRAWN");

    private final String name;
    private final String value;

    SouVendorFbStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    /**
     * 通过指定value值获取枚举
     *
     * @param value
     * @return
     */
    public static SouVendorFbStatus get(String value) {
        for (SouVendorFbStatus o : SouVendorFbStatus.values()) {
            if (o.value.equals(value)) {
                return o;
            }
        }
        return null;
    }

    /**
     * 枚举值列表是否包含指定code
     *
     * @param code
     * @return true or false
     */
    public static boolean isContain(String code) {
        return (get(code) != null);
    }

}
