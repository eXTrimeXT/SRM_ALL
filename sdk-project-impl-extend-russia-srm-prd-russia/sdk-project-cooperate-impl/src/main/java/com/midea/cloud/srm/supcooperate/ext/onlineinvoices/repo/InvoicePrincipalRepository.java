package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.repo;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.InvoicePrincipal;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Component
public class InvoicePrincipalRepository extends PurchaseRepository<Record> {

    public InvoicePrincipalRepository() {
        super(InvoicePrincipal.class.getSimpleName(), "principalId", "开票主体");
    }

    @Override
    public QlResult save(QlQueryAction queryAction) {
        Record record = getRecord(queryAction);
        List<Record> history = qlService.queryByWrapper(QlWrappers.query(schemaType)
                .eq(InvoicePrincipal::getOrgId, record.get(InvoicePrincipal::getOrgId)), Record.class);
        if (CollectionUtils.isNotEmpty(history)) {
            Assert.isTrue(history.get(0).get(InvoicePrincipal::getPrincipalId).equals(record.get(InvoicePrincipal::getPrincipalId)), "业务实体已存在");
        }

        return super.save(queryAction);
    }
}
