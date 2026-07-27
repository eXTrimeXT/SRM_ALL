package com.midea.cloud.srm.sou.purinq.service.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.*;
import com.midea.cloud.srm.model.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouSelectEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 100014337
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouSelectEventServiceImpl implements ExtPurInqSouSelectEventService {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private SouOrderDAO souOrderDAO;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private ExtPurInqSouVendorRoundDAO extPurInqSouVendorRoundDAO;
    @Autowired
    private ExtPurInqSouItemRoundDAO extPurInqSouItemRoundDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;

    /**
     * 总价比价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void totalPriceCompare(long projectId) {
        // 1: 校验操作条件/权限
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, "询价单[{0}]不存在", projectId);
        AssertUtils.isFalse(new Date().before(souProject.getOrderEndTime()), "未截止报价，不能进行总价比价");
        Set<Long> needOrderVendorIds = extPurInqSouVendorRoundDAO.lambdaQuery()
                .eq(ExtPurInqSouVendorRound::getProjectId, projectId)
                .eq(ExtPurInqSouVendorRound::getRound, souProject.getCurrentRound())
                .eq(ExtPurInqSouVendorRound::getCanOrder, Enable.Y)
                .list().stream().map(ExtPurInqSouVendorRound::getVendorId).collect(Collectors.toSet());
        // 2: 查询数据
        List<SouItem> souItemList; {
            Set<Long> availableSouItemIds = extPurInqSouItemRoundDAO.lambdaQuery()
                    .eq(ExtPurInqSouItemRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPurInqSouItemRound::getRound, souProject.getCurrentRound())
                    .list().stream().map(ExtPurInqSouItemRound::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemDAO.list(SouItem::getProjectId, projectId)
                    .stream().filter(e -> availableSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
        }
        AssertUtils.notEmpty(souItemList, "当前轮次物料均已关闭，无法进行总价比价");
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, projectId)
                .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .in(SouOrderItem::getSouItemId, souItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                .list();
        {// 判断确保本轮次应报价供应商均报价了
            Set<Long> orderedVendorIds = orderItemList.stream().map(SouOrderItem::getVendorId).collect(Collectors.toSet());
            needOrderVendorIds.forEach(vendorId -> {
                if (!orderedVendorIds.contains(vendorId)) {
                    SouVendor vendor = souVendorDAO.lambdaQuery()
                            .eq(SouVendor::getProjectId, projectId)
                            .eq(SouVendor::getVendorId, vendorId)
                            .one();
                    throw new IllegalArgumentException(MessageFormat.format("供应商[{0}]未报价，不能进行总价比价", vendor.getVendorName()));
                }
            });
        }
        Map<Long/* vendorId */, SouOrder> orderMap; {
            if (!orderItemList.isEmpty()) {
                orderMap = souOrderDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(SouOrder::getVendorId, Function.identity()));
            } else {
                orderMap = Collections.emptyMap();
            }
        }
        Map<Long/* vendorId */, List<SouOrderItem>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(SouOrderItem::getVendorId));
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap; {
            if (!orderItemList.isEmpty()) {
                inqOrderItemMap = extPurInqSouOrderItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
            } else {
                inqOrderItemMap = Collections.emptyMap();
            }
        }
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, projectId).stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 3: 确保供应商对所有物料均提交的报价（否则不支持总价比价）
        Map<Long/* vendorId */, BigDecimal> vendorTotalPriceMap = new HashMap<>(vendorMap.size());
        orderItemMap.forEach((vendorId, oiList) -> {
            BigDecimal totalPrice = BigDecimal.ZERO;
            Map<Long/* souItemId */, SouOrderItem> oiMap = oiList.stream().collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
            for (SouItem souItem : souItemList) {
                SouOrderItem orderItem = oiMap.get(souItem.getSouItemId());
                if (orderItem == null) {
                    throw new IllegalArgumentException(MessageFormat.format("供应商[{0}]未对物料[{1}]进行报价，不能总价比价", vendorMap.get(vendorId).getVendorName(), souItem.getItemDesc()));
                }
                ExtPurInqSouOrderItem inqOrderItem = inqOrderItemMap.get(orderItem.getOrderItemId());
                totalPrice = totalPrice.add(inqOrderItem.getPriceTaxTotal());
            }
            vendorTotalPriceMap.put(vendorId, totalPrice);
        });
        // 4: 排序
        List<Long> vendorRankList = new ArrayList<>(vendorTotalPriceMap.keySet());
        vendorRankList.sort((a, b) -> {
            BigDecimal aPrice = vendorTotalPriceMap.get(a);
            BigDecimal bPrice = vendorTotalPriceMap.get(b);
            int value = aPrice.compareTo(bPrice);
            if (value != 0) { return value; }
            // 价格相同，比较报价时间
            SouOrder aOrder = orderMap.get(a);
            SouOrder bOrder = orderMap.get(b);
            if (aOrder.getSubmitTime().before(bOrder.getSubmitTime())) {
                return -1;
            } else if (aOrder.getSubmitTime().after(bOrder.getSubmitTime())) {
                return 1;
            } else {
                return 0;
            }
        });
        // 5: 更新报价明细的排名何入围/淘汰状态
        List<SouOrderItem> needUpdateOrderItemList = new ArrayList<>(orderItemMap.size() << 3);
        for (int i = 0; i < vendorRankList.size(); i++) {
            long vendorId = vendorRankList.get(i);

            List<SouOrderItem> oiList = orderItemMap.get(vendorId);
            needUpdateOrderItemList.addAll(oiList);
            for (SouOrderItem orderItem : oiList) {
                orderItem.setRanking(i + 1);
                orderItem.setWinStatus(i <= 0 ? SouWinStatusEnum.Y : SouWinStatusEnum.N);
            }
        }
        if (!needUpdateOrderItemList.isEmpty()) {
            souOrderItemDAO.updateBatchById(needUpdateOrderItemList);
        }
    }

    /**
     * 结束询价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void finishSou(long projectId) {
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, "询价单[{0}]不存在", projectId);
        AssertUtils.isFalse(new Date().before(souProject.getOrderEndTime()), "询价未截止");

        souProjectDAO.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.PRICE_END)
                .eq(SouProject::getProjectId, projectId)
                .update();
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, InqSouProjectStatusEnum.PRICE_END)
                .eq(ExtPurInqSouProject::getProjectId, projectId)
                .update();
        // 将所有物料的最新轮次报价的入围----->标记为中标
        Set<Long> latestOrderItemIds; {
            Set<Long> tempOrderItemIds = souOrderItemDAO.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, projectId)
                    .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.Y)
                    .select(SouOrderItem::getOrderItemId)
                    .list().stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
            if (tempOrderItemIds.isEmpty()) {
                latestOrderItemIds = Collections.emptySet();
            } else {
                latestOrderItemIds = extPurInqSouOrderItemDAO.lambdaQuery()
                        .in(ExtPurInqSouOrderItem::getOrderItemId, tempOrderItemIds)
                        .eq(ExtPurInqSouOrderItem::getLatestPriceTag, Enable.Y)
                        .list().stream().map(ExtPurInqSouOrderItem::getOrderItemId).collect(Collectors.toSet());
            }
        }
        if (!latestOrderItemIds.isEmpty()) {
            souOrderItemDAO.lambdaUpdate()
                    .set(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                    .in(SouOrderItem::getOrderItemId, latestOrderItemIds)
                    .update();
        }
    }

}
