package com.midea.cloud.srm.sou.meiql.recommvendor.risk.factory;

import com.midea.cloud.srm.sou.meiql.recommvendor.risk.service.RiskService;

/**
 * @Description: 供应商风险查询抽象工厂类
 *
 * @author srm
 * @date 2024-05-18
 */
public abstract class AbstractRiskFactory {

    /**
     * 获取供应商风险查询生成策略接口
     * @return
     */
    public abstract RiskService riskDataGenerator();

    /**
     * 根据类名获取供应商风险前置查询数据策略接口
     * @param classSimpleName
     * @return
     */
    public abstract RiskService riskPreTreatment(String classSimpleName);

    /**
     * 获取供应商风险查询整合异常数据策略接口
     * @return
     */
    public abstract RiskService riskDataTreating();
}
