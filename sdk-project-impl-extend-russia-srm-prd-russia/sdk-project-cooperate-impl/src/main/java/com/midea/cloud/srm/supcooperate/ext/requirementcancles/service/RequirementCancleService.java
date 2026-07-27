package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface RequirementCancleService {

    /**
     * cancle
     * @param requirementHeadIdList
     * @param requirementHeadNumMap
     * @param localCache
     * @return
     */
    public Object cancle(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap, HashMap<String, Object> localCache);
}
