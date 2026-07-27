package com.midea.cloud.srm.sou.inq.ext.plugin.query.order;

import com.github.pagehelper.PageHelper;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouOrderQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouVendorRound;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouItemRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderMapper;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouVendorRoundDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import com.midea.cloud.srm.sou.inq.spi.order.InqSouOrderQueryHandler;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouOrderQueryHandler extends InqSouOrderQueryHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private ExtPjInqSouItemRoundDAO extPjInqSouItemRoundDAO;
    @Autowired
    private ExtPjInqSouVendorRoundDAO extPjInqSouVendorRoundDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private ExtPjInqSouOrderMapper extPjInqSouOrderMapper;
    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;

    @Override
    @ApiOperation("查询寻源单指定轮次中哪些供应商具有报价的权限")
    public Set<Long/* vendorId */> getAuthedVendors(long projectId, @Nullable Integer round) {
        SouProject souProject = souProjectDAO.getById(projectId);
        if (round == null) { round = souProject.getCurrentRound(); }

        // 长城询比价禁止公开选项（暂时没有，而且公开场景会导致判断分支逻辑更加复杂，目前不宜考虑）
        AssertUtils.isFalse(SouPublishScopeEnum.OPEN_TENDER.equals(souProject.getPublishScope()), "长城询比价禁止公开场景");

        return extPjInqSouVendorRoundDAO.lambdaQuery()
                .eq(ExtPjInqSouVendorRound::getProjectId, projectId)
                .eq(ExtPjInqSouVendorRound::getRound, round)
                .eq(ExtPjInqSouVendorRound::getCanOrder, Enable.Y)
                .select(ExtPjInqSouVendorRound::getVendorId)
                .list().stream().map(ExtPjInqSouVendorRound::getVendorId).collect(Collectors.toSet());
    }

    @Override
    @ApiOperation("查询报价单列表后的额外处理")
    public List<ApiSouOrderQueryVO> doHandlerForListOrders(ApiSouOrderQueryDTO queryParam, String souType) {
        ExtInqSouOrderQueryDTO inqQueryParam = SouObjectXUtil.convertTargetObj(queryParam, ExtInqSouOrderQueryDTO.class);
        queryParam.formatParams();
        // 1: 查询数据
        if (inqQueryParam.getPageNum() != null || inqQueryParam.getPageSize() != null) {
            PageHelper.startPage(inqQueryParam.getPageNum(), inqQueryParam.getPageSize());
        }
        List<ApiSouOrderQueryVO> voList = extPjInqSouOrderMapper.listOrders(inqQueryParam, souType);
        // 2: 查询额外信息
        if (!voList.isEmpty()) {
            Map<Long/* projectId */, InqSouProject> inqProjectMap = inqSouProjectDAO.listByIds(voList.stream().map(ApiSouOrderQueryVO::getProjectId).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toMap(InqSouProject::getProjectId, Function.identity()));
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(inqProjectMap.get(vo.getProjectId()), vo));
        }

        return voList;
    }

    @Override
    @ApiOperation("报价单列表查询后的额外字段处理")
    protected void orderQueryDataProcessing(long vendorId, List<ApiSouOrderQueryVO> voList) {
        Set<Long> projectIds = voList.stream().map(ApiSouOrderQueryVO::getProjectId).collect(Collectors.toSet());
        if (!projectIds.isEmpty()) {
            Map<Long/* projectId */, List<SouRound>> roundMap = souRoundDAO.lambdaQuery()
                    .in(SouRound::getProjectId, projectIds)
                    .list()
                    .stream().collect(Collectors.groupingBy(SouRound::getProjectId));
            Map<Long/* projectId */, Map<Integer/* round */, ExtPjInqSouVendorRound>> vendorRoundMap = extPjInqSouVendorRoundDAO.lambdaQuery()
                    .in(ExtPjInqSouVendorRound::getProjectId, projectIds)
                    .eq(ExtPjInqSouVendorRound::getVendorId, vendorId)
                    .eq(ExtPjInqSouVendorRound::getCanOrder, Enable.Y)
                    .orderByDesc(ExtPjInqSouVendorRound::getRound)
                    .list().stream().collect(Collectors.groupingBy(ExtPjInqSouVendorRound::getProjectId, Collectors.toMap(ExtPjInqSouVendorRound::getRound, Function.identity())));
            Date now = new Date();
            voList.forEach(vo -> {
                Map<Integer/* round */, ExtPjInqSouVendorRound> vendorRoundMp = vendorRoundMap.get(vo.getProjectId());
                if (vendorRoundMp == null || vendorRoundMp.isEmpty()) {
                    vo.setCanOrder(Enable.N);
                } else {
                    ExtPjInqSouVendorRound vr = vendorRoundMp.get(vo.getCurrentRound());
                    vo.setCanOrder(vr != null && Enable.Y.equals(vr.getCanOrder()) ? Enable.Y : Enable.N);
                }
                if (Enable.Y.equals(vo.getCanOrder())) {
                    // 进一步判断是否符合报价时间
                    List<SouRound> roundList = roundMap.get(vo.getProjectId());
                    roundList.sort(Comparator.comparing(SouRound::getRound).reversed());
                    SouRound currentRound = roundList.get(0);
                    if (currentRound.getOrderStartTime().after(now) || currentRound.getOrderEndTime().before(now)) {
                        vo.setCanOrder(Enable.N);
                    }
                }
                // 如果为空则说明还未提交报价，将报价状态设置为默认值
                if (vo.getOrderStatus() == null) {
                    vo.setOrderStatus(SouOrderStatusEnum.DRAFT);
                }
            });
        } else {
            voList.forEach(vo -> {
                SouOrderStatusEnum orderStatus = vo.getOrderStatus();
                if (SouOrderStatusEnum.DRAFT.equals(orderStatus) || SouOrderStatusEnum.WITHDRAW.equals(orderStatus)) {
                    vo.setCanOrder(Enable.Y);
                } else {
                    vo.setCanOrder(Enable.N);
                }
            });
        }
    }

    @ApiOperation("查询指定供应商在指定轮次的可报价物料集合")
    @Override
    public List<SouItem> getAvailableItemsForVendor(long projectId, @Nullable Integer round, long vendorId) {
        SouProject souProject = souProjectDAO.getById(projectId);
        if (round == null) { round = souProject.getCurrentRound(); }

        // 长城询比价禁止公开选项（暂时没有，而且公开场景会导致判断分支逻辑更加复杂，目前不宜考虑）
        AssertUtils.isFalse(SouPublishScopeEnum.OPEN_TENDER.equals(souProject.getPublishScope()), "长城询比价禁止公开场景");

        // 查找供应商轮次信息
        boolean canOrder; {
            ExtPjInqSouVendorRound vendorRound = extPjInqSouVendorRoundDAO.lambdaQuery()
                    .eq(ExtPjInqSouVendorRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPjInqSouVendorRound::getRound, round)
                    .eq(ExtPjInqSouVendorRound::getVendorId, vendorId)
                    .one();
            canOrder = vendorRound != null && Enable.Y.equals(vendorRound.getCanOrder());
        }
        if (!canOrder) { return Collections.emptyList(); }
        // 查询指定轮次可用的物料
        List<SouItem> souItemList; {
            Set<Long> availableSouItemIds = extPjInqSouItemRoundDAO.lambdaQuery()
                    .eq(ExtPjInqSouItemRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPjInqSouItemRound::getRound, round)
                    .list().stream().map(ExtPjInqSouItemRound::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemDAO.list(SouItem::getProjectId, projectId)
                    .stream().filter(e -> availableSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
            // 排除被关闭的物料
            Set<Long> closedSouItemIds = inqSouItemDAO.listByIds(souItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                    .stream().filter(e -> Enable.Y.equals(e.getHasClose()))
                    .map(InqSouItem::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemList.stream().filter(e -> !closedSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
        }
        return souItemList;
    }

    @ApiOperation("查询供应商在指定轮次可报价的物料信息")
    @Override
    public Map<Long/* vendorId */, List<SouItem>> getAvailableItemsForVendors(long projectId, @Nullable Integer round, Set<Long> vendorIds) {
        SouProject souProject = souProjectDAO.getById(projectId);
        if (round == null) { round = souProject.getCurrentRound(); }

        // 长城询比价禁止公开选项（暂时没有，而且公开场景会导致判断分支逻辑更加复杂，目前不宜考虑）
        AssertUtils.isFalse(SouPublishScopeEnum.OPEN_TENDER.equals(souProject.getPublishScope()), "长城询比价禁止公开场景");

        // 查找供应商轮次信息
        Set<Long> canOrderVendorIds = extPjInqSouVendorRoundDAO.lambdaQuery()
                .eq(ExtPjInqSouVendorRound::getProjectId, souProject.getProjectId())
                .eq(ExtPjInqSouVendorRound::getRound, round)
                .in(ExtPjInqSouVendorRound::getVendorId, vendorIds)
                .list().stream().filter(e -> Enable.Y.equals(e.getCanOrder())).map(ExtPjInqSouVendorRound::getVendorId).collect(Collectors.toSet());
        if (canOrderVendorIds.isEmpty()) { return Collections.emptyMap(); }
        // 查询指定轮次可用的物料
        List<SouItem> souItemList; {
            Set<Long> availableSouItemIds = extPjInqSouItemRoundDAO.lambdaQuery()
                    .eq(ExtPjInqSouItemRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPjInqSouItemRound::getRound, round)
                    .list().stream().map(ExtPjInqSouItemRound::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemDAO.list(SouItem::getProjectId, projectId)
                    .stream().filter(e -> availableSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
            // 排除被关闭的物料
            Set<Long> closedSouItemIds = inqSouItemDAO.listByIds(souItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                    .stream().filter(e -> Enable.Y.equals(e.getHasClose()))
                    .map(InqSouItem::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemList.stream().filter(e -> !closedSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
        }

        Map<Long/* vendorId */, List<SouItem>> resultMap = new HashMap<>(vendorIds.size());
        for (Long vendorId : vendorIds) {
            resultMap.put(vendorId, canOrderVendorIds.contains(vendorId) ? souItemList : Collections.emptyList());
        }
        return resultMap;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
