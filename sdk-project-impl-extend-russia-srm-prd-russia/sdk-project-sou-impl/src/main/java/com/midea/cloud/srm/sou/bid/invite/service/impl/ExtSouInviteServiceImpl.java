package com.midea.cloud.srm.sou.bid.invite.service.impl;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.bid.invite.service.ExtSouInviteService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Api("供应商邀请报名更新接口实现类")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ExtSouInviteServiceImpl implements ExtSouInviteService {
    @Autowired
    private QlService qlService;

    @Override
    public void updateIsBid(ExtSouProject project, Long vendorId, String isBid) {
        Map<Long, Long> vendorHeadMap = queryVendorIdAndHeadId(Collections.singletonList(vendorId));

        //更新是否投标
        List<SouInviteItem> souInviteItemList = qlService.query(MqlType.SOU_INVITE_ITEM_BUYER, MeiQl.newCondition()
                .eq(SouInviteItem::getInviteHeadId, vendorHeadMap.get(vendorId)).eq(SouInviteItem::getProjectNo, project.getExtProjectNo()), SouInviteItem.class);
        if(CollectionUtils.isNotEmpty(souInviteItemList)) {
            souInviteItemList.stream().forEach(souInviteItem -> {
                souInviteItem.setIsBid(isBid);
                souInviteItem.setVendorId(vendorId);
            });
            qlService.update(MqlType.SOU_INVITE_ITEM_BUYER, souInviteItemList);
        }

    }

    protected Map<Long, Long> queryVendorIdAndHeadId(List<Long> vendorIdList) {
        List<SouInviteHead> inviteHeadList = qlService.query(MqlType.SOU_INVITE_HEAD_BUYER, MeiQl.newCondition().in(SouInviteHead::getVendorId, vendorIdList), SouInviteHead.class);
        if(CollectionUtils.isEmpty(inviteHeadList)) {
            return new HashMap<>(50);
        }
        return inviteHeadList.stream().collect(Collectors.toMap(k -> k.getVendorId(), v -> v.getInviteHeadId(), (k1, k2)->k2));
    }

    @Override
    public void updateIsInvalidBid(ExtSouProject project, Long vendorId, String isInvalidBid) {
        Map<Long, Long> vendorHeadMap = queryVendorIdAndHeadId(Collections.singletonList(vendorId));
        //更新是否废标
        List<SouInviteItem> souInviteItemList = qlService.query(MqlType.SOU_INVITE_ITEM_BUYER, MeiQl.newCondition()
                .eq(SouInviteItem::getInviteHeadId, vendorHeadMap.get(vendorId)).eq(SouInviteItem::getProjectNo, project.getExtProjectNo()), SouInviteItem.class);
        if(CollectionUtils.isNotEmpty(souInviteItemList)) {
            souInviteItemList.stream().forEach(souInviteItem -> {
                souInviteItem.setIsInvalidBid(isInvalidBid);
                souInviteItem.setVendorId(vendorId);
            });
            qlService.update(MqlType.SOU_INVITE_ITEM_BUYER, souInviteItemList);
        }
    }

    @Override
    public void updateIsSuccBidBatch(ExtSouProject project, List<SouInviteItem> souInviteItems) {
        if(CollectionUtils.isEmpty(souInviteItems)) {
            return;
        }
        Map<Long, Long> vendorHeadMap = queryVendorIdAndHeadId(souInviteItems.stream().map(v->v.getVendorId()).distinct().collect(Collectors.toList()));
        Map<Long, Long> headVendorMap = new HashMap<>(50);
        for(Long key : vendorHeadMap.keySet()) {
            headVendorMap.put(vendorHeadMap.get(key), key);
        }

        List<SouInviteItem> souInviteItemList = qlService.query(MqlType.SOU_INVITE_ITEM_BUYER, MeiQl.newCondition()
                .eq(SouInviteItem::getProjectNo, project.getExtProjectNo())
                .in(SouInviteItem::getInviteHeadId, new ArrayList<>(vendorHeadMap.values())), SouInviteItem.class);
        souInviteItemList.stream().forEach(souInviteItem -> {
            souInviteItem.setVendorId(headVendorMap.get(souInviteItem.getInviteHeadId()));
        });

        Map<Long, SouInviteItem> itemMap = souInviteItems.stream().collect(Collectors.toMap(k -> k.getVendorId(), Function.identity(), (k1, k2) -> k2));

        souInviteItemList.stream().forEach(souInviteItem -> {
            souInviteItem.setIsSuccBid(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getIsSuccBid());
        });

        qlService.update(MqlType.SOU_INVITE_ITEM_BUYER, souInviteItemList);

    }

    @Override
    public void updateNotParticipatingReasonBatch(ExtSouProject project, List<SouInviteItem> souInviteItems) {
        if(CollectionUtils.isEmpty(souInviteItems)) {
            return;
        }

        Map<Long, Long> vendorHeadMap = queryVendorIdAndHeadId(souInviteItems.stream().map(v->v.getVendorId()).distinct().collect(Collectors.toList()));
        Map<Long, Long> headVendorMap = new HashMap<>(50);
        for(Long key : vendorHeadMap.keySet()) {
            headVendorMap.put(vendorHeadMap.get(key), key);
        }

        List<SouInviteItem> souInviteItemList = qlService.query(MqlType.SOU_INVITE_ITEM_BUYER, MeiQl.newCondition()
                .eq(SouInviteItem::getProjectNo, project.getExtProjectNo())
                .in(SouInviteItem::getInviteHeadId, new ArrayList<>(vendorHeadMap.values())), SouInviteItem.class);
        souInviteItemList.stream().forEach(souInviteItem -> {
            souInviteItem.setVendorId(headVendorMap.get(souInviteItem.getInviteHeadId()));
        });

        Map<Long, SouInviteItem> itemMap = souInviteItems.stream().collect(Collectors.toMap(k -> k.getVendorId(), Function.identity(), (k1, k2) -> k2));

        souInviteItemList.stream().forEach(souInviteItem -> {
            souInviteItem.setNotParticipatingReason(ObjectUtils.defaultIfNull(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getNotParticipatingReason(), ""));
        });

        qlService.update(MqlType.SOU_INVITE_ITEM_BUYER, souInviteItemList);
    }

    @Override
    public void updateScoreBatch(ExtSouProject project, List<SouInviteItem> souInviteItems) {
        if(CollectionUtils.isEmpty(souInviteItems)) {
            return;
        }
        Map<Long, Long> vendorHeadMap = queryVendorIdAndHeadId(souInviteItems.stream().map(v->v.getVendorId()).distinct().collect(Collectors.toList()));
        Map<Long, Long> headVendorMap = new HashMap<>(50);
        for(Long key : vendorHeadMap.keySet()) {
            headVendorMap.put(vendorHeadMap.get(key), key);
        }

        List<SouInviteItem> souInviteItemList = qlService.query(MqlType.SOU_INVITE_ITEM_BUYER, MeiQl.newCondition()
                .eq(SouInviteItem::getProjectNo, project.getExtProjectNo())
                .in(SouInviteItem::getInviteHeadId, new ArrayList<>(vendorHeadMap.values())), SouInviteItem.class);
        souInviteItemList.stream().forEach(souInviteItem -> {
            souInviteItem.setVendorId(headVendorMap.get(souInviteItem.getInviteHeadId()));
        });

        Map<Long, SouInviteItem> itemMap = souInviteItems.stream().collect(Collectors.toMap(k -> k.getVendorId(), Function.identity(), (k1, k2) -> k2));

        souInviteItemList.stream().forEach(souInviteItem -> {

            //     * 技术得分
            if(ObjectUtils.allNotNull(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getTechScore())) {
                souInviteItem.setTechScore(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getTechScore());
            }

            //     * 综合得分
            if(ObjectUtils.allNotNull(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getTotalScore())) {
                souInviteItem.setTotalScore(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getTotalScore());
            }
            //     * 绩效得分
            if(ObjectUtils.allNotNull(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getPerformanceScore())) {
                souInviteItem.setPerformanceScore(itemMap.getOrDefault(souInviteItem.getVendorId(), new SouInviteItem()).getPerformanceScore());
            }

        });

        qlService.update(MqlType.SOU_INVITE_ITEM_BUYER, souInviteItemList);
    }
}
