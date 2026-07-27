package com.midea.cloud.srm.model.sou.sourcing.enums;

/**
 * 备注
 * @author huangbf3
 */
public enum SouProcessNodeEnum {
    /**
     * 1 : 项目信息
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
    evaluation,
    /**
     * 编制定标结果
     */
    bidReuslt,
    /**
     * 中/落标通知
     */
    bidWinOrLoss,
    /**
     * 归档
     */
    bidArchive,
    /**
     * 保证金管理
     */
    bondManagement;

    public static String getEnumDictCode() {
        return "SOU_PROCESS_NODE";
    }
}
