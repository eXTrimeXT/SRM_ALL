package com.midea.cloud.srm.supcooperate.ext.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveDetail;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeDetailQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceivePerEmp;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNote;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNoteDetail;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.OrderDetailStatus;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.pr.requirement.service.IRequirementLineService;
import com.midea.cloud.srm.supcooperate.eas.entity.ReceiveInfo;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNoteDetail;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.enums.ExtDeliveryNoteDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.service.ExtDeliveryNoteService;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.mapper.PurchaseOrderReceiveOnTimeDetailMapper;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import com.midea.cloud.srm.supcooperate.order.service.IOrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Service
public class ExtOrderServiceImpl implements ExtOrderService {

    @Autowired
    private IRequirementLineService requirementLineService;
    @Autowired
    private IOrderDetailService orderDetailService;
    @Autowired
    private ExtDeliveryNoteService extDeliveryNoteService;
    @Autowired
    private QlService qlService;
    @Autowired
    private PurchaseOrderReceiveOnTimeDetailMapper purchaseOrderReceiveOnTimeDetailMapper;
    @Autowired
    private BaseClient baseClient;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelOrderDetail(List<Long> orderDetailIds, String extReturnRequirement, String closedCause) {
        Assert.notEmpty(orderDetailIds, "订单明细id不能为空");
        List<Record> details = qlService.readByKeys(PurchaseSchemaEnum.ORDER_DETAIL.getType(), orderDetailIds, Record.class);
        details.forEach(e -> {
            Assert.isTrue(OrderDetailStatus.ACCEPT == e.get(ExtOrderDetail::getOrderDetailStatus), "当前状态不能取消");
            if (ExtOrderDetailStatusEnum.FINISHED.name().equals(e.get(ExtOrderDetail::getExtDetailStatus))) {
                throw new BaseException("当前状态不能取消");
            }
        });
        long ongoingCount = qlService.countByWrapper(QlWrappers.query("DeliveryNoteDetail")
                .in(DeliveryNoteDetail::getOrderDetailId, orderDetailIds)
                .notIn(ExtDeliveryNoteDetail::getExtDetailStatus, Arrays.asList(ExtDeliveryNoteDetailStatusEnum.CANCELLED, ExtDeliveryNoteDetailStatusEnum.FINISHED)));
        Assert.isTrue(ongoingCount == 0, "所勾选行存在送货单，请取消送货单后再取消订单");

        // 退回需求池
        if (YesOrNo.YES.getValue().equals(extReturnRequirement)) {
            Map<Long, BigDecimal> requirementQtyMap = details.stream().filter(e -> e.get(OrderDetail::getCeeaRequirementLineId) != null)
                    .collect(Collectors.toMap(e -> e.get(OrderDetail::getCeeaRequirementLineId),
                            e -> BigDecimalUtil.sub(e.get(OrderDetail::getOrderNum), e.get(OrderDetail::getReceiveSum))));
            if (MapUtils.isNotEmpty(requirementQtyMap)) {
                List<RequirementLine> requirementLines = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                        .select(RequirementLine::getRequirementLineId, RequirementLine::getOrderQuantity, RequirementLine::getVersion)
                        .in(RequirementLine::getRequirementLineId, requirementQtyMap.keySet()), RequirementLine.class);
                requirementLines.forEach(e -> {
                    BigDecimal remainQty = BigDecimalUtil.add(e.getOrderQuantity(), requirementQtyMap.get(e.getRequirementLineId()));
                    BigDecimal usedQty = BigDecimalUtil.sub(e.getRequirementQuantity(), remainQty);
                    boolean success = requirementLineService.update(new LambdaUpdateWrapper<RequirementLine>()
                            .set(RequirementLine::getOrderQuantity, remainQty)
                            .set(RequirementLine::getCeeaExecutedQuantity, usedQty)
                            .set(RequirementLine::getVersion, e.getVersion() + 1)
                            .eq(RequirementLine::getRequirementLineId, e.getRequirementLineId())
                            .eq(RequirementLine::getVersion, e.getVersion()));
                    Assert.isTrue(success, "数据已发生变化，请重试");
                });
            }
        }

        // 修改状态
        details.forEach(e -> {
            List<Serializable> serializables = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                    .set(ExtOrderDetail::getOrderDetailStatus, OrderDetailStatus.CLOSED)
                    .set(ExtOrderDetail::getExtDetailStatus, null)
                    .set(ExtOrderDetail::getClosedCause, closedCause)
                    .set(ExtOrderDetail::getExtReturnRequirement, extReturnRequirement)
                    .set(ExtOrderDetail::getVersion, e.get(OrderDetail::getVersion) + 1)
                    .eq(ExtOrderDetail::getOrderDetailId, e.get(OrderDetail::getOrderDetailId))
                    .eq(ExtOrderDetail::getVersion, e.get(OrderDetail::getVersion)));
            Assert.notEmpty(serializables, "数据已发生变化，请重试");
        });

        // 更新订单状态
        List<Long> orderIds = details.stream().map(e -> e.get(OrderDetail::getOrderId)).collect(Collectors.toList());
        updateExtOrderStatus(orderIds, closedCause);
    }

    /**
     * 根据送货进度更新订单状态
     */
    @Override
    public void updateExtOrderStatus(List<Long> orderIds, String closedCause) {
        List<Record> orders = qlService.readByKeys(PurchaseSchemaEnum.ORDER.getType(), orderIds, Record.class);
        Map<Long, Record> orderMap = orders.stream().collect(Collectors.toMap(e -> e.get(Order::getOrderId), e -> e, (v1, v2) -> v1));
        List<Record> details = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType()).in(OrderDetail::getOrderId, orderIds), Record.class);
        Map<Long, List<Record>> orderDetailMap = details.stream().collect(Collectors.groupingBy(e->e.get(OrderDetail::getOrderId)));
        orderDetailMap.forEach((k, v) -> {
            Record order = orderMap.get(k);
            long closeCount = v.stream().filter(e -> OrderDetailStatus.CLOSED == e.get(OrderDetail::getOrderDetailStatus)).count();
            if (closeCount == v.size()) {
                List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER.getType())
                        .set(ExtOrder::getExtStatus, null)
                        .set(ExtOrder::getOrderStatus, PurchaseOrderEnum.CLOSED)
                        .set(ExtOrder::getClosedCause, closedCause)
                        .set(ExtOrder::getVersion, order.get(ExtOrder::getVersion) + 1)
                        .eq(ExtOrder::getOrderId, order.get(ExtOrder::getOrderId))
                        .eq(ExtOrder::getVersion, order.get(ExtOrder::getVersion)));
                Assert.notEmpty(ids, "数据已发生变化，请重试");
                return;
            }

            String extStatus = null;
            long finishCount = v.stream().filter(e ->
                    ExtOrderDetailStatusEnum.FINISHED.name().equals(e.get(ExtOrderDetail::getExtDetailStatus))
                    || OrderDetailStatus.CLOSED.equals(e.get(OrderDetail::getOrderDetailStatus))).count();
            if (finishCount == v.size()) {
                extStatus = ExtOrderStatusEnum.FINISHED.name();
            } else {
                long ongoingCount = v.stream().filter(e -> ExtOrderDetailStatusEnum.ONGOING.name().equals(e.get(ExtOrderDetail::getExtDetailStatus))).count();
                if (ongoingCount > 0) {
                    extStatus = ExtOrderStatusEnum.ONGOING.name();
                }
            }

            if (extStatus == null || !extStatus.equals(order.get(ExtOrder::getExtStatus))) {
                List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER.getType())
                        .set(ExtOrder::getExtStatus, extStatus)
                        .set(ExtOrder::getVersion, order.get(ExtOrder::getVersion) + 1)
                        .eq(ExtOrder::getOrderId, order.get(ExtOrder::getOrderId))
                        .eq(ExtOrder::getVersion, order.get(ExtOrder::getVersion)));
                Assert.notEmpty(ids, "数据已发生变化，请重试");
            }
        });
    }

    @Override
    public ExtOrderDetailStatusEnum calcExtOrderDetailStatus(Record orderDetail) {
        BigDecimal orderNum = orderDetail.get(OrderDetail::getOrderNum);
        BigDecimal receiveSum = orderDetail.get(OrderDetail::getReceiveSum);
        BigDecimal storageNum = orderDetail.get(OrderDetail::getStorageNum);

        ExtOrderDetailStatusEnum status = ExtOrderDetailStatusEnum.ONGOING;
        if (receiveSum.compareTo(BigDecimal.ZERO) == 0) {
            status = null;
        } else if (orderNum.compareTo(storageNum) == 0) {
            status = ExtOrderDetailStatusEnum.FINISHED;
        }
        return status;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void writebackStorageQty(List<WarehousingReturnDetail> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 回写送货单入库数量
        Map<Long, BigDecimal> deliveryReceiveMap = list.stream().collect(Collectors.toMap(e -> e.getDeliveryNoteDetailId(), WarehousingReturnDetail::getReceiveNum, (v1, v2) -> BigDecimalUtil.add(v1, v2)));
        List<Record> deliveryNoteDetails = qlService.readByKeys(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType(), new ArrayList(deliveryReceiveMap.keySet()), Record.class);
        writebackByDeliveryReceive(deliveryReceiveMap, deliveryNoteDetails, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void writebackReceiveQty(List<ReceiveInfo> list) {
        Map<String, List<ReceiveInfo>> map = list.stream().collect(Collectors.groupingBy(ReceiveInfo::getDeliveryNoteNum));
        Set<String> deliveryNoteNums = map.keySet();
        Assert.notEmpty(deliveryNoteNums, "送货单号不存在");
        List<Record> deliveryNotes = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE.getType())
                .in(DeliveryNote::getDeliveryNumber, deliveryNoteNums), Record.class);
        if (CollectionUtils.isEmpty(deliveryNotes) || deliveryNoteNums.size() != deliveryNotes.size()) {
            throw new BaseException("存在送货单号错误");
        }

        List<Record> deliveryNoteDetails = new ArrayList<>();
        Map<Long, BigDecimal> deliveryReceiveMap = new HashMap<>(16);
        deliveryNotes.forEach(deliveryNote -> {
            //获取单个送货单下 送货明细
            List<ReceiveInfo> receiveInfos = map.get(deliveryNote.get(DeliveryNote::getDeliveryNumber));
            //eas 单个明细下 汇总数量
            Map<String, BigDecimal> lineQtyMap = receiveInfos.stream().collect(Collectors.groupingBy(e -> e.getDeliveryNoteLineNum(), Collectors.mapping(e1 -> new BigDecimal(e1.getCollectNum()), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            List<Record> details = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                    .eq(DeliveryNoteDetail::getDeliveryNoteId, deliveryNote.get(DeliveryNote::getDeliveryNoteId)).in(DeliveryNoteDetail::getLineNum, lineQtyMap.keySet()), Record.class);
            //放入明细行数量
            details.forEach(e -> deliveryReceiveMap.put(e.get(DeliveryNoteDetail::getDeliveryNoteDetailId), lineQtyMap.get(e.get(DeliveryNoteDetail::getLineNum).toString())));

            deliveryNoteDetails.addAll(details);
        });


        // 回写送货单入库数量
        writebackByDeliveryReceive(deliveryReceiveMap, deliveryNoteDetails, false);
    }

    @Override
    public PageInfo<OrderReceivePerEmp> findReceiveOnTimeRatio(OrderReceiveOnTimeQueryParam orderReceiveOnTimeQueryParam) {
        String orderDetailAl = "od";
        String orderAl = "o";
        List<Date> orderDates = orderReceiveOnTimeQueryParam.getCeeaPurchaseOrderDate();
        int maxSize = 2;
        if(CollUtil.isEmpty(orderDates)||orderDates.size()<maxSize){
            throw new BaseException("订单日期范围不能为空");
        }
        setNullPage(orderReceiveOnTimeQueryParam);
        /*默认添加日期范围*/
        QlQueryWrapper qlQueryWrapper = QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType(),orderDetailAl)
                .select(QlQueryFieldWrapper.field(orderAl,ExtOrder::getOrderId))
                .select(ExtOrder::getCeeaOrgId)
                .select(ExtOrder::getCeeaOrgName)
                .select(ExtOrder::getCeeaOrgCode)
                .select(ExtOrder::getVendorCode)
                .select(ExtOrder::getVendorName)
                .select(ExtOrder::getCeeaEmpUserId)
                .select(ExtOrder::getCeeaEmpNo)
                .select(ExtOrder::getCeeaEmpUsername)
                .select(QlQueryFieldWrapper.sum(OrderDetail::getOrderNum, LambdaUtil.getFieldName(OrderDetail::getOrderNum)))
                .select(QlQueryFieldWrapper.sum(OrderDetail::getReceiveSum, LambdaUtil.getFieldName(OrderDetail::getReceiveSum)))
                .innerJoin(PurchaseSchemaEnum.ORDER.getType(),orderAl,o->{
                    o.eq(QlQueryFieldWrapper.field(orderDetailAl,OrderDetail::getOrderId),
                            QlQueryFieldWrapper.field(orderAl, Order::getOrderId));
                }).ge(com.midea.cloud.srm.model.suppliercooperate.order.entry.Order::getCeeaPurchaseOrderDate,orderDates.get(0))
                .and(o->{
                    o.lt(Order::getCeeaPurchaseOrderDate,orderDates.get(1));
                });
        /* 采购员 */
        if(ObjectUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getCeeaEmpUsername())){
            qlQueryWrapper.like(Order::getCeeaEmpUsername,"%"+orderReceiveOnTimeQueryParam.getCeeaEmpUsername()+"%");
        }
        if(ObjectUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getCeeaEmpNo())){
            qlQueryWrapper.eq(Order::getCeeaEmpNo,orderReceiveOnTimeQueryParam.getCeeaEmpNo());
        }
        /* 申请单位代码 */
        if(ObjectUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getCeeaOrgCode())){
            qlQueryWrapper.eq(ExtOrder::getCeeaOrgCode,orderReceiveOnTimeQueryParam.getCeeaOrgCode());
        }

        /* 申请单位ID */
        if(ObjectUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getCeeaOrgId())){
            qlQueryWrapper.eq(ExtOrder::getCeeaOrgId,orderReceiveOnTimeQueryParam.getCeeaOrgId());
        }

        /* 供应商名称模糊查询 */
        if(ObjectUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getVendorName())){
            qlQueryWrapper.like(ExtOrder::getVendorName,"%"+orderReceiveOnTimeQueryParam.getVendorName()+"%");
        }
        if(ObjectUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getVendorCode())){
            qlQueryWrapper.eq(ExtOrder::getVendorCode,orderReceiveOnTimeQueryParam.getVendorCode());
        }
        /* 该供应商+申请单位+采购员下所有的送货单数据的收货总数量/订单总数量
        *  按供应商+申请单位+采购员分组 */
        qlQueryWrapper.groupBy(ExtOrder::getVendorCode,ExtOrder::getCeeaOrgCode,ExtOrder::getCeeaEmpNo);
        log.info(qlQueryWrapper.toString());
        List<OrderReceivePerEmp> orderReceivePerEmps = qlService.queryByWrapper(qlQueryWrapper,OrderReceivePerEmp.class);
        if(CollUtil.isNotEmpty(orderReceivePerEmps)) {
            List<OrderReceiveDetail> List = purchaseOrderReceiveOnTimeDetailMapper.queryReceiveOnTimeDetail2(orderReceivePerEmps);
            Map<String, Integer> tot = new HashMap<>(15);
            Map<String, Integer> receiveTot = new HashMap<>(15);
            for (OrderReceiveDetail orderReceiveDetail : List) {
                Date date = orderReceiveDetail.getEasReceiveTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (date != null) {
                    String formattedDateStr = sdf.format(date);
                    if (formattedDateStr.compareTo((orderReceiveDetail.getDeliveryDate())) > 0) {
                        orderReceiveDetail.setDeliveryNoteDetailStatus("延期交货");
                    } else {
                        orderReceiveDetail.setDeliveryNoteDetailStatus("按时交货");
                    }
                }
                        else{
                    orderReceiveDetail.setDeliveryNoteDetailStatus("执行中");
                }
                StringBuilder sb=new StringBuilder();
                sb.append(orderReceiveDetail.getCeeaOrgId()).append("-").append(orderReceiveDetail.getCeeaOrgCode()).append("-")
                        .append(orderReceiveDetail.getVendorCode()).append("-").append(orderReceiveDetail.getCeeaEmpNo());
                Integer temp=tot.getOrDefault(sb.toString(),0);
                temp++;
                tot.put(sb.toString(),temp);
                if ("按时交货".equals(orderReceiveDetail.getDeliveryNoteDetailStatus())) {
                    Integer temp2=receiveTot.getOrDefault(sb.toString(),0);
                    temp2++;
                    receiveTot.put(sb.toString(),temp2);
                }
            }
            orderReceivePerEmps.forEach(e->{
                String ceeaEmpNo=e.getCeeaEmpNo();
                String ceeaOrgCode=e.getCeeaOrgCode();
                Long ceeaOrgId=e.getCeeaOrgId();
                String vendorCode=e.getVendorCode();
                StringBuilder sb=new StringBuilder();
                sb.append(ceeaOrgId).append("-").append(ceeaOrgCode).append("-")
                        .append(vendorCode).append("-").append(ceeaEmpNo);
                Integer total=tot.getOrDefault(sb.toString(),0);
                Integer receiveTotal=receiveTot.getOrDefault(sb.toString(),0);
                if(total==0) {
                    //这里的1没有任何意义，只是为了下面计算的统一
                    total=1;
                    receiveTotal=1;
                }
                e.setReceiveOnTime(new BigDecimal(receiveTotal).divide(new BigDecimal(total),4, RoundingMode.DOWN).multiply(new BigDecimal(100)).setScale(2));
            });
        }
        PageInfo pageInfo;
        int total = orderReceivePerEmps.size();
        orderReceivePerEmps = getLimitList(orderReceivePerEmps,OrderReceivePerEmp.class,orderReceiveOnTimeQueryParam.getPageNum(), orderReceiveOnTimeQueryParam.getPageSize());
        pageInfo =  new PageInfo<>(orderReceivePerEmps);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public PageInfo<OrderReceiveDetail> findReceiveOnTimeDetail(OrderReceiveOnTimeDetailQueryParam orderReceiveOnTimeDetailQueryParam) throws ParseException {
        setNullPage(orderReceiveOnTimeDetailQueryParam);
        List<OrderReceiveDetail> orderReceiveDetails = purchaseOrderReceiveOnTimeDetailMapper.queryReceiveOnTimeDetail(orderReceiveOnTimeDetailQueryParam);
        if(CollUtil.isNotEmpty(orderReceiveDetails)){
//            List<String> unitCodes = orderReceiveDetails.stream().map(OrderReceiveDetail::getUnit).collect(Collectors.toList());
//            List<PurchaseUnit> purchaseUnits = baseClient.listPurchaseUnitByCodeList(unitCodes);
            Map<String, DictItemDTO> orderDetailStatusMap = getDictItemMap("OrderDetailStatus");
            Map<String, DictItemDTO> deliverNoteDetailMap = getDictItemMap("DELIVERY_NOTE_DETAIL_STATUS");
//            Map<String, List<PurchaseUnit>> purchaseUnitMap = new HashMap<>(16);
//            if(CollUtil.isNotEmpty(purchaseUnits)){
//                purchaseUnitMap = purchaseUnits.stream().collect(Collectors.groupingBy(PurchaseUnit::getUnitCode));
//            }
            for (OrderReceiveDetail orderReceiveDetail:orderReceiveDetails){
                if(orderDetailStatusMap.containsKey(orderReceiveDetail.getOrderDetailStatus())){
                    orderReceiveDetail.setOrderDetailStatusDesc(orderDetailStatusMap.get(orderReceiveDetail.getOrderDetailStatus()).getDictItemName());
                }
//                if(purchaseUnitMap.containsKey(orderReceiveDetail.getUnit())){
//                    orderReceiveDetail.setUnitDesc(purchaseUnitMap.get(orderReceiveDetail.getUnit()).get(0).getUnitName());
//                }
                if(deliverNoteDetailMap.containsKey(orderReceiveDetail.getDeliveryNoteDetailStatus())){
                    orderReceiveDetail.setDeliveryNoteDetailStatusDesc(deliverNoteDetailMap.get(orderReceiveDetail.getDeliveryNoteDetailStatus()).getDictItemName());
                }

                /**
                 * 修改送货单行号deliveryNoteDetailStatus
                 * 送货状态  如果收货时间>交货时间 延期交货， 如果收货时间<=交货日期 按时交货
                 */

                /**
                 * 首先截取收货时间的时分秒
                 */
                Date date=orderReceiveDetail.getEasReceiveTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if(date!=null) {
                    String formattedDateStr = sdf.format(date);
                    if (formattedDateStr.compareTo((orderReceiveDetail.getDeliveryDate())) > 0) {
                        orderReceiveDetail.setDeliveryNoteDetailStatus("延期交货");
                    } else {
                        orderReceiveDetail.setDeliveryNoteDetailStatus("按时交货");
                    }
                }
                /**
                 * 如果EAS收货时间为NULL 订单行状态设为执行中
                 */
                else{
                    orderReceiveDetail.setDeliveryNoteDetailStatus("执行中");
                }
            }

        }
        int total = orderReceiveDetails.size();
        orderReceiveDetails = getLimitList(orderReceiveDetails,OrderReceiveDetail.class, orderReceiveOnTimeDetailQueryParam.getPageNum(),orderReceiveOnTimeDetailQueryParam.getPageSize());
        PageInfo pageInfo =  new PageInfo<>(orderReceiveDetails);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    private void writebackByDeliveryReceive(Map<Long, BigDecimal> deliveryReceiveMap, List<Record> deliveryNoteDetails, boolean storageFlag) {
        List<Record> deliveryRecords = deliveryNoteDetails.stream().map(e -> {
            //eas当前送货明细行数量
            BigDecimal qty = deliveryReceiveMap.get(e.get(DeliveryNoteDetail::getDeliveryNoteDetailId));
            BigDecimal warehouseQty = BigDecimalUtil.add(e.get(DeliveryNoteDetail::getWarehouseQuantity), qty);
            //srm当前送货明细行数量
            BigDecimal deliveryQuantity = e.get(DeliveryNoteDetail::getDeliveryQuantity);

            //增加校验  累计到货数量 <= 本次送货数量 - 送货单累计已取消数量
            if(warehouseQty.compareTo(BigDecimalUtil.sub(deliveryQuantity, e.get(ExtDeliveryNoteDetail::getExtCancelQty))) > 0){
                log.info("累计到货数量异常,{},{},{},{}",e.get(DeliveryNoteDetail::getDeliveryNoteDetailId),warehouseQty,deliveryQuantity,e.get(ExtDeliveryNoteDetail::getExtCancelQty));
                throw new BaseException("累计到货数量 不能大于（本次送货数量 - 送货单累计已取消数量）");
            }

            Record record = new Record();
            record.put(DeliveryNoteDetail::getDeliveryNoteDetailId, e.get(DeliveryNoteDetail::getDeliveryNoteDetailId));
            record.put(DeliveryNoteDetail::getWarehouseQuantity, warehouseQty);
            if (warehouseQty.compareTo(e.get(DeliveryNoteDetail::getDeliveryQuantity)) >= 0) {
                record.put(ExtDeliveryNoteDetail::getExtDetailStatus, ExtDeliveryNoteDetailStatusEnum.FINISHED);
                record.put(ExtDeliveryNoteDetail::getExtFinishTime, LocalDateTime.now());
            } else if(warehouseQty.compareTo(BigDecimal.ZERO) > 0){
                record.put(ExtDeliveryNoteDetail::getExtDetailStatus, ExtDeliveryNoteDetailStatusEnum.PART_RECEIPT);
            }

            // 入库或退货时间
            if (storageFlag) {
                // 单据计算入库数量
                BigDecimal storageQty = BigDecimalUtil.add(e.get(ExtDeliveryNoteDetail::getExtStorageQty), qty);
                record.put(ExtDeliveryNoteDetail::getExtStorageQty, storageQty);
                record.put(ExtDeliveryNoteDetail::getExtStorageTime, new Date());
            }else{
                record.put(ExtDeliveryNoteDetail::getExtReceiveTime, new Date());
            }
            return record;
        }).collect(Collectors.toList());
        qlService.update(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType(), deliveryRecords);

        // 更新送货单状态
        List<Long> deliveryNoteIds = deliveryNoteDetails.stream().map(e -> e.get(DeliveryNoteDetail::getDeliveryNoteId)).distinct().collect(Collectors.toList());
        extDeliveryNoteService.updateDeliveryNoteExtStatus(deliveryNoteIds);

        // 回写订单入库数量
        Map<Long, BigDecimal> orderMap = deliveryNoteDetails.stream().collect(Collectors.toMap(e -> e.get(DeliveryNoteDetail::getOrderDetailId), e -> deliveryReceiveMap.get(e.get(DeliveryNoteDetail::getDeliveryNoteDetailId)), (v1, v2) -> BigDecimalUtil.add(v1, v2)));
        List<Record> orderDetails = qlService.readByKeys(PurchaseSchemaEnum.ORDER_DETAIL.getType(), new ArrayList(orderMap.keySet()), Record.class);
        orderDetails.forEach(e -> {
            BigDecimal storageNum = BigDecimalUtil.add( e.get(OrderDetail::getStorageNum), orderMap.get(e.get(OrderDetail::getOrderDetailId)));
            e.set(OrderDetail::getStorageNum, storageNum);
            ExtOrderDetailStatusEnum extStatus = calcExtOrderDetailStatus(e);
            List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                    .set(ExtOrderDetail::getStorageNum, storageNum)
                    .set(ExtOrderDetail::getExtDetailStatus, extStatus)
                    .set(OrderDetail::getVersion, e.get(OrderDetail::getVersion) + 1)
                    .eq(OrderDetail::getOrderDetailId, e.get(OrderDetail::getOrderDetailId))
                    .eq(OrderDetail::getVersion, e.get(OrderDetail::getVersion)));
            Assert.notEmpty(ids, "数据已发生变化，请重试");
        });

        // 更新订单状态
        List<Long> orderIds = orderDetails.stream().map(e -> e.get(OrderDetail::getOrderId)).collect(Collectors.toList());
        updateExtOrderStatus(orderIds, null);
    }

    private Map<String, DictItemDTO> getDictItemMap(String dictCode){
        return baseClient.listAllByDictCode(dictCode).stream().collect(Collectors.toMap(DictItemDTO::getDictItemCode, Function.identity()));
    }

    /**
     * 返回分页列表
     * @param allData
     * @param tClass
     * @param page
     * @param pageSize
     * @param <T>
     * @return
     */
    private <T> List<T> getLimitList(List<T> allData,Class<T> tClass,int page,int pageSize){
        int total =  allData.size();
        if(allData.size()<pageSize){
            return allData;
        } else {
            int pageNum = total%pageSize == 0?total/pageSize:total/pageSize+1;
            int fromIndex = (page-1)*pageSize;
            if(pageNum==page){
                return allData.subList(fromIndex,total);
            } else {
                return allData.subList(fromIndex,fromIndex+pageSize);
            }
        }

    }

    private void setNullPage(BasePage basePage){
        if(ObjectUtil.isNull(basePage.getPageNum())){
            basePage.setPageNum(1);
        }
        if(ObjectUtil.isNull(basePage.getPageSize())){
            basePage.setPageSize(15);
        }
    }

}
