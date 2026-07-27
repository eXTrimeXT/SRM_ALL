package com.midea.cloud.srm.supcooperate.ext.invoicenotices.dto;

import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExtInvoiceNoticeDetail extends InvoiceNoticeDetail {

    private static final long serialVersionUID = 6102854415177921441L;
    private String extSyncEas;

    private String extMaterialModel;
}
