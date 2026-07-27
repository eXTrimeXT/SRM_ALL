package com.midea.cloud.srm.sou.meiql.answer.repo;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayFileDTO;
import com.midea.cloud.srm.model.sou.enums.BidSignStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
public class ReplayFileRepository extends CrudRepository {
    @Autowired
    private QlService qlService;

    @Override
    public QlResult doSave(QlQueryAction queryAction, List<Record> recs) {
        if (CollectionUtils.isNotEmpty(recs)) {
            List<Long> replayFileIds = recs.stream().filter(r ->null != r.get(ReplayFileDTO::getReplayFileId)).map(r -> r.get(ReplayFileDTO::getReplayFileId)).collect(Collectors.toList());
            //判断是否存在已签署的附件
            List<ReplayFileDTO> list = qlService.queryByWrapper(QlWrappers.query(TypeEnum.ReplayFile.getCode())
                    .in(ReplayFileDTO::getReplayFileId,replayFileIds)
                    .eq(ReplayFileDTO::getSignStatus, BidSignStatusEnum.SIGN.getCode()),ReplayFileDTO.class);
            //如果存在已签署，不能修改签署状态，解决状态并发问题,满足两个条件 同一个id，当前为未签署，数据库为已签署
            if (CollectionUtils.isNotEmpty(list)) {
                Map<Long,ReplayFileDTO> map = list.stream().collect(Collectors.toMap(ReplayFileDTO::getReplayFileId, Function.identity(), (k1, k2)->k2));
                for (Record record : recs) {
                    Long replayFileId = record.get(ReplayFileDTO::getReplayFileId);
                    String signStatus = record.get(ReplayFileDTO::getSignStatus);
                    if (null != replayFileId && map.containsKey(replayFileId) && BidSignStatusEnum.NOT_SIGN.getCode().equals(signStatus)) {
                        record.put(ReplayFileDTO::getFileId, map.get(replayFileId).getFileId());
                        record.put(ReplayFileDTO::getFileName, map.get(replayFileId).getFileName());
                        record.put(ReplayFileDTO::getSignStatus, map.get(replayFileId).getSignStatus());
                        record.put(ReplayFileDTO::getSignTime, map.get(replayFileId).getSignTime());
                    }
                }
            }
        }
        return super.doSave(queryAction,recs);
    }
}
