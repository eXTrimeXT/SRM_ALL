package com.midea.cloud.srm.biz.pj.changchengapi.sign.service;

import com.midea.cloud.srm.model.pj.sign.dto.SignCallback;

/**
 * @author huangbf3
 */
public interface ISignService {

    /**
     * 契约锁状态回调
     * @param signCallback
     */
    void statusCallback(SignCallback signCallback);
}
