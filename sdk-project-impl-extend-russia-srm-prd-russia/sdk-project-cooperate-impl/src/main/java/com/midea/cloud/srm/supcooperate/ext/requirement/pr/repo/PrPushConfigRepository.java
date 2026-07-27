package com.midea.cloud.srm.supcooperate.ext.requirement.pr.repo;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrPushConfig;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author zenghx2
 */
@Component
public class PrPushConfigRepository extends PurchaseRepository<PrPushConfig> {

    public PrPushConfigRepository() {
        super("PrPushConfig", "configId", "自动分单配置");
    }

    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeCreate(queryAction, records);
//        Record record = getRecord((List<Record>) records);
//        long count = qlService.countByWrapper(QlWrappers.query(schemaType)
//                .eq(PrPushConfig::getOrgId, record.get(PrPushConfig::getOrgId))
//                .eq(PrPushConfig::getStatus, YesOrNo.YES.getValue()));
//        Assert.isTrue(count == 0, "管理单元已配置");
    }

    @Override
    protected void beforeUpdate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeUpdate(queryAction, records);
//        Record record = getRecord((List<Record>) records);
//        long count = qlService.countByWrapper(QlWrappers.query(schemaType)
//                .eq(PrPushConfig::getOrgId, record.get(PrPushConfig::getOrgId))
//                .notEq(PrPushConfig::getConfigId, record.get(PrPushConfig::getConfigId))
//                .eq(PrPushConfig::getStatus, YesOrNo.YES.getValue()));
//        Assert.isTrue(count == 0, "管理单元已配置");
    }
}
