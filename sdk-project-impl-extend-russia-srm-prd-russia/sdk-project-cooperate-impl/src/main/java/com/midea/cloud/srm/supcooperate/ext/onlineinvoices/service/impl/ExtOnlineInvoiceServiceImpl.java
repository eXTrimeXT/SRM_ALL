package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.service.impl;

import cn.hutool.json.JSONUtil;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.pm.po.CeeaWarehousingReturnDetailEnum;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.OnlineInvoice;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.OnlineInvoiceOcrInvoice;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.OnlineInvoiceSourceEnum;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoice;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoiceDetail;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.service.ExtOnlineInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Component
public class ExtOnlineInvoiceServiceImpl implements ExtOnlineInvoiceService {

    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private QlService qlService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void pushEasInvoice(Record onlineInvoice, List<Record> onlineInvoiceDetails, List<Record> ocrInvoiceList) {
        if (!OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(onlineInvoice.get(ExtOnlineInvoice::getExtSource))) {
            return;
        }

        StringBuffer invoiceCodes = new StringBuffer();
        StringBuffer invoiceNumbers = new StringBuffer();
        if (CollectionUtils.isNotEmpty(ocrInvoiceList)) {
            AtomicInteger i = new AtomicInteger(0);
            ocrInvoiceList.forEach(e -> {
                invoiceCodes.append(e.get(OnlineInvoiceOcrInvoice::getInvoiceName));
                invoiceNumbers.append(e.get(OnlineInvoiceOcrInvoice::getInvoiceNum));
                if (i.incrementAndGet() < ocrInvoiceList.size()) {
                    invoiceCodes.append(";");
                    invoiceNumbers.append(";");
                }
            });
        }

        // 修改已推送状态
        qlService.updateByWrapper(QlWrappers.update(OnlineInvoice.class.getSimpleName())
                .set(ExtOnlineInvoice::getExtSyncEas, YesOrNo.YES.getValue())
                .eq(OnlineInvoice::getOnlineInvoiceId, onlineInvoice.get(OnlineInvoice::getOnlineInvoiceId)));

        // 推送eas
        List<Record> lines = onlineInvoiceDetails.stream().map(e -> {
            Record record = new Record();
            //EAS源单编码（入库单编码）
            record.put("billno", e.get(ExtOnlineInvoiceDetail::getReceiveOrderNo));
            //入库单顺序号
            record.put("seq", e.get(ExtOnlineInvoiceDetail::getReceiveOrderLineNo));
            //数量
            record.put("qty", e.get(ExtOnlineInvoiceDetail::getInvoiceQuantity));
            //税率
            record.put("taxrate", e.get(ExtOnlineInvoiceDetail::getTaxRate));
            //未税单价
            record.put("price", e.get(ExtOnlineInvoiceDetail::getUnitPriceExcludingTax));
            //含税单价
            record.put("taxprice", e.get(ExtOnlineInvoiceDetail::getUnitPriceContainingTax));
            if (CeeaWarehousingReturnDetailEnum.RETURN.name().equals(e.get(ExtOnlineInvoiceDetail::getType))) {
                //金额
                record.put("amount", BigDecimalUtil.sub(BigDecimal.ZERO, e.get(ExtOnlineInvoiceDetail::getNoTaxAmount)));
                //税额
                record.put("tax", BigDecimalUtil.sub(BigDecimal.ZERO, e.get(ExtOnlineInvoiceDetail::getTax)));
                //转出税额
                record.put("outtaxamount", BigDecimalUtil.sub(BigDecimal.ZERO,e.get(ExtOnlineInvoiceDetail::getExtInputTaxAmount)));
            } else {
                //金额
                record.put("amount", e.get(ExtOnlineInvoiceDetail::getNoTaxAmount));
                //税额
                record.put("tax", e.get(ExtOnlineInvoiceDetail::getTax));
                //转出税额
                record.put("outtaxamount", e.get(ExtOnlineInvoiceDetail::getExtInputTaxAmount));
            }
            //SAP成本中心编码
            record.put("sapcostcenter", e.get(ExtOnlineInvoiceDetail::getExtSapCostCode));
            //发票用途编码
            record.put("invoicepur", e.get(ExtOnlineInvoiceDetail::getExtInvoiceUsage));
            //SAP成本中心类型
            record.put("sapcostentertype", e.get(ExtOnlineInvoiceDetail::getExtSapCostContent));
            return record;
        }).collect(Collectors.toList());

        Record head = new Record();
        //是否进项税转出(1是，0否)
        head.put("isjxszc", YesOrNo.YES.getValue().equals(onlineInvoice.get(ExtOnlineInvoice::getExtInputTax)) ? 1 : 0);
        //是否免增(1是，0否)
        head.put("ismz", YesOrNo.YES.getValue().equals(onlineInvoice.get(ExtOnlineInvoice::getExtFreeOfCharge)) ? 1 : 0);
        //是否代开(1是，0否)
        head.put("isotherremark", YesOrNo.YES.getValue().equals(onlineInvoice.get(ExtOnlineInvoice::getExtBehalfInvoice)) ? 1 : 0);
        //发票号码
        head.put("invoicenumber", invoiceNumbers.toString());
        //发票编码
        head.put("invoicecode", invoiceCodes.toString());
        //单据金额（不含税金额）
        head.put("amount", onlineInvoice.get(ExtOnlineInvoice::getExcluTaxTotalAmount));
        //单据金额（含税金额）
        head.put("taxamount", onlineInvoice.get(ExtOnlineInvoice::getTaxTotalAmount));
        //税额
        head.put("totaltax", onlineInvoice.get(ExtOnlineInvoice::getTotalTax));
        //制单人工号
        head.put("creator", onlineInvoice.get(ExtOnlineInvoice::getCreatedBy));
        head.put("entries", lines);
        log.info("推送eas开票单：{}", JSONUtil.toJsonStr(head));
        pjProjectExtClient.pushInvoiceNotice(head);
    }

}
