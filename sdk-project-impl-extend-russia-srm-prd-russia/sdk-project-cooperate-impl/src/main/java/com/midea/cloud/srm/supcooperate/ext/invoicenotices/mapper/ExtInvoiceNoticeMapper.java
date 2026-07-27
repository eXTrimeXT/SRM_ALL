package com.midea.cloud.srm.supcooperate.ext.invoicenotices.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.supcooperate.excel.InvoiceNoticeDetailVo;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/11
 */

@Mapper
public interface ExtInvoiceNoticeMapper extends BaseMapper<InvoiceNoticeDetailVo> {
    /**
     * GetById
     * @param id
     * @return
     */
    @Select("select * from scc_sc_invoice_notice_detail where INVOICE_NOTICE_ID = #{id}")
    List<InvoiceNoticeDetailVo> GetById(Long id);


}

