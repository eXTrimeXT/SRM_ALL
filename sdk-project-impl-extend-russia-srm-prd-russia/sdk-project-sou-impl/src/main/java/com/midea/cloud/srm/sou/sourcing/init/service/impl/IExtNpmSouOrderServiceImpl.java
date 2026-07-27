package com.midea.cloud.srm.sou.sourcing.init.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtNpmSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtNpmSouOrderMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouOrderService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description ext_scc_sou_order
 * @author panmq
 * @date 2023-11-08
 */
@Slf4j
@Service
public class IExtNpmSouOrderServiceImpl extends ServiceImpl<ExtNpmSouOrderMapper, ExtNpmSouOrder> implements IExtNpmSouOrderService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Override
    public List<ExtNpmSouOrder> extendSouOrder(List<ExtSouOrder> souOrderList) {
        if(CollectionUtils.isEmpty(souOrderList)) {
            return new ArrayList<>();
        }

        List<ExtNpmSouOrder> npmSouOrderList = new ArrayList<>();

        //查询数据库
        LambdaQueryWrapper<ExtNpmSouOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExtNpmSouOrder::getOrderId, souOrderList.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toList())).in(ExtNpmSouOrder::getRound, souOrderList.stream().map(ExtSouOrder::getRound).distinct().collect(Collectors.toList()));
        List<ExtNpmSouOrder> npmSouOrders = this.list(queryWrapper);
        Map<String, ExtNpmSouOrder> npmSouOrderMap = npmSouOrders.stream().collect(Collectors.toMap(o -> StringUtils.joinWith("_", o.getOrderId(), o.getRound(), ObjectUtils.defaultIfNull(o.getExtOrderType(), ExtOrderTypeEnum.BUS.getCode())), Function.identity(), (k1, k2)->k2));

        souOrderList.stream().forEach(extSouOrder -> {
            ExtNpmSouOrder npmSouOrder = new ExtNpmSouOrder();

            BeanCopyUtil.copyProperties(npmSouOrder, extSouOrder);
            npmSouOrder.setExtOrderType(ObjectUtils.defaultIfNull(extSouOrder.getExtOrderType(), ExtOrderTypeEnum.BUS.getCode()));
            npmSouOrder.setOrderStatus(ObjectUtils.defaultIfNull(extSouOrder.getOrderStatus(), SouOrderStatusEnum.DRAFT).name());
            String key = StringUtils.joinWith("_", extSouOrder.getOrderId(), extSouOrder.getRound(), ObjectUtils.defaultIfNull(extSouOrder.getExtOrderType(), ExtOrderTypeEnum.BUS.getCode()));

            ExtNpmSouOrder existsOrder = npmSouOrderMap.get(key);

            if(Objects.isNull(existsOrder)) {
                npmSouOrder.setExtOrderId(IdGenrator.generate());
            } else {
                npmSouOrder.setExtOrderId(existsOrder.getExtOrderId());
            }
            npmSouOrderList.add(npmSouOrder);
        });
        this.saveOrUpdateBatch(npmSouOrderList);
        return npmSouOrderList;
    }

    @Override
    public void updateReadBid(Long projectId) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "项目信息不存在");

        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, projectId)
                .eq(ExtSouOrder::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId()).list();
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(orderList), "投标信息不存在");

        List<ExtNpmSouOrder> extNpmSouOrders = this.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toList()))
                .eq(ExtNpmSouOrder::getRound, souProject.getCurrentRound()).list();

        extNpmSouOrders.stream().filter(o -> StringUtils.isBlank(o.getExtReadBidFlag())).forEach(extNpmSouOrder -> {
            extNpmSouOrder.setExtReadBidFlag(YesOrNo.YES.getValue());
            this.updateById(extNpmSouOrder);
        });
    }

    @Override
    public void updateDownloadTime(Long projectId) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "项目信息不存在");

        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, projectId)
                .eq(ExtSouOrder::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId()).list();
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(orderList), "投标信息不存在");

        List<ExtNpmSouOrder> extNpmSouOrders = this.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toList()))
                .eq(ExtNpmSouOrder::getRound, souProject.getCurrentRound()).list();

        extNpmSouOrders.stream().filter(o -> Objects.isNull(o.getExtDownBidFileTime())).forEach(extNpmSouOrder -> {
            extNpmSouOrder.setExtDownBidFileTime(new Date());
            this.updateById(extNpmSouOrder);
        });

    }

    @Override
    public List<ExtSouOrder> techOrderRange(Long projectId) {
        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, projectId).list();
        if(CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        }
        List<ExtNpmSouOrder> extNpmSouOrders = this.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toList())).eq(ExtNpmSouOrder::getRound, 1).eq(ExtNpmSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name()).list();
        if(CollectionUtils.isEmpty(extNpmSouOrders)) {
            return new ArrayList<>();
        }

        extNpmSouOrders = extNpmSouOrders.stream().sorted(Comparator.comparingInt(o -> ExtOrderTypeEnum.TECH.getCode().equals(o.getExtOrderType()) ? 0 : 1)).collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtNpmSouOrder::getOrderId))), ArrayList::new)
        );
        List<Long> orderIdList = extNpmSouOrders.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
        return orderList.stream().filter(o -> orderIdList.contains(o.getOrderId())).collect(Collectors.toList());
    }

    @Override
    public List<ExtSouOrder> queryNewestOrder(Long projectId) {
        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, projectId).list();
        if(CollectionUtils.isEmpty(orderList)) {
            return orderList;
        }

        //查询轮次投标信息
        List<ExtNpmSouOrder> extNpmSouOrders = this.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream()
                .map(ExtSouOrder::getOrderId).collect(Collectors.toList()))
                .orderByAsc(ExtNpmSouOrder::getSubmitTime).orderByDesc(ExtNpmSouOrder::getExtOrderType).list();

        Map<String, Integer> tenderTimesMap = statisticTenderTimes(extNpmSouOrders);

        extNpmSouOrders = extNpmSouOrders.stream().sorted(new Comparator<ExtNpmSouOrder>() {
            /** 顺序大的排在前面 */
            @Override
            public int compare(ExtNpmSouOrder o1, ExtNpmSouOrder o2) {
                int type1 = ExtOrderTypeEnum.TECH.getCode().equals(o1.getExtOrderType()) ? 0 : 1;
                int type2 = ExtOrderTypeEnum.TECH.getCode().equals(o2.getExtOrderType()) ? 0 : 1;
                if(Integer.compare(type2, type1) == SrmConstant.NUM_ZERO) {
                    return ObjectUtils.defaultIfNull(o2.getRound(), SrmConstant.NUM_ONE).compareTo(ObjectUtils.defaultIfNull(o1.getRound(), SrmConstant.NUM_ONE));
                }
                return Integer.compare(type2, type1);
            }
        }).collect(Collectors.toList());

        //最大轮次
        
        Map<Long, ExtNpmSouOrder> maxRoundOrder = new HashMap<>(16);
        extNpmSouOrders.stream().forEach(extNpmSouOrder -> {
            if(!maxRoundOrder.containsKey(extNpmSouOrder.getOrderId())) {
               maxRoundOrder.put(extNpmSouOrder.getOrderId(), extNpmSouOrder); 
            }
        });
        orderList.stream().forEach(o -> {
            ExtNpmSouOrder extNpmSouOrder = maxRoundOrder.get(o.getOrderId());
            
            if(ObjectUtils.allNotNull(extNpmSouOrder)) {
                copyBean(o, extNpmSouOrder);
                //报价状态
                o.setOrderStatus(toSouOrderStatusEnum(extNpmSouOrder.getOrderStatus()));
                //投标次数
                o.setTenderTimes(MapUtils.getInteger(tenderTimesMap, StringUtils.joinWith(SrmConstant.UNDER_LINE, o.getOrderId(), o.getRound()), 0));
            }
        });

        return orderList;
    }

    private Map<String, Integer> statisticTenderTimes(List<ExtNpmSouOrder> extNpmSouOrders) {
        Map<String, Integer> tenderTimesMap = new HashMap<>(50);

        if(CollectionUtils.isNotEmpty(extNpmSouOrders)) {
            Map<Long, List<ExtNpmSouOrder>> extNpmOrderGroup = extNpmSouOrders.stream().filter(o -> ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).collect(Collectors.groupingBy(o->o.getOrderId()));
            for(Long orderId : extNpmOrderGroup.keySet()) {
                List<ExtNpmSouOrder> roundList = extNpmOrderGroup.get(orderId).stream().sorted(Comparator.comparingInt(r->r.getRound())).collect(Collectors.toList());

                AtomicReference<Integer> times = new AtomicReference<>(0);
                roundList.stream().forEach(r -> {
                    if(SouOrderStatusEnum.SUBMISSION.name().equals(r.getOrderStatus())) {
                        times.getAndSet(times.get() + 1);
                    }
                    String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, r.getOrderId(), r.getRound());
                    tenderTimesMap.put(key, times.get());
                });
            }
        }

        return tenderTimesMap;
    }

    private void copyBean(ExtSouOrder extSouOrder, ExtNpmSouOrder extNpmSouOrder) {
        try {
            BeanCopyUtil.copyProperties(extSouOrder, extNpmSouOrder, true);
        } catch (Exception e) {
            log.error("copyBean Exception", e);
        }
    }

    private SouOrderStatusEnum toSouOrderStatusEnum(String orderStatus) {
        try {
            return SouOrderStatusEnum.valueOf(orderStatus);
        }catch (Exception e) {
            log.error("toSouOrderStatusEnum Exception" ,e);
        }
        return null;
    }

    @Override
    public List<ExtSouOrder> queryNewestBusSubmissionOrder(Long projectId) {
        List<ExtSouOrder> orderList = this.queryNewestOrder(projectId);
        if(CollectionUtils.isEmpty(orderList)) {
            return orderList;
        }
        return orderList.stream().filter(o -> SouOrderStatusEnum.SUBMISSION.equals(o.getOrderStatus())).filter(o -> ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).collect(Collectors.toList());
    }
}

