package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.srm.model.sou.recommvendor.dto.ExceptionSupplierDto;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * @Description: for srm 异常名录供应商
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentExceptionVendor extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentExceptionVendor start...");
        //异常名录供应商列表定义
        List<ExceptionSupplierDto> exceptionSupplierDtoList = new ArrayList<>(50);
        //查询异常名录
        if(CollectionUtils.isNotEmpty(riskRequest.getContactNameList())) {
            Map<String, Object> param = new HashMap<>(50);
            param.put("contactNameList", riskRequest.getContactNameList());
            param.put("ceeaContactMethodList", riskRequest.getCeeaContactMethodList());
            param.put("emailList", riskRequest.getEmailList());

            exceptionSupplierDtoList = RiskComponent.getInstance().getRecommvendorMapper().queryExceptionSupplier(param);
        }

        log.info("riskService VendorRiskPreTreatmentExceptionVendor end...");
        return new RiskResponse(exceptionSupplierDtoList);
    }
}
