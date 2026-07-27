package com.midea.cloud.srm.sou.purinq.flow;

import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouCreateApprovalUnPassDTO;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.sourcing.init.service.SouInitEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: for srm
 * 集采询比价询比价 - 立项审批流回调
 * PS: EXT_SOU_PURINQ_CREATE
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouInitWorkFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private SouInitEventService souInitEventService;

    /**
     * 提交审批流后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void submitFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalSubmit(projectId, ExtPurInqSouTypeEnum.ext_pur_inq.name());
    }

    /**
     * 审批通过后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void passFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalPass(projectId, ExtPurInqSouTypeEnum.ext_pur_inq.name());
    }

    /**
     * 审批驳回后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void rejectFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(projectId, SouApprovalStatusEnum.REJECTED), ExtPurInqSouTypeEnum.ext_pur_inq.name());
    }

    /**
     * 审批撤回后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void withdrawFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(projectId, SouApprovalStatusEnum.WITHDRAW), ExtPurInqSouTypeEnum.ext_pur_inq.name());
    }

    /**
     * 审批作废后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void destoryFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(projectId, SouApprovalStatusEnum.ABANDONED), ExtPurInqSouTypeEnum.ext_pur_inq.name());
    }

    @Override
    @Nullable
    public String getVariableFlow(Long projectId, String param) {
        return null;
    }

    @Override
    @Nullable
    public String getDataPushFlow(Long projectId, String param) {
        return null;
    }

}
