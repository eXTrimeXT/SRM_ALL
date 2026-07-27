package com.midea.cloud.srm.sou.inq.ext.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqCloseItemParams;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouVendorRound;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSelectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceQueryService;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouItemRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouVendorRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouSelectEventService;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouRoundService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
 * 备注
 * @author huangbf3
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouSelectEventServiceImpl implements ExtInqSouSelectEventService {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private SouOrderDAO souOrderDAO;
    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;
    @Autowired
    private ExtPjInqSouVendorRoundDAO extPjInqSouVendorRoundDAO;
    @Autowired
    private ExtPjInqSouItemRoundDAO extPjInqSouItemRoundDAO;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private IExtSouRoundService souRoundService;
    @Autowired
    private ExtFixPriceQueryService extFixPriceQueryService;

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
        Set<Long> needOrderVendorIds = extPjInqSouVendorRoundDAO.lambdaQuery()
                .eq(ExtPjInqSouVendorRound::getProjectId, projectId)
                .eq(ExtPjInqSouVendorRound::getRound, souProject.getCurrentRound())
                .eq(ExtPjInqSouVendorRound::getCanOrder, Enable.Y)
                .list().stream().map(ExtPjInqSouVendorRound::getVendorId).collect(Collectors.toSet());
        // 2: 查询数据
        List<SouItem> souItemList; {
            Set<Long> availableSouItemIds = extPjInqSouItemRoundDAO.lambdaQuery()
                    .eq(ExtPjInqSouItemRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPjInqSouItemRound::getRound, souProject.getCurrentRound())
                    .list().stream().map(ExtPjInqSouItemRound::getSouItemId).collect(Collectors.toSet());
            List<SouItem> souItemList2 = souItemDAO.list(SouItem::getProjectId, projectId)
                    .stream().filter(e -> availableSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
            // 排除被关闭的物料
            Set<Long> closedSouItemIds = inqSouItemDAO.listByIds(souItemList2.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                    .stream().filter(e -> Enable.Y.equals(e.getHasClose()))
                    .map(InqSouItem::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemList2.stream().filter(e -> !closedSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
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
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap; {
            if (!orderItemList.isEmpty()) {
                inqOrderItemMap = inqSouOrderItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
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
                InqSouOrderItem inqOrderItem = inqOrderItemMap.get(orderItem.getOrderItemId());
                totalPrice = totalPrice.add(inqOrderItem.getPriceTaxTotal());
            }
            vendorTotalPriceMap.put(vendorId, totalPrice);
        });
        List<SouOrderItem> needUpdateOrderItemList = getSouOrderItems(orderMap, orderItemMap, vendorTotalPriceMap);
        if (!needUpdateOrderItemList.isEmpty()) {
            souOrderItemDAO.updateBatchById(needUpdateOrderItemList);
        }

        // 更新总价比价标识
        souRoundService.lambdaUpdate()
                .eq(ExtSouRound::getProjectId, souProject.getProjectId())
                .eq(ExtSouRound::getRound, souProject.getCurrentRound())
                .set(ExtSouRound::getExtTotalCompare, YesOrNo.YES.getValue())
                .update();
    }

    /**
     * 拆分
     * @param orderMap 参数
     * @param orderItemMap 参数
     * @param vendorTotalPriceMap 参数
     * @return 返回
     */
    @NotNull
    private static List<SouOrderItem> getSouOrderItems(Map<Long, SouOrder> orderMap, Map<Long, List<SouOrderItem>> orderItemMap, Map<Long, BigDecimal> vendorTotalPriceMap) {
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
        return needUpdateOrderItemList;
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
        inqSouProjectDAO.lambdaUpdate()
                .set(InqSouProject::getExtProjectStatus, InqSouProjectStatusEnum.PRICE_END)
                .eq(InqSouProject::getProjectId, projectId)
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
                latestOrderItemIds = inqSouOrderItemDAO.lambdaQuery()
                        .in(InqSouOrderItem::getOrderItemId, tempOrderItemIds)
                        .eq(InqSouOrderItem::getLatestPriceTag, Enable.Y)
                        .list().stream().map(InqSouOrderItem::getOrderItemId).collect(Collectors.toSet());
            }
        }
        if (!latestOrderItemIds.isEmpty()) {
            souOrderItemDAO.lambdaUpdate()
                    .set(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                    .in(SouOrderItem::getOrderItemId, latestOrderItemIds)
                    .update();
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void draftCloseSouItems(Set<Long> souItemIds) {
        //删除scc_sou_item 表对应记录
        souItemDAO.removeByIds(souItemIds);
        closeSouItems(souItemIds);
    }

    /**
     * 关闭物料需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void closeSouItems(Set<Long> souItemIds) {
        List<InqSouItem> inqSouItemList = inqSouItemDAO.listByIds(souItemIds)
                .stream().filter(e -> Enable.N.equals(e.getHasClose())).collect(Collectors.toList());
        if (inqSouItemList.isEmpty()) { return; }
        //检查定价单明细是否已存在
        checkFixPriceLines(inqSouItemList);
        inqSouItemDAO.lambdaUpdate()
                .set(InqSouItem::getHasClose, Enable.Y)
                .in(InqSouItem::getSouItemId, inqSouItemList.stream().map(InqSouItem::getSouItemId).collect(Collectors.toSet()))
                .update();
        // 将上游需求池那边标记为未询价
        Set<Long> reqLineIds = new HashSet<>(16);
        for (InqSouItem souItem : inqSouItemList) {
            if (StringUtils.isNotBlank(souItem.getExtSourceFromLineIds())) {
                String[] arr = souItem.getExtSourceFromLineIds().split(",");
                for (String s : arr) {
                    reqLineIds.add(Long.valueOf(s));
                }
            }
        }
        if (!reqLineIds.isEmpty()) {
            qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                    .set("ifCreateInq", Enable.N)
                    .in(RequirementLine::getRequirementLineId, new ArrayList<>(reqLineIds)));
        }
    }

    private void checkFixPriceLines(List<InqSouItem> inqSouItemList) {
        List<SouItem> souItemList = souItemDAO.lambdaQuery().in(
                SouItem::getSouItemId,
                inqSouItemList.stream().map(InqSouItem::getSouItemId).collect(Collectors.toList())).list();
        if(CollUtil.isNotEmpty(extFixPriceQueryService.queryLines(souItemList))){
            throw new BaseException("定价单明细已存在，不能操作");
        }
    }

    @Override
    public void closeSouItems(ExtInqCloseItemParams params) {
        List<InqSouItem> inqSouItemList = inqSouItemDAO.listByIds(Collections.singletonList(params.getSouItemId()))
                .stream().filter(e -> Enable.N.equals(e.getHasClose())).collect(Collectors.toList());
        if (inqSouItemList.isEmpty()) { return; }
        //检查定价单明细是否已存在
        checkFixPriceLines(inqSouItemList);
        inqSouItemDAO.lambdaUpdate()
                .set(InqSouItem::getHasClose, Enable.Y)
                .in(InqSouItem::getSouItemId, inqSouItemList.stream().map(InqSouItem::getSouItemId).collect(Collectors.toSet()))
                .update();
        // 将上游需求池那边标记为未询价
        Set<Long> reqLineIds = new HashSet<>(16);
        for (InqSouItem souItem : inqSouItemList) {
            if (StringUtils.isNotBlank(souItem.getExtSourceFromLineIds())) {
                String[] arr = souItem.getExtSourceFromLineIds().split(",");
                for (String s : arr) {
                    reqLineIds.add(Long.valueOf(s));
                }
            }
        }

        // 更新状态
        qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                .set("extPoolStatus", Enable.N)
                .set("extClosedCause", params.getReason())
                .in(RequirementLine::getRequirementLineId, new ArrayList<>(reqLineIds)));

    }

}
