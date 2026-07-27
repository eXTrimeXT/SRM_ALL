package com.midea.cloud.srm.supcooperate.ext.invoicenotices.dto;

import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class ExtInvoiceNoticeSaveDTO extends ExtInvoiceNotice{
    private List<InvoiceNoticeDetail> detailList;
}
