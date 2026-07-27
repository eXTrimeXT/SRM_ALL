package com.midea.cloud.srm.sou.inq.select.service;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;

/**
 * @author ex_liuxy46
 */
public interface SiInqSouSelectEventWebService {

    /**
     * 采购商：发起新一轮
     * @param param 参数
     * @param reason 参数
     */
    void startNewRound(ApiSouStartNewRoundDTO param, String reason);
}
