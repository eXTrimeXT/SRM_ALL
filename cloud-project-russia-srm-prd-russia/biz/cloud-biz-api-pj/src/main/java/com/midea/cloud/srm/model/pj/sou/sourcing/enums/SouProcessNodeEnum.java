package com.midea.cloud.srm.model.pj.sou.sourcing.enums;

/**
 * 寻源.流程节点
 * 字典值: SOU_PROCESS_NODE
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public enum SouProcessNodeEnum {

    /**
     * 项目信息
     */
    projectInfo,
    /**
     * 2 : 需求信息
     */
    requireInfo,
    /**
     * 3 : 邀请供应商
     */
    inviteVendor,
    /**
     * 4 : 评分规则
     */
    scoreRule,
    /**
     * 5 : 立项审批
     */
    createApproval,
    /**
     * 6 : 报名管理
     */
    signUpManagement,
    /**
     * 7 : 投标控制
     */
    bidingControl,
    /**
     * 8 : 技术标管理
     */
    techManagement,
    /**
     * 9 : 商务标管理
     */
    businessManagement,
    /**
     * 10:  评选
     */
    evaluation;

    public static String getEnumDictCode() {
        return "SOU_PROCESS_NODE";
    }

}
