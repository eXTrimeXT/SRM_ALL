package com.midea.cloud.srm.sou.sourcing.bpmtodo.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.sou.bpmtodo.dto.SouBpmtodoParam;
import com.midea.cloud.srm.model.sou.bpmtodo.dto.SouBpmtodoResponse;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ExtSouOrderDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.sourcing.bpmtodo.service.ExtSouBpmtodoService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtNpmSouOrderService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/31
 */
@Service
@Slf4j
public class ExtSouBpmtodoServiceImpl implements ExtSouBpmtodoService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtNpmSouOrderService npmSouOrderService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private ExtBidSouInitQueryWebService bidSouInitQueryWebService;

    @Autowired
    private IExtNpmSouOpenBidRecordService iExtNpmSouOpenBidRecordService;

    /**
     * 招标流程手机待办-商务标信息查询接口
     * @param param
     * @return
     */
    @Override
    public SouBpmtodoResponse queryBusTodoInfo(SouBpmtodoParam param) {
        //校验和初始化请求参数
        checkAndIniteParam(param);
        //查询项目信息
        ExtSouProject souProject = projectService.getById(param.getProjectId());
        AssertUtils.notNull(souProject, "请求参数有误，找不到项目信息！");

        //响应实体
        SouBpmtodoResponse response = new SouBpmtodoResponse();
        response.setShowOpenButton(YesOrNo.NO.getValue());
        response.setExtSouProject(souProject);
        response.setExtSouOrderDtoList(new ArrayList<>(15));

        //查询投标供应商
        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, souProject.getProjectId()).list();
        if(CollectionUtils.isEmpty(orderList)) {
            return response;
        }

        //查询小于等于查询轮次投标的供应商(已投标和废标)
        List<ExtNpmSouOrder> leRoundExtSouOrderList = npmSouOrderService.lambdaQuery().in(ExtNpmSouOrder::getOrderId, orderList.stream().map(o -> o.getOrderId()).collect(Collectors.toList()))
                .le(ExtNpmSouOrder::getRound, param.getRound()).in(ExtNpmSouOrder::getOrderStatus, Arrays.asList(SouOrderStatusEnum.SUBMISSION.name(), SouOrderStatusEnum.CANCEL.name()))
                .list();
        //没有查询轮次数据时终止程序
        if(CollectionUtils.isEmpty(leRoundExtSouOrderList) || !leRoundExtSouOrderList.stream().anyMatch(o -> Integer.compare(o.getRound(), param.getRound()) == 0)) {
            return response;
        }

        //统计投标次数
        Map<Long, Set<Integer>> tenderTimesMap = leRoundExtSouOrderList.stream().collect(Collectors.groupingBy(o -> o.getOrderId(), Collectors.mapping(o->o.getRound(), Collectors.toSet())));

        //查询轮次的数据
        List<ExtNpmSouOrder> extSouOrderList = leRoundExtSouOrderList.stream().filter(o -> Integer.compare(o.getRound(), param.getRound()) == 0).collect(Collectors.toList());

        //过滤投标供应商数据
        List<Long> orderIdList = extSouOrderList.stream().map(o -> o.getOrderId()).distinct().collect(Collectors.toList());
        orderList = orderList.stream().filter(o -> orderIdList.contains(o.getOrderId())).collect(Collectors.toList());

        //查询供应商投标报价信息
        List<ExtSouOrderItem> orderItemList = orderItemService.lambdaQuery().in(ExtSouOrderItem::getOrderId, extSouOrderList.stream().map(o -> o.getOrderId()).distinct().collect(Collectors.toList()))
                .eq(ExtSouOrderItem::getRound, param.getRound()).list();
        if(CollectionUtils.isEmpty(orderItemList)) {
            return response;
        }
        //投标报价信息分组
        Map<Long, List<ExtSouOrderItem>> orderItemGroup = orderItemList.stream().collect(Collectors.groupingBy(o -> o.getOrderId()));

        //查询报价模板
        ApiExtSouPriceTemplateDto templateDto = bidSouInitQueryWebService.listPriceTemplate(param.getProjectId());
        //取含税报价标识
        AtomicBoolean extPriceTaxFlag = new AtomicBoolean(templateDto.getSelectedList().stream().anyMatch(f -> "extPriceTax".equals(f.getColumnCode())));
        //报价模板是否包含数量/工程量
        AtomicBoolean extQuantityFlag = new AtomicBoolean(templateDto.getSelectedList().stream().anyMatch(f -> ExtSouBidComponent.fieldName(ExtSouItem::getExtQuantity).equals(f.getColumnCode())));

        //查询报价信息
        List<ExtSouItem> itemList = itemService.listByIds(orderItemList.stream().map(o -> o.getSouItemId()).distinct().collect(Collectors.toList()));
        Map<Long, ExtSouItem> itemMap = itemList.stream().collect(Collectors.toMap(k -> k.getSouItemId(), Function.identity(), (k1, k2) -> k2));

        //查询供应商信息
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, param.getProjectId()).list();
        Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(k -> k.getVendorId(), Function.identity(), (k1, k2) -> k2));

        //商务投标中，当前轮次不允许查看价格
        Boolean shieldPrice = Arrays.asList(SouBiddingProStatusEnum.TECH_BID_OPEN.getCode(), SouBiddingProStatusEnum.TECH_BID_EVA.getCode(), SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode(), SouBiddingProStatusEnum.BUS_BID.getCode(), SouBiddingProStatusEnum.BUS_BID_END.getCode()).contains(souProject.getProjectStatus())
                && Integer.compare(souProject.getCurrentRound(), param.getRound()) == 0;
        orderList.stream().forEach(order -> {
            ExtSouOrderDto extSouOrderDto = new ExtSouOrderDto();
            extSouOrderDto.setVendorName(vendorMap.getOrDefault(order.getVendorId(), new ExtSouVendor()).getVendorName());
            extSouOrderDto.setTenderTimes(tenderTimesMap.getOrDefault(order.getOrderId(), new HashSet<>(1)).size());

            if(!shieldPrice) {
                //计算含税总价
                extSouOrderDto.setExtTaxAmount(taxAmount(orderItemGroup.get(order.getOrderId()), extPriceTaxFlag, extQuantityFlag, itemMap));
            }
            response.getExtSouOrderDtoList().add(extSouOrderDto);
        });

        //开标按钮显示
        List<ExtNpmSouOpenBidRecord> records = iExtNpmSouOpenBidRecordService.lambdaQuery().eq(ExtNpmSouOpenBidRecord::getProjectId, param.getProjectId())
                .eq(ExtNpmSouOpenBidRecord::getRound, param.getRound())
                .eq(ExtNpmSouOpenBidRecord::getOpenType, ExtOrderTypeEnum.BUS.getCode())
                .eq(ExtNpmSouOpenBidRecord::getUserName, AppUserUtil.getUserName()).list();
        if(CollectionUtils.isNotEmpty(records) && records.stream().anyMatch(s -> ProcessStatusEnum.PENDING.getCode().equals(s.getOpenStatus()))) {
            response.setShowOpenButton(YesOrNo.YES.getValue());
        }

        return response;
    }

    private BigDecimal taxAmount(List<ExtSouOrderItem> orderItemList, AtomicBoolean extPriceTaxFlag, AtomicBoolean extQuantityFlag, Map<Long, ExtSouItem> itemMap) {
        if(CollectionUtils.isEmpty(orderItemList)) {
            return null;
        }

        AtomicReference<BigDecimal> totalTaxAmount = new AtomicReference<>(BigDecimal.ZERO);
        orderItemList.stream().forEach(orderItem -> {
            ApiExtSouOrderItemDto orderItemDto = new ApiExtSouOrderItemDto();
            BeanCopyUtil.copyProperties(orderItemDto, orderItem);
            //将报价信息转换成报价信息表的字段
            orderItemDto.coverItemFields();
            //按汇率转换成CNY的价格
            orderItemDto.convertExchangeRateAsItemFields();

            //含税单价
            BigDecimal taxPrice = null;
            if(extPriceTaxFlag.get()) {
                //取含税报价标识
                taxPrice = orderItemDto.getExtPriceTax();
            } else {
                //固定含税单价
                taxPrice = orderItemDto.getExtFixedPriceTax();
            }

            //数量
            BigDecimal quantity = null;
            ExtSouItem item = itemMap.getOrDefault(orderItem.getSouItemId(), new ExtSouItem());
            if(extQuantityFlag.get()) {
                //数量/工程量
                quantity = item.getExtQuantity();
            } else {
                //固定含税单价
                quantity = item.getRequireQuantity();
            }

            //累加
            if(ObjectUtils.allNotNull(taxPrice, quantity)) {
                BigDecimal taxAmount = taxPrice.multiply(quantity);
                totalTaxAmount.set(totalTaxAmount.get().add(taxAmount));
            }

        });

        return totalTaxAmount.get();
    }

    /**
     * 校验请求参数和初始化请求参数
     * @param param
     */
    private void checkAndIniteParam(SouBpmtodoParam param) {
        AssertUtils.notNull(param.getProjectId(), "招标单ID不能为空");
        //未传请求参数时默认查第一轮
        if(Objects.isNull(param.getRound())) {
            param.setRound(1);
        }
    }
}
