package com.midea.cloud.srm.sou.meiql.answer.repo;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlUpdateWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.sou.meiql.answer.service.AnswerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
 *  修改日期: 2023/10/17 09:48:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class ReplayRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    public ReplayRepository() {
        this.register("readByAnswer", this::readByAnswer, true, "查看回复");
    }

    @Autowired
    private AnswerService answerService;

    private QlResult readByAnswer(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Long answerId = recs.get(0).get(AnswerDTO::getAnswerId);
        Long vendorId = AppUserUtil.getLoginAppUser().getCompanyId();
        List<AnswerVendorDTO> list = qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                .eq(AnswerVendorDTO::getVendorId,vendorId)
                .eq(AnswerVendorDTO::getAnswerId,answerId),AnswerVendorDTO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            AnswerVendorDTO dto = list.get(0);
            Long replayId = dto.getReplayId();
            queryAction.setPayload(Arrays.asList(replayId));
            return super.read(queryAction);
        } else {
            return new QlResult();
        }
    }

    @Override
    public QlResult doSave(QlQueryAction queryAction, List<Record> recs) {
        QlResult qlResult = null;
        //只能保存单个
        if (CollectionUtils.isNotEmpty(recs)) {
            Long answerVendorId = recs.get(0).get(AnswerVendorDTO::getAnswerVendorId);
            answerService.checkConfirm(answerVendorId);
            qlResult = super.doSave(queryAction,recs);
            Long id = (Long) qlResult.getRecords().get(0);
            QlUpdateWrapper qlUpdateWrapper = QlWrappers.update(TypeEnum.AnswerVendor.getCode())
                    .eq(AnswerVendorDTO::getAnswerVendorId,answerVendorId)
                    .set(AnswerVendorDTO::getIfReplay, YesOrNo.YES.getValue())
                    .set(AnswerVendorDTO::getReplayId,id)
                    .set(AnswerVendorDTO::getLastReplayTime,new Date());
            qlService.updateByWrapper(qlUpdateWrapper);
        }
        return qlResult;
    }

    @Override
    public void afterRead(QlQueryAction queryAction, Collection<Record> records){
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        if(!UserType.BUYER.name().equals(user.getUserType())){
            List<Long> ids = new ArrayList<>();
            for (Record record : records) {
                if (null != record.get(AnswerVendorDTO::getIfRead)
                        && YesOrNo.NO.getValue().equals(record.get(AnswerVendorDTO::getIfRead) )) {
                    ids.add(record.get(AnswerVendorDTO::getAnswerId));
                }
            }
            //更新已读状态
            if (CollectionUtils.isNotEmpty(ids)) {
                QlUpdateWrapper qlUpdateWrapper = QlWrappers.update(queryAction.getType())
                        .in(AnswerVendorDTO::getAnswerId,ids)
                        .in(AnswerVendorDTO::getVendorId,user.getCompanyId())
                        .set(AnswerVendorDTO::getIfRead, YesOrNo.YES.getValue())
                        .set(AnswerVendorDTO::getReadTime,new Date());
                qlService.updateByWrapper(qlUpdateWrapper);
            }
        }
    }
}
