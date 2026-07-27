package com.midea.cloud.srm.supcooperate.ext.requirement.pr.repo;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrVendorConfig;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * @author zenghx2
 */
@Component
public class PrVendorConfigRepository extends PurchaseRepository<PrVendorConfig> {

    public PrVendorConfigRepository() {
        super("PrVendorConfig", "configId", "历史供应商配置");

    }

    @Override
    protected void beforeCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeCreate(queryAction, records);
        Record record = getRecord((List<Record>) records);
        long count = qlService.countByWrapper(QlWrappers.query(schemaType)
                .eq(PrVendorConfig::getOrgId, record.get(PrVendorConfig::getOrgId))
                .eq(PrVendorConfig::getStatus, YesOrNo.YES.getValue()));
        Assert.isTrue(count == 0, "管理单元已配置");
    }

    @Override
    protected void beforeUpdate(QlQueryAction queryAction, Collection<Record> records) {
        super.beforeUpdate(queryAction, records);
        Record record = getRecord((List<Record>) records);
        long count = qlService.countByWrapper(QlWrappers.query(schemaType)
                .eq(PrVendorConfig::getOrgId, record.get(PrVendorConfig::getOrgId))
                .notEq(PrVendorConfig::getConfigId, record.get(PrVendorConfig::getConfigId))
                .eq(PrVendorConfig::getStatus, YesOrNo.YES.getValue()));
        Assert.isTrue(count == 0, "管理单元已配置");
    }
}
