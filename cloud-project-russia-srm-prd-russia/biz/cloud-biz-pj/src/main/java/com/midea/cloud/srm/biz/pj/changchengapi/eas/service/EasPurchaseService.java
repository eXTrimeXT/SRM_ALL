package com.midea.cloud.srm.biz.pj.changchengapi.eas.service;
/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
public interface EasPurchaseService {

    /**
     * pushDeliveryNote
     * @param data
     * @throws Exception
     */
    void pushDeliveryNote(Object data) throws Exception;

    /**
     * pushInvoiceNotice
     * @param data
     * @throws Exception
     */
    void pushInvoiceNotice(Object data) throws Exception;

    /**
     * getActualStock
     * @param data
     * @return
     * @throws Exception
     */
    Object getActualStock(Object data) throws Exception;
}
