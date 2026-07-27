package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service;


import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmFlowList;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;

import java.util.List;

/**
 * @author huangbf3
 */
public interface BusinessCallBpmService{


    /**
     * 备注
     * @param businessId
     * @param businessType
     * @param commentmsg
     * @return
     */
    BpmResultDTO rollBackProcess(Long businessId,String businessType,String commentmsg);

    /**
     * 备注
     * @param businessId
     * @param businessType
     * @return
     */
    BpmResultDTO<List<BpmFlowList>> approvalRecord(Long businessId, String businessType);

}
