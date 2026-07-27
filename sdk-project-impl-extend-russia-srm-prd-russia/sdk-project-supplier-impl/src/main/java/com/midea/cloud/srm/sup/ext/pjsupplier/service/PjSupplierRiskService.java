package com.midea.cloud.srm.sup.ext.pjsupplier.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;

import java.util.List;
/**
 * @author luxc18
 */
public interface PjSupplierRiskService {

    /**
     * 查询供应商风险-黑名单
     * @param vendorIdList
     * @return
     */
    List<Record> querySupplierRiskBlacklist(List<Long> vendorIdList);
}
