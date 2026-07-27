package com.midea.cloud.srm.biz.pj.baseregions.mapper;

import com.midea.cloud.srm.model.base.region.entity.Region;

import java.util.List;
import java.util.Map;
/**
 * @author huangbf3
 */
public interface BaseRegionMapper {

    /**
     * 备注
     * @param param
     * @return
     */
    List<Region> queryRegion(Map<String, Object> param);
}
