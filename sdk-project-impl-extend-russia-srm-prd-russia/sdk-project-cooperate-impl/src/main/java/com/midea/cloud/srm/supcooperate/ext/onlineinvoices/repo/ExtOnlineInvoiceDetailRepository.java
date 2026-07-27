package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.repo;

import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.OnlineInvoiceSourceEnum;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoiceDetail;
import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceDetailDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Component
public class ExtOnlineInvoiceDetailRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        String source = records.iterator().next().get(ExtOnlineInvoiceDetail::getExtSource);
        if (OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(source)) {
            List<Long> invoiceNoticeDetailIds = records.stream().map(e -> e.get(OnlineInvoiceDetailDTO::getInvoiceDetailId)).collect(Collectors.toList());
            List<Record> details = qlService.readByKeys(InvoiceNoticeDetail.class.getSimpleName(), invoiceNoticeDetailIds, Record.class);
            Map<Long, BigDecimal> qtyMap = details.stream().collect(Collectors.toMap(e -> e.get(InvoiceNoticeDetail::getInvoiceDetailId), e -> e.get(InvoiceNoticeDetail::getNotInvoiceQuantity)));
            records.stream().forEach(e -> {
                e.put(ExtOnlineInvoiceDetail::getNotInvoiceQuantity, qtyMap.get(e.get(ExtOnlineInvoiceDetail::getInvoiceDetailId)));
            });
        } else {
            List<Long> checkOrderDetailIds = records.stream().map(e -> e.get(ExtOnlineInvoiceDetail::getExtCheckDetailId)).collect(Collectors.toList());
            List<Record> details = qlService.readByKeys(CheckOrderDetail.class.getSimpleName(), checkOrderDetailIds, Record.class);
            Map<Long, BigDecimal> qtyMap = details.stream().collect(Collectors.toMap(e -> e.get(CheckOrderDetail::getCheckOrderDetailId), e -> BigDecimalUtil.sub(e.get(CheckOrderDetail::getCheckQty), e.get(CheckOrderDetail::getInvoiceQty))));
            records.stream().forEach(e -> {
                e.put(ExtOnlineInvoiceDetail::getNotInvoiceQuantity, qtyMap.get(e.get(ExtOnlineInvoiceDetail::getExtCheckDetailId)));
            });
        }

    }
}
