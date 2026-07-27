package com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class ExtDeliveryNoteSaveDTO extends ExtDeliveryNote {

    private List<ExtDeliveryNoteDetail> detailList;

}
