package com.midea.cloud.srm.supcooperate.ext.invoicenotices.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.supcooperate.excel.InvoiceNoticeDetailVo;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.mapper.ExtInvoiceNoticeMapper;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.service.ExtInvoiceNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/6
 */
@Service
public class ExtInvoiceNoticeServiceImpl implements ExtInvoiceNoticeService{

    @Autowired
    private ExtInvoiceNoticeMapper extInvoiceNoticeMapper;

    @Override
    public List<InvoiceNoticeDetailVo> GetById(Long invoiceNoticeId){

        return  extInvoiceNoticeMapper.GetById(invoiceNoticeId);
    }
}
