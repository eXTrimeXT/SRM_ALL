package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface ExtOnlineInvoiceService {

    /**
     * pushEasInvoice
     * @param onlineInvoice
     * @param onlineInvoiceDetails
     * @param ocrInvoiceList
     */
    void pushEasInvoice(Record onlineInvoice, List<Record> onlineInvoiceDetails, List<Record> ocrInvoiceList);

}
