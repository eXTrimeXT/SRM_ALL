package com.midea.cloud.srm.biz.pj.sou.inq.service;

import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
public interface PjInqSouInitEventWebService {
    /**
     * editInq
     * @param param
     * @return
     */
    long editInq(ApiInqSouInitDTO param);
}
