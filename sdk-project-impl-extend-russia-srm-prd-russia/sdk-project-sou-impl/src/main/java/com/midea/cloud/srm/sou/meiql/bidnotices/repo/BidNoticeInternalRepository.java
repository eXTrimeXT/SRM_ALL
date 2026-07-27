package com.midea.cloud.srm.sou.meiql.bidnotices.repo;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeInternalDTO;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class BidNoticeInternalRepository extends CrudRepository {
    @Autowired
    private BidNoticeService bidNoticeService;
    public BidNoticeInternalRepository() {
        //注册action
        this.register("send",this::send,true,"发送内部");
        //去掉保存事务
        this.register("save", this::save, false, "保存");
    }

    private QlResult send(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for (Record record : recs) {
            record.put(BidNoticeInternalDTO::getIsSend, YesOrNo.YES.getValue());
            record.put(BidNoticeInternalDTO::getSendTime,new Date());
            //生成合同
//            bidNoticeService.createContract(record.get(BidNoticeInternalDTO::getInternalId));
        }
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(),recs));
    }
}
