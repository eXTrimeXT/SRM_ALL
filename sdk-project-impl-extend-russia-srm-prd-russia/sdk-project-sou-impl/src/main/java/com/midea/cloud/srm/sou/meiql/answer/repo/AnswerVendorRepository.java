package com.midea.cloud.srm.sou.meiql.answer.repo;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerConfirmStatusEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.sou.bid.event.ExtBidSouEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/17 18:23:54
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class AnswerVendorRepository extends CrudRepository {

    @Autowired
    private QlService qlService;
    @Autowired
    private ExtBidSouEvent extBidSouEvent;

    public AnswerVendorRepository() {
        this.register("confirm", this::confirm, true, "澄清确认");
    }

    private QlResult confirm(QlQueryAction queryAction) {
        String code = AnswerConfirmStatusEnum.COMFIRMED.getCode();
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        if(CollectionUtils.isEmpty(recs)){
            throw new BaseException("确认数据不能为空");
        }
        Long answerId = recs.get(0).get(AnswerVendorDTO::getAnswerId);
        for (Record record : recs) {
            record.put(AnswerVendorDTO::getConfirmStatus, code);
        }
        QlResult qlResult = super.doUpdate(queryAction, recs);
        // 判断是否全部明细行都已确认，是则修改头状态为已确认。
        List<Record> records = qlService.queryByWrapper(QlWrappers.query(MqlType.ANSWER_VENDOR)
                .eq(AnswerVendorDTO::getAnswerId, answerId), Record.class);
        List<Record> list = records.stream().filter(record ->
                StringUtils.isEmpty(record.get(AnswerVendorDTO::getConfirmStatus)) || !code.equals(record.get(AnswerVendorDTO::getConfirmStatus)))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(list)){
            Record answer = new Record();
            answer.put(AnswerDTO::getAnswerId, answerId);
            answer.put(AnswerDTO::getAnswerStatus, code);
            qlService.update(MqlType.ANSWER, Collections.singletonList(answer));
            //推送ai
            extBidSouEvent.pushAnswerEvent(answerId);
        }
        return qlResult;
    }
}
