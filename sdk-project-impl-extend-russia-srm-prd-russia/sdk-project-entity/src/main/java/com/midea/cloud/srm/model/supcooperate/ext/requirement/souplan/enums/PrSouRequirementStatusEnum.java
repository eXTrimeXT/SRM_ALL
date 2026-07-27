package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums;

/**
 * 招标计划池 - 需求状态
 * PS: PR_SOU_REQUIREMENT_STATUS
 * @author huangbf3
 */
public enum PrSouRequirementStatusEnum {
    /**
     * 计划中
     */
    EXECUTING,
    /**
     * 项目中
     */
    PROJECT,
    /**
     * 已完成
     */
    FINISH,
    /**
     * 已取消
     */
    CANCEL,
    /**
     * 已变更
     */
    CHANGED;

    public static String getDictCode() {
        return "PR_SOU_REQUIREMENT_STATUS";
    }

}
