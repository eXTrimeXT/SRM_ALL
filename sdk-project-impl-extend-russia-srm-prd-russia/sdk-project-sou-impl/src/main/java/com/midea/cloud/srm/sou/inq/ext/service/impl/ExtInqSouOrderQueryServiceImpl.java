package com.midea.cloud.srm.sou.inq.ext.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqOrderItemHisQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqOrderItemHisQueryVO;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderDetailVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouItemRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderMapper;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouOrderQueryService;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.control.service.SouControlEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemLadderDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderFileDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemLadderDAO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouOrderQueryServiceImpl implements ExtInqSouOrderQueryService {

    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderDAO souOrderDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private SouOrderItemLadderDAO souOrderItemLadderDAO;
    @Autowired
    private SouInitQueryService souInitQueryService;
    @Autowired
    private ExtPjInqSouOrderDAO extPjInqSouOrderDAO;
    @Autowired
    private SouOrderFileDAO souOrderFileDAO;
    @Autowired
    private SouItemLadderDAO souItemLadderDAO;
    @Autowired
    private ExtPjInqSouOrderMapper extPjInqSouOrderMapper;
    @Autowired
    private ExtPjInqSouItemRoundDAO extPjInqSouItemRoundDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private BaseClient baseClient;

    /**
     * 获取上一轮报价
     */
    @Override
    public List<ApiInqSouOrderItemVO> getLastOrderItems(long projectId, long vendorId) {
        // 1: 查询询价单信息
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, "询价单[{0}]不存在", projectId);
        AssertUtils.isTrue(SouTypeEnum.inq.name().equals(souProject.getSouType()), "非询比价单据");
        if (souProject.getCurrentRound() == null || souProject.getCurrentRound() <= 1) { return Collections.emptyList(); }
        // 2: 查询供应商在小于当前轮次的所有有效的报价明细
        Map<Long/* orderItemId */, SouOrderItem> orderItemMap = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, projectId)
                .eq(SouOrderItem::getVendorId, vendorId)
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .lt(SouOrderItem::getRound, souProject.getCurrentRound())
                .list().stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
        List<InqSouOrderItem> inqOrderItemList = inqSouOrderItemDAO.listByIds(orderItemMap.keySet());
        // 4: 查询物料需求信息
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.list(SouItem::getProjectId, projectId).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.list(InqSouItem::getProjectId, projectId).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        // 5: 查询阶梯报价
        Map<Long/* orderItemId */, List<SouOrderItemLadder>> orderItemLadderMap = souOrderItemLadderDAO.lambdaQuery()
                .in(SouOrderItemLadder::getOrderItemId, orderItemMap.keySet())
                .orderByAsc(SouOrderItemLadder::getSortIndex)
                .list().stream().collect(Collectors.groupingBy(SouOrderItemLadder::getOrderItemId));
        // 6: 组装数据
        List<ApiInqSouOrderItemVO> voList = new ArrayList<>(inqOrderItemList.size());
        for (InqSouOrderItem inqOrderItem : inqOrderItemList) {
            ApiInqSouOrderItemVO vo = new ApiInqSouOrderItemVO();
            voList.add(vo);

            SouOrderItem orderItem = orderItemMap.get(inqOrderItem.getOrderItemId());
            SouObjectXUtil.mergeProperties(orderItem, vo);

            SouObjectXUtil.mergeProperties(inqOrderItem, vo);

            SouItem souItem = souItemMap.get(vo.getSouItemId());
            SouObjectXUtil.mergeProperties(souItem, vo);

            InqSouItem inqSouItem = inqSouItemMap.get(vo.getSouItemId());
            SouObjectXUtil.mergeProperties(inqSouItem, vo);

            vo.setLadderPriceList(orderItemLadderMap.get(vo.getOrderItemId()));
        }

        return voList;
    }

    /**
     * 查询指定轮次报价信息
     */
    @Override
    public ApiInqSouOrderDetailVO getSouOrderInfo(long projectId, long vendorId, @Nullable Integer round) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(projectId);
        // 1: 查询立项信息
        ApiInqSouInitDetailVO inqSouInitInfo; {
            ApiSouInitDetailVO souInitInfo = souInitQueryService.getSouInitInfo(projectId, SouTypeEnum.inq.name());
            souInitInfo.doVendorView(vendorId);
            inqSouInitInfo = SouObjectXUtil.convertTargetObj(souInitInfo, ApiInqSouInitDetailVO.class);
        }
        round = round != null ? round : inqSouInitInfo.getProjectInfo().getCurrentRound();
        // 2: 查询指定轮次生效的物料信息
        Set<Long> availableSouItemIds = extPjInqSouItemRoundDAO.lambdaQuery()
                .eq(ExtPjInqSouItemRound::getProjectId, projectId)
                .eq(ExtPjInqSouItemRound::getRound, round)
                .eq(ExtPjInqSouItemRound::getCanOrder, Enable.Y)
                .list().stream().map(ExtPjInqSouItemRound::getSouItemId).collect(Collectors.toSet());
        // 3: 查询本轮报价单
        SouOrder souOrder = souOrderDAO.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getVendorId, vendorId)
                .eq(SouOrder::getRound, round)
                .one();
        ExtPjInqSouOrder inqSouOrder = null; {
            if (souOrder != null) {
                inqSouOrder = extPjInqSouOrderDAO.getById(souOrder.getOrderId());
            }
        }
        // 4: 查询本轮报价明细
        List<SouOrderItem> orderItemList = Collections.emptyList(); {
            if (souOrder != null) {
                orderItemList = souOrderItemDAO.list(SouOrderItem::getOrderId, souOrder.getOrderId());
            }
        }
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = Collections.emptyMap(); {
            if (souOrder != null) {
                inqOrderItemMap = inqSouOrderItemDAO.list(InqSouOrderItem::getOrderId, souOrder.getOrderId())
                        .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
            }
        }
        // 5: 查询物料需求信息
        List<SouItem> souItemList = SouActiveBeanUtils.getActiveBean(SouTypeEnum.inq.name(), ApiSouOrderQueryHandler.class)
                .getAvailableItemsForVendor(projectId, round, vendorId);
        Map<Long, SouItem> souItemMap = souItemList.stream().collect(Collectors.toMap(s -> s.getSouItemId(), Function.identity(), (k1, k2) -> k2));

        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(souItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        // 6: 查询阶梯报价
        Map<Long/* orderItemId */, List<SouOrderItemLadder>> orderItemLadderMap = Collections.emptyMap(); {
            if (!inqOrderItemMap.isEmpty()) {
                orderItemLadderMap = souOrderItemLadderDAO.lambdaQuery()
                        .in(SouOrderItemLadder::getOrderItemId, inqOrderItemMap.keySet())
                        .orderByAsc(SouOrderItemLadder::getSortIndex)
                        .list().stream().collect(Collectors.groupingBy(SouOrderItemLadder::getOrderItemId));
            }
        }
        // 7: 查询附件
        List<SouOrderFile> orderFileList; {
            if (souOrder != null) {
                orderFileList = souOrderFileDAO.lambdaQuery()
                        .eq(SouOrderFile::getOrderId, souOrder.getOrderId())
                        .list();
            } else {
                orderFileList = Collections.emptyList();
            }
        }
        // 8: 查询物料需求附件
        Map<Long/* souItemId */, List<SceneFile>> sceneFileMap; {
            List<SceneFile> sceneFileList = baseClient.listSceneFileBatch(new ArrayList<>(inqSouItemMap.keySet()));
            if (sceneFileList.isEmpty()) {
                sceneFileMap = Collections.emptyMap();
            }
            else {
                sceneFileMap = sceneFileList.stream().collect(Collectors.groupingBy(SceneFile::getBusinessId));
            }
        }
        // 9: 查询阶梯价模板信息
        Map<Long/* souItemId */, List<SouItemLadder>> souItemLadderMap = souItemLadderDAO.list(SouItemLadder::getProjectId, projectId)
                .stream().sorted(Comparator.comparing(SouItemLadder::getSortIndex)).collect(Collectors.groupingBy(SouItemLadder::getSouItemId));
        // 10: 组装数据
        ApiInqSouOrderDetailVO vo = getApiInqSouOrderDetailVO(round, inqSouInitInfo, availableSouItemIds, souOrder, inqSouOrder, orderItemList, inqOrderItemMap, souItemList, inqSouItemMap, orderItemLadderMap, orderFileList, sceneFileMap, souItemLadderMap);

        return vo;
    }

    /**
     * 组装数据
     * @param round 参数
     * @param inqSouInitInfo 参数
     * @param availableSouItemIds 参数
     * @param souOrder 参数
     * @param inqSouOrder 参数
     * @param orderItemList 参数
     * @param inqOrderItemMap 参数
     * @param souItemList 参数
     * @param inqSouItemMap 参数
     * @param orderItemLadderMap 参数
     * @param orderFileList 参数
     * @param sceneFileMap 参数
     * @param souItemLadderMap 参数
     * @return 返回
     */
    @NotNull
    private static ApiInqSouOrderDetailVO getApiInqSouOrderDetailVO(Integer round, ApiInqSouInitDetailVO inqSouInitInfo, Set<Long> availableSouItemIds, SouOrder souOrder, ExtPjInqSouOrder inqSouOrder, List<SouOrderItem> orderItemList, Map<Long, InqSouOrderItem> inqOrderItemMap, List<SouItem> souItemList, Map<Long, InqSouItem> inqSouItemMap, Map<Long, List<SouOrderItemLadder>> orderItemLadderMap, List<SouOrderFile> orderFileList, Map<Long, List<SceneFile>> sceneFileMap, Map<Long, List<SouItemLadder>> souItemLadderMap) {
        ApiInqSouOrderDetailVO vo = new ApiInqSouOrderDetailVO();
        {
            vo.setInitInfo(inqSouInitInfo);
            vo.setOrder(souOrder);
            if (vo.getOrder() != null) {
                SouObjectXUtil.mergeProperties(inqSouOrder, vo.getOrder());
            }
            if (CollectionUtils.isNotEmpty(orderFileList)) {
                vo.setOrderFileList(SouObjectXUtil.convertList(orderFileList, ApiSouOrderFileVO.class));
            }
            vo.setItemList(SouObjectXUtil.convertList(souItemList, ApiInqSouOrderItemVO.class)); {
                // 去掉指定轮次未生效的数据
                vo.getItemList().removeIf(e -> !availableSouItemIds.contains(e.getSouItemId()));

                Map<Long/* souItemId */, SouOrderItem> orderItemMap = orderItemList.stream().collect(Collectors.toMap(SouOrderItem::getSouItemId, Function.identity()));
                for (ApiInqSouOrderItemVO orderItem : vo.getItemList()) {
                    SouOrderItem oi = orderItemMap.get(orderItem.getSouItemId());
                    if (oi != null) {
                        SouObjectXUtil.mergeProperties(oi, orderItem);
                    }

                    if (oi != null) {
                        InqSouOrderItem ioi = inqOrderItemMap.get(oi.getOrderItemId());
                        if (ioi != null) {
                            SouObjectXUtil.mergeProperties(ioi, orderItem);
                        }
                    }

                    InqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                    SouObjectXUtil.mergeProperties(inqSouItem, orderItem);

                    List<SouOrderItemLadder> orderLadderList = orderItemLadderMap.get(orderItem.getOrderItemId());
                    if (CollectionUtils.isNotEmpty(orderLadderList)) {
                        orderItem.setLadderPriceList(orderLadderList);
                    } else {
                        List<SouItemLadder> souLadderList = souItemLadderMap.get(orderItem.getSouItemId());
                        if (CollectionUtils.isNotEmpty(souLadderList)) {
                            orderItem.setLadderPriceList(SouObjectXUtil.convertList(souLadderList, SouOrderItemLadder.class));
                        }
                    }

                    orderItem.setRound(round);
                    if (orderItem.getAdvancePaymentRemark() == null) {
                        orderItem.setAdvancePaymentRemark(Enable.N);
                    }

                    orderItem.setItemFiles(sceneFileMap.get(orderItem.getSouItemId()));
                }
            }
        }
        return vo;
    }

    /**
     * 供应商历史报价列表查询
     */
    @Override
    public List<ExtInqOrderItemHisQueryVO> listVendorOrderHis(ExtInqOrderItemHisQueryDTO queryParam) {
        queryParam.formatParams();
        // 1: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<ExtInqOrderItemHisQueryVO> voList = SouObjectXUtil.convertList(extPjInqSouOrderMapper.listVendorOrderHis(queryParam), ExtInqOrderItemHisQueryVO.class);
        if (voList.isEmpty()) { return voList; }
        // 2: 查询额外信息
        // 2.1: 查询询比价报价明细信息
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = inqSouOrderItemDAO.listByIds(voList.stream().map(ExtInqOrderItemHisQueryVO::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqOrderItemMap.get(vo.getOrderItemId()), vo));
        // 2.2: 查询物料需求信息
        Set<Long> souItemIds = voList.stream().map(ExtInqOrderItemHisQueryVO::getSouItemId).collect(Collectors.toSet());
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.listByIds(souItemIds).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(souItemMap.get(vo.getSouItemId()), vo));
        // 2.3: 查询询比价物料需求信息
        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(souItemIds).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqSouItemMap.get(vo.getSouItemId()), vo));
        // 2.4: 查询询比价报价单信息
        Map<Long/* orderId */, ExtPjInqSouOrder> inqOrderMap = extPjInqSouOrderDAO.listByIds(voList.stream().map(ExtInqOrderItemHisQueryVO::getOrderId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPjInqSouOrder::getOrderId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqOrderMap.get(vo.getOrderId()), vo));
        // 2.5: 查询询价单信息
        Map<Long/* projectId */, SouProject> souProjectMap = souProjectDAO.listByIds(voList.stream().map(ExtInqOrderItemHisQueryVO::getProjectId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouProject::getProjectId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(souProjectMap.get(vo.getProjectId()), vo));
        // 2.6: 查询报价单
        Map<Long/* orderId */, SouOrder> orderMap = souOrderDAO.listByIds(inqOrderMap.keySet()).stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        voList.forEach(vo -> SouObjectXUtil.mergeProperties(orderMap.get(vo.getOrderId()), vo));
        // 2.7: 查询供应商
        Map<String/* projectId_vendorId */, SouVendor> vendorMap = souVendorDAO.lambdaQuery()
                .in(SouVendor::getProjectId, souProjectMap.keySet())
                .list().stream().collect(Collectors.toMap(e -> e.getProjectId() + "_" + e.getVendorId(), Function.identity()));
        voList.forEach(vo -> {
            SouVendor vendor = vendorMap.get(vo.getProjectId() + "_" + vo.getVendorId());
            if (vendor != null) {
                vo.setVendorCode(vendor.getVendorCode());
                vo.setVendorName(vendor.getVendorName());
            }
        });

        return voList;
    }

}
