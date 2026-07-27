package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums;

/**
 * 竞价MQL - 流程节点
 * 字典值: SOU_AUCT_PROCESS_NODE
 *
 * @author zhangwk12@midea.com
 * @since 2023/07/10
 */
public enum AuctSouProcessNodeEnum {

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
     * 立项审批
     */
    createApproval,

    /**
     * 报名管理
     */
    signUpManagement,

    /**
     * 10:  评选
     */
    evaluation,

    /**
     * 保证金管理
     */
    bondManagement,

    /**
     * 竞价大厅
     */
    auctHall;

    public static String getDictCode() {
        return "SOU_AUCT_PROCESS_NODE";
    }

}
