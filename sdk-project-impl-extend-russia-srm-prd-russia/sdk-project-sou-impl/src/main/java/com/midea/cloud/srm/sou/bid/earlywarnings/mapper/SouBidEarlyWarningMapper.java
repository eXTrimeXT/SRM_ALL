package com.midea.cloud.srm.sou.bid.earlywarnings.mapper;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface SouBidEarlyWarningMapper {

    /**
     * listPlan
     * @param param
     * @return
     */
    List<ExtSouPlan> listPlan(Map<String, Object> param);
}
