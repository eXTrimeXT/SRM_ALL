package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源 - 工作小组角色
 * 字典值: SOU_GROUP_RULE
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/21
 */
public enum SouGroupRoleEnum {

    /**
     * 成本经理
     */
    COST_MANAGER,
    /**
     * 物流经理
     */
    LOGISTIC_MANAGER,
    /**
     * 研发经理
     */
    DEVELOP_MANAGER,
    /**
     * 高级经理
     */
    SENIOR_MANAGER,
    /**
     * 质量经理
     */
    QUALITY_MANAGER,
    /**
     * 财务经理
     */
    FINANCE_MANAGER;

    public static String getEnumDictCode() {
        return "SOU_GROUP_ROLE";
    }

}
