package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.PjProjectBidExtClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCallback;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.RequirementCancleFlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: for srm 取消审批流程实现类
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Service
public class RequirementCancleFlowServiceImpl implements RequirementCancleFlowService {
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private PjProjectBidExtClient pjProjectBidExtClient;

    @Override
    public void cancleFlow(Long businessId, String businessCode) {
        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(MqlType.FLOW_INSTANCE_RECORD)
                .eq(FlowInstanceRecord::getBusinessId, businessId).eq(FlowInstanceRecord::getTemplateCode, businessCode)
                .orderByDesc(FlowInstanceRecord::getCreationDate));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return;
        }
        RecordDTO recordDTO = recordDTOList.get(0);
        if(StringUtils.isBlank(recordDTO.get(FlowInstanceRecord::getInstanceId))) {
            return;
        }

        BpmCallback callback = new BpmCallback();
        callback.setProcessInstId(recordDTO.get(FlowInstanceRecord::getInstanceId));
        callback.setActionName(SouConstant.BPM_FLOW_RETURN);

        try {
            pjProjectBidExtClient.callback(callback);
        } catch (Exception e) {
            log.error("cancleFlow Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
