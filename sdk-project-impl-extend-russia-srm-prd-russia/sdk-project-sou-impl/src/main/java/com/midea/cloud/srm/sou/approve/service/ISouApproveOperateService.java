package com.midea.cloud.srm.sou.approve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveOperate;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;

/**
 * @description scc_npm_sou_approve_operate
 * @author panmq2
 * @date 2023-10-23
 */
public interface ISouApproveOperateService extends IService<SouApproveOperate> {

    /**
     * 审批操作
     * @param businessId 参数
     * @param type 参数
     * @param description 参数
     * @param callbackBean 参数
     * @return 返回
     */
    public SouApproveOperate operate(Long businessId, SouApprovalStatusEnum type, String description, String callbackBean);

    /**
     * 获取审批人最新的审批意见
     * @param approveUserId 参数
     * @return 返回
     */
    public SouApproveOperate getNewestOperate(Long approveUserId);
}

