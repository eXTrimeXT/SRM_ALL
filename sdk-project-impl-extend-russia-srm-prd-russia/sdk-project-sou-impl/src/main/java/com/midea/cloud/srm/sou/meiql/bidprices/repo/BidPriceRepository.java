package com.midea.cloud.srm.sou.meiql.bidprices.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@Slf4j
public class BidPriceRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    public BidPriceRepository() {
        this.register("edit", this::edit, "编辑");
    }


    public QlResult edit(QlQueryAction queryAction) {
        PayloadWrapper payload = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload());
        List<Record> records = payload.asRecords();
        List<Record> editList = qlService.queryByWrapper(QlWrappers.query(queryAction.getType()).in(BidPriceDto::getParentBidPriceId, records.stream().map(r -> r.get(BidPriceDto::getBidPriceId)).collect(Collectors.toList())), Record.class);
        return MeiQl.toResult(queryAction.getType(), QlQueryFieldWrapper.field(BidPriceDto::getBidPriceId).getFieldName(), editList);
    }
}
