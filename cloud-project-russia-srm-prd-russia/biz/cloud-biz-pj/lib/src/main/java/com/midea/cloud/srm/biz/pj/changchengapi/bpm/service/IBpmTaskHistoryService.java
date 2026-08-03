package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmTaskHistory;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;

/**
 * bpm审批历史接口类
 * @author huangbf3
 */
public interface IBpmTaskHistoryService extends BaseService<BpmTaskHistory> {
    /**
     * 保存bpm审批通过历史
     * @param record 参数
     */
    void savePassBpmTaskHistory(FlowInstanceRecord record);
}
