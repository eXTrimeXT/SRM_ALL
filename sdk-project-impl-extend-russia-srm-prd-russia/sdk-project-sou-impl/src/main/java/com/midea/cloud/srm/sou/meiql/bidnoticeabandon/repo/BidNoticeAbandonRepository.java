package com.midea.cloud.srm.sou.meiql.bidnoticeabandon.repo;

import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class BidNoticeAbandonRepository extends CrudRepository {

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    public BidNoticeAbandonRepository() {
        //注册action
        this.register("submit",this::submit,true,"提交");
        this.register("abandon",this::abandon,true,"废弃");
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        this.initValues(recs);
        return super.doSave(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value()),recs);
    }

    /**
     * 初始化值
     * @param recs
     */
    private void initValues(List<Record> recs) {
        //初始化状态为拟定，类型为废标申请
        for (Record rec : recs) {
            if (StringUtils.isEmpty(rec.get(BidNoticeDTO::getStatus))) {
                rec.put(BidNoticeDTO::getStatus, BidNoticeStatusEnum.DRAFT.getCode());
                rec.put(BidNoticeDTO::getType, CaTypeEnum.DESTORY.getCode());
            }
        }
    }

    @Override
    public QlResult doSave(QlQueryAction queryAction,List<Record> recs) {
        this.initValues(recs);
        return super.doSave(queryAction,recs);
    }

    private QlResult abandon(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for (Record rec : recs) {
            rec.put(BidNoticeDTO::getStatus, BidNoticeStatusEnum.ABANDON.getCode());
        }
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(),recs));
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
            qlCondition.eq(BidNoticeDTO::getType,CaTypeEnum.DESTORY.getCode());
        }
        return qlCondition;
    }

    @Override
    public void afterRead(QlQueryAction queryAction, Collection<Record> records) {
        super.afterRead(queryAction, records);
        records.forEach(record->{
            String applicantNo =  extSouInitQueryService.getApplicantNo(record.get(BidNoticeDTO::getProjectId));
            String applicantid =  extSouInitQueryService.getApplicantId(applicantNo);
            record.put(BidNoticeDTO::getApplicantNo, applicantNo);
            record.put(BidNoticeDTO::getApplicantId, applicantid);
        });
    }
}
