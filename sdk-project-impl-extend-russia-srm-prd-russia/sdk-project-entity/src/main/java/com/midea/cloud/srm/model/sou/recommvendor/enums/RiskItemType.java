package com.midea.cloud.srm.model.sou.recommvendor.enums;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public enum RiskItemType {
    //{"联系人重复", "联系人电话重复", "联系人邮箱重复", "法人重复",
    //            "关联关系黑名单", "股东重复", "主要人员重复", "关联关系供应商"}
    LINKMAN_NAME(0, "联系人重复"),
    LINKMAN_TEL(1, "联系人电话重复"),
    LINKMAN_MAIL(2, "联系人邮箱重复"),
    LEGAL(3, "法人重复"),
    BLACKLIST(4, "关联关系黑名单"),
    HOLDER(5, "股东重复"),
    MAIN_PERSON(6, "主要人员重复"),
    RELATIONS_VENDOR(7, "关联关系供应商"),
    NAME(8, "名字重复"),
    TEL(9, "电话重复"),
    EMAIL(10, "邮箱重复"),
    MONITORING(11, "供应商风险"),
    HIS_LINKNAME(12,"报名联系人重复"),
    HIS_PHONE(13,"报名联系电话重复"),
    HIS_EMAIL(14,"报名邮箱重复"),
    CATEGORY_RESTRICTION(15,"品类受限")
    ;

    private Integer index;

    private String type;

    RiskItemType(Integer index, String type) {
        this.index = index;
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }
}
