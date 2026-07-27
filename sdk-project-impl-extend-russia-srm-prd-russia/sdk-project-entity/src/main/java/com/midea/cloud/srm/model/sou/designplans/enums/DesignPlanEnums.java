package com.midea.cloud.srm.model.sou.designplans.enums;
/**
 * 备注
 * @author huangbf3
 */
public enum DesignPlanEnums {
    /**
     * 拟定
     **/
    DRAFT("DRAFT", "拟定"),
    /**
     * 审批中
     **/
    APPROVING("APPROVING", "审批中"),
    /**
     * 已审批
     **/
    APPROVED("APPROVED", "已审批"),
    /**
     * 已驳回
     **/
    REJECTED("REJECTED", "已驳回"),
    /**
     * 已废弃
     **/
    ABANDON("ABANDON", "已废弃"),
    /**
     * 已撤回
     **/
    WITHDRAW("WITHDRAW", "已撤回");

    private String code;
    private String name;

    DesignPlanEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }


    public static String getDesignPlanEnumsName(String code) {
        for (DesignPlanEnums p : DesignPlanEnums.values()) {
            if (code.equals(p.getCode())) {
                return p.getName();
            }
        }
        return null;
    }
}
