package com.midea.cloud.srm.sup.ext.pjsupplier.service;

import org.springframework.web.bind.annotation.RequestParam;
/**
 * @author luxc18
 */
public interface PjSupplierService {

    /**
     * 获取MDM编码
     * @param companyId
     */
    void getMdmCodeByCompanyId(Long companyId);

    /**
     * 发送钉钉
     * @param companyId
     */
    void sendDingDingMsg(Long companyId);

}
