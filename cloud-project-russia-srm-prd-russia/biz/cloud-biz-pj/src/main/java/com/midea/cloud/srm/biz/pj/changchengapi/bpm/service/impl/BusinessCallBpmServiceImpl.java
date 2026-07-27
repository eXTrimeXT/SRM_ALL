package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.BusinessCallBpmService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmFlowList;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmRollBackDTO;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class BusinessCallBpmServiceImpl implements BusinessCallBpmService {

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    private IBpmService iBpmService;

    @Override
    public BpmResultDTO rollBackProcess(Long businessId, String businessType, String commentmsg) {

        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("FlowInstanceRecord");
        wrapper.eq(FlowInstanceRecord::getBusinessId, businessId);
        wrapper.eq(FlowInstanceRecord::getTemplateCode, businessType);
        wrapper.orderByDesc(FlowInstanceRecord::getFlowInstanceRecordId);
        List<FlowInstanceRecord> list = qlOpenClient.query(ContextPath.BASE,wrapper,FlowInstanceRecord.class);

        FlowInstanceRecord flowInstanceRecord = new FlowInstanceRecord();
        BpmRollBackDTO bpmRollBackDTO = new BpmRollBackDTO();

        if(CollectionUtil.isNotEmpty(list)){
            flowInstanceRecord = list.get(0);
            //流程实例id
            bpmRollBackDTO.setProcessinstid(flowInstanceRecord.getInstanceId());
            //创建人
            bpmRollBackDTO.setCreateuser(flowInstanceRecord.getCreatedBy());
            //撤消原因
            bpmRollBackDTO.setCommentmsg(commentmsg);
        }
        return iBpmService.rollBackProcess(bpmRollBackDTO,businessId.toString());
    }

    @Override
    public BpmResultDTO<List<BpmFlowList>> approvalRecord(Long businessId, String businessType) {

        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("FlowInstanceRecord");
        wrapper.eq(FlowInstanceRecord::getBusinessId, businessId);
        wrapper.eq(FlowInstanceRecord::getTemplateCode, businessType);
        wrapper.orderByDesc(FlowInstanceRecord::getFlowInstanceRecordId);
        List<FlowInstanceRecord> list = qlOpenClient.query(ContextPath.BASE,wrapper,FlowInstanceRecord.class);
        FlowInstanceRecord flowInstanceRecord = new FlowInstanceRecord();

        JSONObject map = new JSONObject();
        if(CollectionUtil.isNotEmpty(list)) {
            map.put("processInsId",list.get(0).getInstanceId());
        }
        BpmResultDTO<List<BpmFlowList>>  bpmFlowList =  iBpmService.getCommentAndTodoTaskList(map);
        return bpmFlowList;
    }
}
