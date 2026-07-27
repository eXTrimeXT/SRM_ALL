package com.midea.cloud.srm.sou.bid.turntos.service;

import com.midea.cloud.srm.model.sou.bidturns.dto.NpmSouBidTurnRquestParamDto;

/**
 * @Author: panmq
 * @Date: 2024/04/07/ $
 * @Description: 招标负责人转办接口
 */
public interface NpmSouBidTurnToService {

    /**
     * 转办责任人
     * @param paramDto
     * @return
     */
    Long turnBidPricipal(NpmSouBidTurnRquestParamDto paramDto);
}
