package com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.feign.ExtSupplierClient;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.sou.meiql.recommvendor.mapper.RecommvendorMapper;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouVendorMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Description: 供应商风险查询注入依赖Bean组件
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@Slf4j
@Data
public class RiskComponent {
    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private ExtSupplierClient extSupplierClient;

    @Autowired
    private SouRecommvendorRiskService souRecommvendorRiskService;

    @Autowired
    private RecommvendorMapper recommvendorMapper;

    @Autowired
    private ExtSouVendorMapper extSouVendorMapper;

    @Resource
    private ExtSouProjectMapper projectMapper;

    private static RiskComponent riskComponent;

    public static RiskComponent getInstance() {
        return riskComponent;
    }

    private RiskComponent() {

    }

    @PostConstruct
    private void inite() {
        riskComponent = this;
    }

}
