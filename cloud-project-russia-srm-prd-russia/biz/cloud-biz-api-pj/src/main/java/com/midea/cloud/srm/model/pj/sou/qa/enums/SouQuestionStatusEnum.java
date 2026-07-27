package com.midea.cloud.srm.model.pj.sou.qa.enums;

/**
 * 项目式询价.质疑状态
 *
 * @author zhangwk12@midea.com
 * @since 2022/06/16
 */
public enum SouQuestionStatusEnum {

    /**
     * 拟定：创建质疑，未提交
     * 未澄清：提交质疑，采购商未澄清
     * 已澄清：采购商针对此质疑已经发布澄清
     * 已驳回：采购商驳回质疑
     */
    DRAFT,
    SUBMITTED,
    CLARIFIED,
    REJECTED;

}