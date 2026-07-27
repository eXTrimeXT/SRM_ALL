package com.midea.cloud.srm.sou.sourcing.bpmtodo.service;

import com.midea.cloud.srm.model.sou.bpmtodo.dto.SouBpmtodoParam;
import com.midea.cloud.srm.model.sou.bpmtodo.dto.SouBpmtodoResponse;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/31
 */
public interface ExtSouBpmtodoService {

    /**
     * 招标流程手机待办-商务标信息查询接口
     * @param param
     * @return
     */
    public SouBpmtodoResponse queryBusTodoInfo(SouBpmtodoParam param);
}
