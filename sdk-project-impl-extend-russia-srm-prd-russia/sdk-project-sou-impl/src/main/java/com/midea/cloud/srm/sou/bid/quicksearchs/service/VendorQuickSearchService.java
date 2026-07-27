package com.midea.cloud.srm.sou.bid.quicksearchs.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;

import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/04/03/ $
 * @Description: 供应商快查
 */
public interface VendorQuickSearchService {

    /**
     * 快速查询供应商-澄清
     * @param params
     * @return
     */
    PageInfo<ExtSouVendor> vendorQuickSearchForAnswer(Map<String, Object> params);
}
