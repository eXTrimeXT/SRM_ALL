package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto;

import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceAdvanceApplyDTO;
import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceOcrInvoiceDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class ExtOnlineInvoiceSaveDTO extends ExtOnlineInvoice {

    private List<ExtOnlineInvoiceDetail> detailList;
    private List<OnlineInvoiceAdvanceApplyDTO> advanceApplyList;
    private List<OnlineInvoiceOcrInvoiceDTO> ocrInvoiceList;
    private List<ExtOnlineInvoicePunishDTO> punishList;
}
