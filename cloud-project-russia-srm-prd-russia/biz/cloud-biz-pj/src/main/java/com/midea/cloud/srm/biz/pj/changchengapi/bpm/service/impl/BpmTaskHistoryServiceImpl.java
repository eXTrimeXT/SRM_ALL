package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.mapper.BpmTaskHistoryMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmTaskHistoryService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmFlowList;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmTaskHistory;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangbf3
 * BPM审批历史实现类
 */
@Slf4j
@Service
public class BpmTaskHistoryServiceImpl extends BaseServiceImpl<BpmTaskHistoryMapper, BpmTaskHistory> implements IBpmTaskHistoryService  {
    @Autowired
    private IBpmService iBpmService;
    @Override
    public void savePassBpmTaskHistory(FlowInstanceRecord record) {
        if(StringUtils.isBlank(record.getInstanceId())||record.getBusinessId()==null){
            return;
        }
        try{
            JSONObject param = new JSONObject();
            param.put("processInsId",record.getInstanceId());
            BpmResultDTO<List<BpmFlowList>> bpmResultDTO = iBpmService.getCommentAndTodoTaskList(param);
            List<BpmTaskHistory> bpmTaskHistories = new ArrayList<>(bpmResultDTO.getData().size());

            for(Object object:bpmResultDTO.getData()){
                BpmTaskHistory bpmTaskHistory = JSONObject.parseObject(JSONObject.toJSONString(object),BpmTaskHistory.class);
                bpmTaskHistory.setOrderId(record.getBusinessId());
                bpmTaskHistory.setBpmTaskHistoryId(IdGenrator.generate());

                bpmTaskHistories.add(bpmTaskHistory);
            }

            this.lambdaUpdate().eq(BpmTaskHistory::getOrderId,record.getBusinessId()).remove();
            this.saveBatch(bpmTaskHistories);
        }catch (Exception e){
            log.info("保存审批通过历史报错:{}",e.getMessage());
        }
    }
}
