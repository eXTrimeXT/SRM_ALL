package com.midea.cloud.srm.sou.agreement.service;

import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;

/**
 * @author 100014336 ganyh19
 * 集采合同对接
 */
public interface JcAgreementContractService {

    /**
     * 保存集采协议信息
     * @param sccSouJcAgreement
     * @return
     */
    Long save(SccSouJcAgreement sccSouJcAgreement);
}
