package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 *供应商历史报名重复信息-联系人 电话 邮箱
 * @author GW00311146
 */
@Slf4j
public class VendorRiskPreTreatmentRecommHistoryReg extends AbstractRiskPretreatment {
    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentRecommHistoryReg start...");

        Map<Long, RecommvendorDto> recommvendorDtoMap = (Map<Long, RecommvendorDto>) riskRequest.getData();
        Map<String, List<ExtSouVendor>> vendorRiskHistoryRegistrationInfo = new HashMap<>(16);
        if (Objects.isNull(recommvendorDtoMap)) {
            return new RiskResponse(vendorRiskHistoryRegistrationInfo);
        }

        List<Long> vendorIdList = new ArrayList<>(recommvendorDtoMap.keySet());;
        List<String> linkNameList = new ArrayList<>();
        List<String> phoneList = new ArrayList<>();
        List<String> emailList = new ArrayList<>();

        vendorIdList.forEach(vendorId -> {
            linkNameList.add(recommvendorDtoMap.get(vendorId).getLinkmanName());
            phoneList.add(recommvendorDtoMap.get(vendorId).getPhone());
            emailList.add(recommvendorDtoMap.get(vendorId).getEmail());
        });
        //名字重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("v.vendor_id", vendorIdList);
        queryWrapper.in("v.linkman_name", linkNameList);
        List<ExtSouVendor> nameRepList = RiskComponent.getInstance().getExtSouVendorMapper().listVendor(queryWrapper);
        //联系方式重复
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.in("v.vendor_id", vendorIdList);
        queryWrapper1.in("v.phone", phoneList);
        List<ExtSouVendor> phoneRepList = RiskComponent.getInstance().getExtSouVendorMapper().listVendor(queryWrapper1);
        //邮箱重复
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.in("v.vendor_id", vendorIdList);
        queryWrapper2.in("v.email", emailList);
        List<ExtSouVendor> emailRepList = RiskComponent.getInstance().getExtSouVendorMapper().listVendor(queryWrapper1);

        vendorRiskHistoryRegistrationInfo.put("nameRepList", nameRepList);
        vendorRiskHistoryRegistrationInfo.put("phoneRepList", phoneRepList);
        vendorRiskHistoryRegistrationInfo.put("emailRepList", emailRepList);


        log.info("riskService VendorRiskPreTreatmentRecommHistoryReg end...");
        return new RiskResponse(vendorRiskHistoryRegistrationInfo);
    }
}
