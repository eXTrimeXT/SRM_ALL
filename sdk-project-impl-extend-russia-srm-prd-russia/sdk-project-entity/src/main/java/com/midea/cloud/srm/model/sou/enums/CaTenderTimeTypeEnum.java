package com.midea.cloud.srm.model.sou.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum CaTenderTimeTypeEnum {
    /**
     * 递交招标资料
     */
    SUBMITE("SUBMITE", "递交招标资料"),
    /**
     * 发出招标文件
     */
    SEND_BID("SEND_BID", "发出招标文件"),
    /**
     * 收标
     */
    ACCEPT_BID("ACCEPT_BID", "收标"),
    /**
     * 澄清变更及评技术标
     */
    CLARIFY_TECH("CLARIFY_TECH", "澄清变更及评技术标"),
    /**
     * 商务标分析汇总
     */
    BUS_ANALYSIS("BUS_ANALYSIS", "商务标分析汇总"),
    /**
     * 定标
     */
    DECIDE_BID("DECIDE_BID", "定标")
    ;

    private String code;
    private String name;

    CaTenderTimeTypeEnum(String code, String name) {
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
