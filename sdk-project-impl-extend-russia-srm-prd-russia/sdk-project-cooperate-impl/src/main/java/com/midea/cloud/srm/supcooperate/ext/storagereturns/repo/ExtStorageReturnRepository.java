package com.midea.cloud.srm.supcooperate.ext.storagereturns.repo;

import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Component
public class ExtStorageReturnRepository extends PurchaseRepository<WarehousingReturnDetail> {

    public ExtStorageReturnRepository() {
        super("StorageReturn","warehousingReturnDetailId", "入库退货明细");

        register("extClose", this::extClose, true, "关闭");
    }

    private QlResult extClose(QlQueryAction qlQueryAction) {
        Record record = getRecord(qlQueryAction);
        List<Long> warehousingReturnDetailIds = ((List<Long>) record.get("warehousingReturnDetailIds"));
        String closeCause = (String)record.get("extClosedCause");
        Assert.notEmpty(warehousingReturnDetailIds, "出入库明细id不能为空");
        List<Record> records = qlService.readByKeys(schemaType, warehousingReturnDetailIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(warehousingReturnDetailIds, records, businessName);
        records.forEach(e->{
            Assert.isTrue(e.get(WarehousingReturnDetail::getNotInvoiceQuantity).compareTo(BigDecimal.ZERO)>0, "已开票不能关闭");
        });
        qlService.updateByWrapper(QlWrappers.update(schemaType)
                .set("extClosedCause", closeCause)
                .set(WarehousingReturnDetail::getNotInvoiceQuantity, BigDecimal.ZERO)
                .in(WarehousingReturnDetail::getWarehousingReturnDetailId, warehousingReturnDetailIds));
        return QlResult.empty();
    }


    @Override
    public QlResult query(QlQueryAction queryAction) {
        QlResult result = super.query(queryAction);
        PurchaseMqlUtils.buildResult(result, PurchaseSchemaEnum.STORAGE_RETURN.getType(), e -> {
            BigDecimal taxPrice = e.get(WarehousingReturnDetail::getUnitPriceContainingTax);
            BigDecimal noTaxPrice = e.get(WarehousingReturnDetail::getUnitPriceExcludingTax);
            BigDecimal qty = e.get(WarehousingReturnDetail::getReceiveNum);
            BigDecimal taxTotalAmount = BigDecimalUtil.mul(taxPrice, qty);
            BigDecimal noTaxTotalAmount = BigDecimalUtil.mul(noTaxPrice, qty);
            BigDecimal invoiceNoticeQty = BigDecimalUtil.sub(qty, e.get(WarehousingReturnDetail::getNotInvoiceQuantity));
            e.put("taxTotalAmount", taxTotalAmount);
            e.put("noTaxTotalAmount", noTaxTotalAmount);
            e.put("taxAmount", BigDecimalUtil.sub(taxTotalAmount, noTaxTotalAmount));
            e.put("invoiceNoticeQty", invoiceNoticeQty);
            e.put("invoiceNoticeFlag", invoiceNoticeQty.compareTo(BigDecimal.ZERO) > 0 ? "是" : "否");
        });
        return result;
    }
}
