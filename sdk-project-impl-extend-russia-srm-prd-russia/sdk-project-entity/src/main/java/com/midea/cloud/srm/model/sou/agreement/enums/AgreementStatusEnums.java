package com.midea.cloud.srm.model.sou.agreement.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum AgreementStatusEnums {

    //状态
    DRAFT ("DRAFT","拟定"),
    EXECUTE ("EXECUTE","待执行"),
    EXECUTING ("EXECUTING","执行中"),
    STOP ("STOP","已终止"),
    EXPIRED ("EXPIRED","已失效"),

    //付款方式
    YHZZ ("1", "银行转账"),
    DH ("2", "电汇"),
    XJ ("3", "现金"),
    YHCDHP ("4", "银行承兑汇票"),
    SYCDHP("5", "商业承兑汇票"),
    QT ("6", "其他"),
    ZP ("7", "支票"),

    //付款条款
    NET90 ("NET90", "收票后90天"),
    NET80 ("NET80", "收票后80天"),
    NET60 ("NET60", "收票后60天"),
    NET45 ("NET45", "收票后45天"),
    NET40 ("NET40", "收票后40天"),
    NET30 ("NET30", "收票后30天"),
    NET10 ("NET10", "收票后10天"),
    GR90 ("GR90", "收货后90天"),
    GR80 ("GR80", "收货后80天"),
    GR60 ("GR60", "收货后60天"),
    GR45 ("GR45", "收货后45天"),
    GR40 ("GR40", "收货后40天"),
    GR30 ("GR30", "收货后30天"),
    GR10 ("GR10", "收货后10天");

    private String code;
    private String name;

    AgreementStatusEnums(String code, String name) {
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
