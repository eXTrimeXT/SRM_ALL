package com.midea.cloud.srm.sou.agreement.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementInfo;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouTieredPricing;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.sou.agreement.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 100014336 ganyh16
 */
@Service
public class JcAgreementContractServiceImpl implements JcAgreementContractService {

    @Resource
    private TieredPricingService tieredPricingService;



    @Resource
    private JcAgreementInfoService agreementInfoService;

    @Resource
    private JcAgreementService agreementService;


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Long save(SccSouJcAgreement sccSouJcAgreement) {
        sccSouJcAgreement.setAgreementStatus(AgreementStatusEnums.DRAFT.getCode());
        agreementService.saveOrUpdate(sccSouJcAgreement);
        List<SccSouJcAgreementInfo> sccSouJcAgreementInfoList = sccSouJcAgreement.getSccSouJcAgreementInfoList();
        if(CollUtil.isNotEmpty(sccSouJcAgreementInfoList)){
            saveJcAgreementInfo(sccSouJcAgreementInfoList,sccSouJcAgreement);
        }
        return sccSouJcAgreement.getAgreementId();
    }

    private void saveJcAgreementInfo(List<SccSouJcAgreementInfo> sccSouJcAgreementInfoList,SccSouJcAgreement sccSouJcAgreement) {
        if (CollectionUtils.isNotEmpty(sccSouJcAgreementInfoList)) {
            List<Long> infoIdList = new ArrayList<>();
            for (SccSouJcAgreementInfo e : sccSouJcAgreementInfoList) {
                if (e.getAgreementInfoId() != null) {
                    infoIdList.add(e.getAgreementInfoId());
                }
                e.setAgreementId(sccSouJcAgreement.getAgreementId());
            }
            removeAgreementInfo(infoIdList,sccSouJcAgreement);
            makeAgreementInfo(sccSouJcAgreementInfoList);

        }
    }

    private void makeAgreementInfo(List<SccSouJcAgreementInfo> sccSouJcAgreementInfoList) {
        for (SccSouJcAgreementInfo e : sccSouJcAgreementInfoList) {
            agreementInfoService.saveOrUpdate(e);
            List<SccSouTieredPricing> sccSouTieredPricingList = e.getSccSouTieredPricingList();
            LambdaQueryWrapper<SccSouTieredPricing> ti = new LambdaQueryWrapper<>();
            ti.eq(SccSouTieredPricing::getAgreementInfoId, e.getAgreementInfoId());
            List<SccSouTieredPricing> list = tieredPricingService.list(ti);
            if (CollectionUtils.isNotEmpty(sccSouTieredPricingList)) {
                Long agreementInfoId = e.getAgreementInfoId();
                //保存新价格
                List<Long> tpList = savePrices(sccSouTieredPricingList,agreementInfoId);
                //删除旧价格
                removeOldPrices(list,ti,tpList);
            } else {
                if (CollectionUtils.isNotEmpty(list)) {
                    tieredPricingService.remove(ti);
                }
            }
        }
    }

    private void removeOldPrices(List<SccSouTieredPricing> list,LambdaQueryWrapper<SccSouTieredPricing> ti, List<Long> tpList) {
        List<Long> pList = list.stream().map(SccSouTieredPricing::getTieredPricingId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pList) && !new HashSet<>(pList).containsAll(tpList)) {
            ti.notIn(SccSouTieredPricing::getTieredPricingId, tpList);
            tieredPricingService.remove(ti);
        }
    }

    private List<Long> savePrices(List<SccSouTieredPricing> sccSouTieredPricingList,Long agreementInfoId) {
        List<Long> tpList = new ArrayList<>();
        for (SccSouTieredPricing a : sccSouTieredPricingList) {
            if (a.getAgreementInfoId() != null) {
                tpList.add(a.getTieredPricingId());
            }
            a.setAgreementInfoId(agreementInfoId);
        }
        tieredPricingService.saveOrUpdateBatch(sccSouTieredPricingList);
        return tpList;
    }

    private void removeAgreementInfo(List<Long> infoIdList,SccSouJcAgreement sccSouJcAgreement){
        LambdaQueryWrapper<SccSouJcAgreementInfo> infoQuery = new LambdaQueryWrapper<>();
        infoQuery.eq(SccSouJcAgreementInfo::getAgreementId, sccSouJcAgreement.getAgreementId());
        List<SccSouJcAgreementInfo> infoList = agreementInfoService.list(infoQuery);
        List<Long> infoIds = infoList.stream().map(SccSouJcAgreementInfo::getAgreementInfoId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(infoIds) && !new HashSet<>(infoIds).containsAll(infoIdList)) {
            infoQuery.notIn(SccSouJcAgreementInfo::getAgreementInfoId, infoIdList);
            agreementInfoService.remove(infoQuery);
            List<Long> priIds = infoIds.stream().filter(element -> !infoIdList.contains(element)).collect(Collectors.toList());
            LambdaQueryWrapper<SccSouTieredPricing> ti = new LambdaQueryWrapper<>();
            ti.in(SccSouTieredPricing::getAgreementInfoId, priIds);
            tieredPricingService.remove(ti);
        }
    }

}
