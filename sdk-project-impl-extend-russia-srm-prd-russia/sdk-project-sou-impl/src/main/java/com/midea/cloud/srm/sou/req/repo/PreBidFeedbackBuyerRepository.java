package com.midea.cloud.srm.sou.req.repo;

import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.sou.req.PreBidFeedback;
import com.midea.cloud.srm.model.sou.req.PreBidFeedbackVendor;
import com.midea.cloud.srm.model.sou.req.enums.PreBidFeedbackStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.VendorFeedbackStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/17 17:25
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class PreBidFeedbackBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    public PreBidFeedbackBuyerRepository() {
        //注册action
        this.register("submit", this::submit, true, "提交");
        this.register("reject", this::reject, true, "驳回");
    }

    private QlResult reject(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        //更新状态为已反馈
        qlService.updateByWrapper(QlWrappers.update(PreBidFeedbackVendor.class)
                .set(PreBidFeedbackVendor::getFeedbackStatus, VendorFeedbackStatusEnum.REJECT.getCode())
                .eq(PreBidFeedbackVendor::getBidFeedbackId,record.get(PreBidFeedback::getBidFeedbackId))
                .eq(PreBidFeedbackVendor::getVendorId, record.get("vendorId")));
        return new QlResult();
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        records.forEach(l->l.set(PreBidFeedback::getStatus, PreBidFeedbackStatusEnum.ISSUED));
        return super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
    }
}
