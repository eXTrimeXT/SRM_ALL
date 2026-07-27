package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Description: for srm 请求爬虫接口
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentCrawler extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentCrawler start...");
        //请求爬虫接口，返回爬虫HashMap缓存对象，key-value, key为供应商名称， value为请求PJ模块调用爬虫接口返回的对象
        Map<String, CompanyAQCApiDTO> companyAqcApiDtoMap = RiskComponent.getInstance().getSouRecommvendorRiskService()
                .crawler(riskRequest.getVendorRiskList(), true, riskRequest.getRecommvenorServiceDictItems());
        log.info("riskService VendorRiskPreTreatmentCrawler end...");
        //响应，data为爬虫HashMap缓存对象
        return new RiskResponse(companyAqcApiDtoMap);
    }
}
