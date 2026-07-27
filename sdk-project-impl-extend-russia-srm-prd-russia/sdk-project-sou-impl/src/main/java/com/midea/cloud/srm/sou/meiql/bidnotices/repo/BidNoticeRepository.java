package com.midea.cloud.srm.sou.meiql.bidnotices.repo;

import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
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
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitEventService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class BidNoticeRepository extends CrudRepository {
    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private QlService qlService;

    public BidNoticeRepository() {
        //注册action
        this.register("submit",this::submit,true,"提交");
        this.register("abandon",this::abandon,true,"废弃");
        //去掉保存事务
        this.register("save", this::save, false, "保存");
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
        //初始化状态为拟定，类型为定标申请
        for (Record rec : recs) {
            if (StringUtils.isEmpty(rec.get(BidNoticeDTO::getStatus))) {
                rec.put(BidNoticeDTO::getStatus, BidNoticeStatusEnum.DRAFT.getCode());
                rec.put(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode());
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
            rec.put(BidNoticeDTO::getStatus, CaStatusEnum.ABANDON.getCode());
        }
        //修改寻源单据状态
        this.updateSouStatus(recs);
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(),recs));
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
            qlCondition.eq(BidNoticeDTO::getType,CaTypeEnum.APPLY.getCode());
        }
        return qlCondition;
    }

    @Override
    public QlResult delete(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //修改寻源状态
        updateSouStatus(recs);
        return super.delete(queryAction);
    }


    private void updateSouStatus(List<Record> recs) {
        //修改寻源单据状态
        List<Long> ids = recs.stream().map(r -> r.get(BidNoticeDTO::getBidNoticeId)).collect(Collectors.toList());
        List<BidNoticeDTO> bidNoticeDtos = qlService.readByKeys(TypeEnum.BidNotice.getCode(),ids,BidNoticeDTO.class);
        if (null != bidNoticeDtos && !bidNoticeDtos.isEmpty()) {
            for (BidNoticeDTO bidNoticeDTO : bidNoticeDtos) {
                //修改招标单据状态
                if (null != bidNoticeDTO.getProjectId() && SouTypeEnum.bid.name().equals(bidNoticeDTO.getSouType())) {
                    extSouInitEventService.updateSouBidStatus(bidNoticeDTO.getProjectId(),SouBiddingProStatusEnum.WIN_LOSS_NOTICE);
                }
            }
        }
    }

    @Override
    public void afterRead(QlQueryAction queryAction, Collection<Record> records) {
        super.afterRead(queryAction, records);
        records.forEach(record->{
            String applicantNo =  extSouInitQueryService.getApplicantNo(record.get(BidNoticeDTO::getProjectId));
            String applicantId =  extSouInitQueryService.getApplicantId(applicantNo);
            record.put(BidNoticeDTO::getApplicantNo, applicantNo);
            record.put(BidNoticeDTO::getApplicantId, applicantId);
        });
    }
}
