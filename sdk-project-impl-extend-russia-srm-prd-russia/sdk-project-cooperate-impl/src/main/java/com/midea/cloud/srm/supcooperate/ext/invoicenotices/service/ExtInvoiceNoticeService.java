package com.midea.cloud.srm.supcooperate.ext.invoicenotices.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.supcooperate.excel.InvoiceNoticeDetailVo;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;

import java.util.List;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/6
 */
public interface ExtInvoiceNoticeService /*extends IService<InvoiceNoticeDetail>*/ {
    /**
     * GetById
     * @param invoiceNoticeId
     * @return
     */
    public List<InvoiceNoticeDetailVo> GetById(Long invoiceNoticeId);

}
