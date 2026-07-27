package com.midea.cloud.srm.supcooperate.ext.deliverynotes.mapper;


import com.midea.cloud.srm.model.supcooperate.excel.DeliveryNoteExcel;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.QueryDeliveryNote;

import java.util.List;

/**
 * @Author: fu
 * @Date: 2024/09/23/ $
 * @Description: 送货单主表查询
 */
public interface ExtDeliveryNoteMapper {

    /**
     * orderIds
     * @param queryDeliveryNote
     * @return
     */
    public List<DeliveryNoteExcel> deliveryList(DeliveryNoteExcel queryDeliveryNote);
}
