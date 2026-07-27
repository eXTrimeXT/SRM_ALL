package com.midea.cloud.srm.sup.ext.pjsupplier.repo;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.repository.QlContext;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.mideacloud.common.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 附件自定义二开
 *
 * @author LUXC18
 * @date 2023/9/28 17:42
 */
@Slf4j
@Component
public class PjManagementAttachRepository extends ProxyRepository {

    public PjManagementAttachRepository() {
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {
        if(!QlContext.isProcessingRelation()){
            return super.save(noProxy(queryAction));
        }
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        recs.forEach(r -> {
            r.put("managementInfoId", IdGenerator.generate());
        });
        return super.save(noProxy(queryAction));
    }
}
