package com.midea.cloud.srm.sou.meiql.recommvendor.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorQuickQueryParam;
/**
 * 备注
 * @author huangbf3
 */
public interface SouRecommvendorQuitSearchService {

    /**
     * 查询供应商列表
     * @param param
     * @return
     */
    PageInfo queryVendor(RecommvendorQuickQueryParam  param);
}
