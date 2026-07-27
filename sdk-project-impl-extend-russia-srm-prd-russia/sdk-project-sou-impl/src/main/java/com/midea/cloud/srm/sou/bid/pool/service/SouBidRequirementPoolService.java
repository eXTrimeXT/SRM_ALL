package com.midea.cloud.srm.sou.bid.pool.service;

import com.midea.cloud.srm.model.sou.pool.dto.SouBidRequirementPoolDto;

import java.util.List;

/**
 * 招标计划需求池
 * @author huangbf3
 */
public interface SouBidRequirementPoolService {

    /**
     * 查询招标计划池信息
     * @param param
     * @return
     */
    SouBidRequirementPoolDto getRequirementPoolInfo(SouBidRequirementPoolDto param);
}
