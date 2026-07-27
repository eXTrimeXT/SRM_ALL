package com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums;

/**
 * 招标计划 - 项目计划 - 项目状态
 * PS: PR_SOU_PROJECT_PLAN_TYPE
 * @author huangbf3
 */
public enum ExtPrSouProjectPlanStatusEnum {
    /**
     * 正常
     */
    NORMAL,
    /**
     * 完结
     */
    FINISH,
    /**
     * 取消
     */
    CANCEL;

    public static String getDictCode() {
        return "PR_SOU_PROJECT_PLAN_TYPE";
    }

}
