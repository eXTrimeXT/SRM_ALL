package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service;

import java.util.List;

/**
 * 招标计划创建校验
 * @author huangbf3
 */
public interface PrSouRequirementCreateValidService {

    /**
     * 校验招标计划需求池校验创建
     * @param requirementHeadIdList
     * @param souType
     */
    public void requirementCreateValid(List<Long> requirementHeadIdList, String souType);
}
