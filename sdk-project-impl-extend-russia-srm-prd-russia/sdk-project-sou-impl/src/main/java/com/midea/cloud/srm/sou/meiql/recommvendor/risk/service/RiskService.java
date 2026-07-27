package com.midea.cloud.srm.sou.meiql.recommvendor.risk.service;

import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;

/**
 * @Description: 供应商风险抽象类策略接口定义
 *
 * @author srm
 * @date 2024-05-18
 */
public interface RiskService {

    /**
     * todo
     * @param riskRequest
     * @return
     */
    RiskResponse todo(RiskRequest riskRequest);
}
