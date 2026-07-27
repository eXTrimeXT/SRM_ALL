package com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service;

import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementApprovalUnPassDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementHeadDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlRequirementAbandonDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;

/**
 * mql - 采购申请立项事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/18
 */
public interface MqlPrRequirementInitEventService {

    /**
     * 编辑采购申请单
     * @param param 采购申请单数据
     * @return 返回
     */
    MqlPrRequirementHeadDTO editRequirement(MqlPrRequirementHeadDTO param);

    /**
     * 删除采购申请单
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     * @return 返回
     */
    MqlPrRequirementHeadVO removeRequirement(long requirementHeadId);

    /**
     * 废弃采购申请单
     * @param param 废弃信息
     */
    void abandonRequirement(MqlRequirementAbandonDTO param);

    /**
     * 复制采购申请单
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     * @param appUser 操作人信息
     * @return 返回
     */
    long/* requirementHeadId */ copyRequirement(long requirementHeadId, LoginAppUser appUser);

    /**
     * 释放采购申请未使用的预算
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    void releaseRequirementBudget(long requirementHeadId);

    /**
     * 立项审批提交后的回调处理
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    void callbackAfterApprovalSubmit(long requirementHeadId);

    /**
     * 立项审批通过后的回调处理
     * @param requirementHeadId {@link PrRequirementHead#getRequirementHeadId}
     */
    void callbackAfterApprovalPass(long requirementHeadId);

    /**
     * 立项审批未通过后的回调处理
     * @param param 回调参数
     */
    void callbackAfterApprovalUnPass(MqlPrRequirementApprovalUnPassDTO param);

}
