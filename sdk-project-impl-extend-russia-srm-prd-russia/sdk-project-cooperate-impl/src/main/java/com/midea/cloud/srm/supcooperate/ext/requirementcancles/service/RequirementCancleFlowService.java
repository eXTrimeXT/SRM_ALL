package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface RequirementCancleFlowService {

    /**
     * 取消审批流程
     * @param businessId
     * @param businessCode
     */
    public void cancleFlow(Long businessId, String businessCode);
}
