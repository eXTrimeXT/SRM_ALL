package com.midea.cloud.srm.sou.purinq.plugin.query.order;

import com.github.pagehelper.PageHelper;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ApiPurInqSouOrderQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.*;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
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
public class ApiPurInqSouOrderQueryHandler extends ApiSouOrderQueryHandler {

    @Autowired
    private ExtPurInqSouOrderMapper extPurInqSouOrderMapper;
    @Autowired
    private ExtPurInqSouVendorRoundDAO extPurInqSouVendorRoundDAO;
    @Autowired
    private ExtPurInqSouOrderDAO extPurInqSouOrderDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private ExtPurInqSouItemRoundDAO extPurInqSouItemRoundDAO;

    @Override
    @ApiOperation("查询报价单列表后的额外处理")
    public List<ApiSouOrderQueryVO> doHandlerForListOrders(ApiSouOrderQueryDTO queryParam, String souType) {
        queryParam.setVendorId(AppUserUtil.getLoginAppUser().getCompanyId());
        ApiPurInqSouOrderQueryDTO inqQueryParam = SouObjectXUtil.convertTargetObj(queryParam, ApiPurInqSouOrderQueryDTO.class);
        // 1: 查询数据
        if (inqQueryParam.getPageNum() != null || inqQueryParam.getPageSize() != null) {
            PageHelper.startPage(inqQueryParam.getPageNum(), inqQueryParam.getPageSize());
        }
        return extPurInqSouOrderMapper.listOrders(inqQueryParam, souType);
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
            Map<Long/* projectId */, Map<Integer/* round */, ExtPurInqSouVendorRound>> vendorRoundMap = extPurInqSouVendorRoundDAO.lambdaQuery()
                    .in(ExtPurInqSouVendorRound::getProjectId, projectIds)
                    .eq(ExtPurInqSouVendorRound::getVendorId, vendorId)
                    .eq(ExtPurInqSouVendorRound::getCanOrder, Enable.Y)
                    .orderByDesc(ExtPurInqSouVendorRound::getRound)
                    .list().stream().collect(Collectors.groupingBy(ExtPurInqSouVendorRound::getProjectId, Collectors.toMap(ExtPurInqSouVendorRound::getRound, Function.identity())));
            Date now = new Date();
            voList.forEach(vo -> {
                Map<Integer/* round */, ExtPurInqSouVendorRound> vendorRoundMp = vendorRoundMap.get(vo.getProjectId());
                if (vendorRoundMp == null || vendorRoundMp.isEmpty()) {
                    vo.setCanOrder(Enable.N);
                } else {
                    ExtPurInqSouVendorRound vr = vendorRoundMp.get(vo.getCurrentRound());
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

    @Override
    @ApiOperation("查询供应商报价明细后的额外处理")
    public ApiSouOrderDetailVO doHandlerAfterGetSouOrderInfo(long projectId, @Nullable Integer round, long vendorId, String souType, ApiSouOrderDetailVO vo) {
        vo = super.doHandlerAfterGetSouOrderInfo(projectId, round, vendorId, souType, vo);
        ApiPurInqSouOrderDetailVO inqVO = SouObjectXUtil.convertTargetObj(vo, ApiPurInqSouOrderDetailVO.class);
        {// 部分立项相关字段（项目策划）不能给供应商看到
            inqVO.getInitInfo().getProjectInfo().setDesignId(null);
            inqVO.getInitInfo().getProjectInfo().setDesignProjectCode(null);
            inqVO.getInitInfo().getProjectInfo().setDesignProjectName(null);
            inqVO.getInitInfo().getProjectInfo().setDesignNum(null);
            inqVO.getInitInfo().getProjectInfo().setDesignCreateUsername(null);
            inqVO.getInitInfo().getProjectInfo().setDesignCreateNickName(null);
            inqVO.getInitInfo().getProjectInfo().setDesignOrgDeptId(null);
            inqVO.getInitInfo().getProjectInfo().setDesignOrgDeptCode(null);
            inqVO.getInitInfo().getProjectInfo().setDesignOrgDeptName(null);
            inqVO.getInitInfo().getProjectInfo().setDesignProjMoney(null);
            inqVO.getInitInfo().getProjectInfo().setDesignProjIntroduce(null);
            inqVO.getInitInfo().getProjectInfo().setDesignPricingIdeas(null);
            inqVO.getInitInfo().getProjectInfo().setDesignArea(null);
            inqVO.getInitInfo().getProjectInfo().setAdjustCode(null);
            inqVO.getInitInfo().getProjectInfo().setAdjustName(null);
        }

        // 1: 查询转化额外物料需求
        Map<Long/* souItemId */, ExtPurInqSouItem> inqItemMap = extPurInqSouItemDAO.list(ExtPurInqSouItem::getProjectId, projectId)
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        inqVO.getInitInfo().getRequireInfo().forEach(item -> SouObjectXUtil.mergeProperties(inqItemMap.get(item.getSouItemId()), item));
        inqVO.getItemList().forEach(item -> SouObjectXUtil.mergeProperties(inqItemMap.get(item.getSouItemId()), item));
        // 2: 查询转化额外报价单
        if (inqVO.getOrder() != null) {
            ExtPurInqSouOrder inqOrder = extPurInqSouOrderDAO.getById(inqVO.getOrder().getOrderId());
            SouObjectXUtil.mergeProperties(inqOrder, inqVO.getOrder());
        }
        // 3: 查询转化额外报价明细
        Set<Long> orderItemIds = inqVO.getItemList().stream().map(ApiPurInqSouOrderItemVO::getOrderItemId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap; {
            if (orderItemIds.isEmpty()) {
                inqOrderItemMap = Collections.emptyMap();
            } else {
                inqOrderItemMap = extPurInqSouOrderItemDAO.listByIds(orderItemIds).stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
            }
        }
        inqVO.getItemList().forEach(item -> {
            if (item.getOrderItemId() != null) {
                SouObjectXUtil.mergeProperties(inqOrderItemMap.get(item.getOrderItemId()), item);
            }
        });

        return SouObjectXUtil.convertTargetObj(inqVO, ApiSouOrderDetailVO.class);
    }

    @Override
    @ApiOperation("查询寻源单指定轮次中哪些供应商具有报价的权限")
    public Set<Long/* vendorId */> getAuthedVendors(long projectId, @Nullable Integer round) {
        SouProject souProject = souProjectDAO.getById(projectId);
        if (round == null) { round = souProject.getCurrentRound(); }

        // 长城询比价禁止公开选项（暂时没有，而且公开场景会导致判断分支逻辑更加复杂，目前不宜考虑）
        AssertUtils.isFalse(SouPublishScopeEnum.OPEN_TENDER.equals(souProject.getPublishScope()), "长城询比价禁止公开场景");

        return extPurInqSouVendorRoundDAO.lambdaQuery()
                .eq(ExtPurInqSouVendorRound::getProjectId, projectId)
                .eq(ExtPurInqSouVendorRound::getRound, round)
                .eq(ExtPurInqSouVendorRound::getCanOrder, Enable.Y)
                .select(ExtPurInqSouVendorRound::getVendorId)
                .list().stream().map(ExtPurInqSouVendorRound::getVendorId).collect(Collectors.toSet());
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
            ExtPurInqSouVendorRound vendorRound = extPurInqSouVendorRoundDAO.lambdaQuery()
                    .eq(ExtPurInqSouVendorRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPurInqSouVendorRound::getRound, round)
                    .eq(ExtPurInqSouVendorRound::getVendorId, vendorId)
                    .one();
            canOrder = vendorRound != null && Enable.Y.equals(vendorRound.getCanOrder());
        }
        if (!canOrder) { return Collections.emptyList(); }
        // 查询指定轮次可用的物料
        List<SouItem> souItemList; {
            Set<Long> availableSouItemIds = extPurInqSouItemRoundDAO.lambdaQuery()
                    .eq(ExtPurInqSouItemRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPurInqSouItemRound::getRound, round)
                    .list().stream().map(ExtPurInqSouItemRound::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemDAO.list(SouItem::getProjectId, projectId)
                    .stream().filter(e -> availableSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
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
        Set<Long> canOrderVendorIds = extPurInqSouVendorRoundDAO.lambdaQuery()
                .eq(ExtPurInqSouVendorRound::getProjectId, souProject.getProjectId())
                .eq(ExtPurInqSouVendorRound::getRound, round)
                .in(ExtPurInqSouVendorRound::getVendorId, vendorIds)
                .list().stream().filter(e -> Enable.Y.equals(e.getCanOrder())).map(ExtPurInqSouVendorRound::getVendorId).collect(Collectors.toSet());
        if (canOrderVendorIds.isEmpty()) { return Collections.emptyMap(); }
        // 查询指定轮次可用的物料
        List<SouItem> souItemList; {
            Set<Long> availableSouItemIds = extPurInqSouItemRoundDAO.lambdaQuery()
                    .eq(ExtPurInqSouItemRound::getProjectId, souProject.getProjectId())
                    .eq(ExtPurInqSouItemRound::getRound, round)
                    .list().stream().map(ExtPurInqSouItemRound::getSouItemId).collect(Collectors.toSet());
            souItemList = souItemDAO.list(SouItem::getProjectId, projectId)
                    .stream().filter(e -> availableSouItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
        }

        Map<Long/* vendorId */, List<SouItem>> resultMap = new HashMap<>(vendorIds.size());
        for (Long vendorId : vendorIds) {
            resultMap.put(vendorId, canOrderVendorIds.contains(vendorId) ? souItemList : Collections.emptyList());
        }
        return resultMap;
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
