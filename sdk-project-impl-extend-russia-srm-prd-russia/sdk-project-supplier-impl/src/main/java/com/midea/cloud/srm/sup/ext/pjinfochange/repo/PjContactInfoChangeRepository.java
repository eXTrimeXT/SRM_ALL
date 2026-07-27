package com.midea.cloud.srm.sup.ext.pjinfochange.repo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.model.supplier.change.entity.ContactInfoChange;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 100014323
 */
@Slf4j
@Component
public class PjContactInfoChangeRepository extends ProxyRepository {

    @Autowired
    private QlService qlService;

    @Override
    public QlResult save(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        QlResult qlResult = null;
        if (CollectionUtils.isNotEmpty(recs)) {
            for (Record contactInfoChange : recs) {
                if (null == contactInfoChange.get(ContactInfoChange::getContactInfoId)) {
                    contactInfoChange.put(ContactInfoChange::getContactInfoId, IdGenrator.generate());
                }
                if(contactInfoChange.getLong("socialSecurityCertificateFileId") == null &&
                        contactInfoChange.getLong("contactChangeId") != null){
                    qlService.updateByWrapper(QlWrappers.update(MqlType.CONTACT_INFO_CHANGE)
                            .set("socialSecurityCertificateFileId",null)
                            .set("socialSecurityCertificateFileName",null)
                            .eq("contactChangeId",contactInfoChange.getLong("contactChangeId"))
                    );
                }
            }
            qlResult = super.save(OpenApiUtil.convertSaveRequest(queryAction.getType(),queryAction.getAction(),recs));
        }
        return qlResult;
    }

}