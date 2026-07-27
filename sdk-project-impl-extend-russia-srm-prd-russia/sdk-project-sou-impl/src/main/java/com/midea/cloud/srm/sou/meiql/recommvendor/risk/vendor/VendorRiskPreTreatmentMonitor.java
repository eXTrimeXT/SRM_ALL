package com.midea.cloud.srm.sou.meiql.recommvendor.risk.vendor;

import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.DictCodeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supplier.risk.entity.Monitoring;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.abstracts.AbstractRiskPretreatment;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.compent.RiskComponent;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskRequest;
import com.midea.cloud.srm.sou.meiql.recommvendor.risk.pojo.RiskResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm 供应商风险管理
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class VendorRiskPreTreatmentMonitor extends AbstractRiskPretreatment {

    private final static String STATUS_MONITORING = "MONITORING";

    @Override
    public RiskResponse todo(RiskRequest riskRequest) {
        log.info("riskService VendorRiskPreTreatmentMonitor start...");

        Map<Long, List<String>> monitoringMap = new HashMap<>(16);

        Map<Long, String> vendorNameMap = new HashMap<>(16);
        if(CollectionUtils.isNotEmpty(riskRequest.getVendorIdList())) {
            List<Monitoring> monitorings = RiskComponent.getInstance().getQlOpenClient().query(ContextPath.SUP, QlOpenWrappers.query(
                    MqlType.MONITORING
            ).eq(Monitoring::getStatus, STATUS_MONITORING).in(Monitoring::getVendorId, riskRequest.getVendorIdList()), Monitoring.class);

            if(CollectionUtils.isNotEmpty(monitorings)) {
                Map<String, List<DictItemDTO>> dictMap = riskRequest.getDictMap();
                List<DictItemDTO> riskTypeList = dictMap.getOrDefault(DictCodeEnum.RISK_TYPE.getCode(), new ArrayList<>());
                List<DictItemDTO> riskLevelList = dictMap.getOrDefault(DictCodeEnum.RISK_LEVEL.getCode(), new ArrayList<>());

                Map<String, String> riskTypeMap = riskTypeList.stream().collect(Collectors.toMap(k -> k.getDictItemCode(), v -> v.getDictItemName(), (k1, k2)->k1));

                Map<String, String> riskLevelMap = riskLevelList.stream().collect(Collectors.toMap(k -> k.getDictItemCode(), v -> v.getDictItemName(), (k1, k2)->k1));

                monitorings.stream().forEach(monitoring -> {
                    //推荐的供应商名称如果存在在供应商风险管理中，并且供应商管理的状态为监控中，则在“查看风险”中显示(“供应商风险”，风险影响类型-风险等级-风险发生后影响描述 这几个内容-拼接 )
                    List<String> monitoringNameList = new ArrayList<>(3);
                    monitoringNameList.add(MapUtils.getString(riskTypeMap, monitoring.getRiskType(), ""));
                    monitoringNameList.add(MapUtils.getString(riskLevelMap, monitoring.getRiskLevel(), ""));
                    monitoringNameList.add(StringUtils.defaultString(monitoring.getRiskInfluencesDescription(), ""));

                    if(!monitoringMap.containsKey(monitoring.getVendorId())) {
                        monitoringMap.put(monitoring.getVendorId(), new ArrayList<>(16));
                    }

                    monitoringMap.get(monitoring.getVendorId()).add(monitoringNameList.stream().collect(Collectors.joining(SrmConstant.SHORT_LINE)));
                    vendorNameMap.put(monitoring.getVendorId(), monitoring.getVendorName());
                });
            }
        }

        //供应商风险管理中状态为监控中的供应商，key-value, key为供应商ID，value为风险描述内容
        Map<Long, String> monitoringInfoMap = new HashMap<>(16);
        for(Long vendorId: monitoringMap.keySet()) {
            monitoringInfoMap.put(vendorId, StringUtils.join(vendorNameMap.get(vendorId), "存在风险: ", monitoringMap.get(vendorId).stream().distinct().collect(Collectors.joining(SrmConstant.SIG_1))));
        }

        log.info("riskService VendorRiskPreTreatmentMonitor end...");
        return new RiskResponse(monitoringInfoMap);
    }



}
