package com.midea.cloud.srm.sou.approve.service.impl;

import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.sou.approve.service.ApproveCallBackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApproveComCallBackServiceImpl implements ApproveCallBackService {
    @Override
    public Long submit(Long businessId) {
        return null;
    }

    @Override
    public Long pass(Long businessId) {
        return null;
    }

    @Override
    public Long reject(Long businessId) {
        return null;
    }

    @Override
    public Long withdraw(Long businessId) {
        return null;
    }

    @Override
    public Long extendOperation(Long businessId, SouApprovalStatusEnum type, Map<String, Object> extendParams) {
        return null;
    }
}
