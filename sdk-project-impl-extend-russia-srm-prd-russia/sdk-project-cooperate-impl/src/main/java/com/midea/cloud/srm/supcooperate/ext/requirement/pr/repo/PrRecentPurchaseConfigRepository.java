package com.midea.cloud.srm.supcooperate.ext.requirement.pr.repo;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrRecentPurchaseConfig;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * @author zenghx2
 */
@Component
public class PrRecentPurchaseConfigRepository extends PurchaseRepository<PrRecentPurchaseConfig> {

    public PrRecentPurchaseConfigRepository() {
        super("PrRecentPurchaseConfig", "configId", "近期采购配置");
    }

    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeCreate(queryAction, records);
        Record record = getRecord((List<Record>) records);
        long count = qlService.countByWrapper(QlWrappers.query(schemaType)
                .eq(PrRecentPurchaseConfig::getOrgId, record.get(PrRecentPurchaseConfig::getOrgId)));
        Assert.isTrue(count == 0, "采购单位已配置");
    }

    @Override
    protected void beforeUpdate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeUpdate(queryAction, records);
        Record record = getRecord((List<Record>) records);
        long count = qlService.countByWrapper(QlWrappers.query(schemaType)
                .eq(PrRecentPurchaseConfig::getOrgId, record.get(PrRecentPurchaseConfig::getOrgId))
                .notEq(PrRecentPurchaseConfig::getConfigId, record.get(PrRecentPurchaseConfig::getConfigId)));
        Assert.isTrue(count == 0, "采购单位已配置");
    }
}


