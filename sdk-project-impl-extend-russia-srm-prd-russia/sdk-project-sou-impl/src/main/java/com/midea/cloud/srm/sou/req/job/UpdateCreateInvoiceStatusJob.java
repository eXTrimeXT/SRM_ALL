package com.midea.cloud.srm.sou.req.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositInvoiceStatusEnum;
import com.midea.cloud.srm.sou.req.service.SouIntDepositInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  定时查询（两个小时）状态为“已提交”的数据，触发查询接口《财务共享-发票结算结果查询》，有结果回写状态，开具成功or开具失败
 * </pre>
 *
 * @author huangbf3@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/2 15:32
 *  修改内容:
 * </pre>
 */
@Job("updateCreateInvoiceStatusJob")
public class UpdateCreateInvoiceStatusJob implements ExecuteableJob {
    @Autowired
    private QlService qlService;
    @Autowired
    private SouIntDepositInvoiceService souIntDepositInvoiceService;
    @Override
    public BaseResult executeJob(Map<String, String> params) {
        List<SouIntDepositInvoice> invoices = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_DEPOSIT_INVOICE_BUYER)
                        .notEq(SouIntDepositInvoice::getCreateInvoiceStatus, YesOrNo.Y.name())
                        .isNotNull(SouIntDepositInvoice::getInvoiceNo)
                        .isNotNull(SouIntDepositInvoice::getSettleDocumentCode)
                        .eq(SouIntDepositInvoice::getStatus,IntDepositInvoiceStatusEnum.INVOICING.getCode())
                ,SouIntDepositInvoice.class);
        if(invoices!=null){
            for(SouIntDepositInvoice invoice:invoices){
                souIntDepositInvoiceService.updateCreateInvoiceStatus(invoice);
            }
        }
        return BaseResult.buildSuccess("执行成功！");
    }
}
