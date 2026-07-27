package com.midea.cloud.srm.supcooperate.ext.deliverynotes.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNote;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.QueryDeliveryNote;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author zenghx2
 */
public interface ExtDeliveryNoteService {
    /**
     * 备注
     * @param deliveryNoteDetails
     * @param cancelReason
     */
    void cancelDeliveryDetails(List<Record> deliveryNoteDetails, String cancelReason);

    /**
     * 备注
     * @param deliveryNoteIds
     * @return
     */
    List<Record> updateDeliveryNoteExtStatus(List<Long> deliveryNoteIds);

    /**
     * releaseOrder
     * @param deliveryNoteDetails
     **/
    void releaseOrder(List<Record> deliveryNoteDetails);

    /**
     * syncEas
     * @param deliveryNote
     * @param deliveryNoteDetails
     * @param delivery
     */
    void syncEas(Record deliveryNote, List<Record> deliveryNoteDetails, boolean delivery);

    /**
     * 根据京东子订单号创建送货单
     * @param jdOrderIds
     */
    void createDeliveryNoteByJDOrderIds(List<String> jdOrderIds);
    /**
     * 送货单导出
     * @param extDeliveryNote
     */
    void deliveryNotesUpload(HttpServletResponse response, QueryDeliveryNote queryDeliveryNote) throws IOException;
}
