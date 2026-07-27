package com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto;

import com.midea.cloud.srm.supcooperate.meiql.deliverynote.dto.DeliveryNoteDetailDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zenghx2
 */
@Data
public class ExtDeliveryNoteDetail extends DeliveryNoteDetailDTO {

    private String extDetailStatus;
    private BigDecimal extStorageQty;
    private BigDecimal extCancelQty;
    private LocalDateTime extFinishTime;
    private BigDecimal extUnTaxAmount;
    private BigDecimal extInTaxAmount;
    private Long extCurrencyId;
    private String extCurrencyCode;
    private String extCurrencyName;
    private String extCancelReason;
    private LocalDateTime extStorageTime;
    private LocalDateTime extReceiveTime;

}
