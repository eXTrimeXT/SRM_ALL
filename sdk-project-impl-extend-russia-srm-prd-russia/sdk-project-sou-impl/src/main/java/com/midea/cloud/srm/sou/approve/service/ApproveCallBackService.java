package com.midea.cloud.srm.sou.approve.service;

import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;

import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
public interface ApproveCallBackService {

    /**
     * 提交
     * @param businessId
     * @return
     */
    public Long submit(Long businessId);

    /**
     * 通过
     * @param businessId
     * @return
     */
    public Long pass(Long businessId);

    /**
     * 驳回
     * @param businessId
     * @return
     */
    public Long reject(Long businessId);

    /**
     * 撤回
     * @param businessId
     * @return
     */
    public Long withdraw(Long businessId);

    /**
     * 扩展的回调方法
     * @param businessId 备注
     * @param type 备注
     * @param extendParams 备注
     * @return 备注
     */
    public Long extendOperation(Long businessId, SouApprovalStatusEnum type, Map<String, Object> extendParams);
}
