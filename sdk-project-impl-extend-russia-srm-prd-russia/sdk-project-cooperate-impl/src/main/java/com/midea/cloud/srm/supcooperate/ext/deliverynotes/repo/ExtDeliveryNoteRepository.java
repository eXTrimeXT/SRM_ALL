package com.midea.cloud.srm.supcooperate.ext.deliverynotes.repo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.order.DeliveryNoteStatus;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryFilter;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNote;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNoteDetail;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.enums.DeliveryNoteSource;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.OrderDetailStatus;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNote;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNoteDetail;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNoteSaveDTO;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.enums.ExtDeliveryNoteDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.service.ExtDeliveryNoteService;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.deliverynote.repo.DeliveryNoteRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import com.midea.cloud.srm.supcooperate.order.service.IDeliveryNoteDetailService;
import com.midea.cloud.srm.supcooperate.order.service.IDeliveryNoteService;
import com.midea.cloud.srm.supcooperate.order.service.IOrderDetailService;
import com.midea.cloud.srm.supcooperate.order.service.IOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Component
public class ExtDeliveryNoteRepository extends PurchaseRepository<ExtDeliveryNote> {

    public ExtDeliveryNoteRepository() {
        super("DeliveryNote", "deliveryNoteId", "送货单");
        this.register("getDetail", this::getDetail, false, "查详情");

        this.register("extSaveOrUpdate", this::extSaveOrUpdate, true, "保存");
        this.register("extCreateByOrder", this::extCreateByOrder, true, "选择订单创建");
        this.register("extCancel", this::extCancel, true, "取消发货");
    }

    @Autowired
    private DeliveryNoteRepository deliveryNoteRepository;
    @Autowired
    private ExtDeliveryNoteService extDeliveryNoteService;
    @Autowired
    private ExtOrderService extOrderService;
    @Autowired
    private IDeliveryNoteDetailService deliveryNoteDetailService;
    @Autowired
    private IDeliveryNoteService deliveryNoteService;
    @Autowired
    private IOrderDetailService orderDetailService;
    @Autowired
    private IOrderService orderService;

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
            QueryParam param = MeiQl.toValue(queryAction.getPayload(), QueryParam.class);
            QueryFilter qf = param.getFilter();
            if (qf != null) {
                Object poc = qf.get("purchaseOrderCode");
                if (poc != null) {
                    Set<Long> dIds = new HashSet<>();
                    List<Order> orderList = orderService.list(new LambdaQueryWrapper<Order>().like(Order::getOrderNumber, poc));
                    if (CollectionUtils.isNotEmpty(orderList)) {
                        List<Long> orderIds = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
                        List<OrderDetail> orderDetailList = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().in(OrderDetail::getOrderId, orderIds));
                        if (CollectionUtils.isNotEmpty(orderDetailList)) {
                            List<Long> orderDetailIds = orderDetailList.stream().map(OrderDetail::getOrderDetailId).collect(Collectors.toList());
                            dIds = deliveryNoteDetailService.list(new LambdaQueryWrapper<DeliveryNoteDetail>().in(DeliveryNoteDetail::getOrderDetailId, orderDetailIds)).stream().map(DeliveryNoteDetail::getDeliveryNoteId).collect(Collectors.toSet());
                        }
                    }
                    dIds.add(-1L);
                    qlCondition.in(ExtDeliveryNote::getDeliveryNoteId, dIds);
                }
            }

        }
        return qlCondition;
    }


    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);
        records.stream().forEach(e -> {
            if (e.get(ExtDeliveryNote::getExtStatus) == null) {
                e.put(ExtDeliveryNote::getExtStatus, e.get(ExtDeliveryNote::getDeliveryNoteStatus));
            }
        });
    }

    @Override
    protected void beforeDelete(QlQueryAction queryAction, Collection<Record> records) {
        Record record = readByRecord(getRecord(queryAction));
        Assert.isTrue(DeliveryNoteStatus.CREATE.name().equals(record.get(DeliveryNote::getDeliveryNoteStatus)), "当前状态不能删除");
        List<Record> deliveryNoteDetails = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                .eq(DeliveryNoteDetail::getDeliveryNoteId, record.get(DeliveryNote::getDeliveryNoteId)), Record.class);
        extDeliveryNoteService.releaseOrder(deliveryNoteDetails);
        super.beforeDelete(queryAction, records);
    }

    /**
     * 选择订单创建
     * [{"orderId":1}]
     */
    private QlResult extCreateByOrder(QlQueryAction action) {
        Record record = getRecord(action);
        Long orderId = record.get(Order::getOrderId);
        Assert.notNull(orderId, "采购订单id不能为空");
        Record order = qlService.readByKey(PurchaseSchemaEnum.ORDER.getType(), orderId, Record.class);
        Assert.notNull(order, "采购订单不存在");
        Assert.isTrue(PurchaseOrderEnum.APPROVED == order.get(Order::getOrderStatus), "订单未生效不能创建送货单");
        Assert.isTrue(!ExtOrderStatusEnum.FINISHED.name().equals(order.get(ExtOrder::getExtStatus)), "订单已完成不能创建送货单");
        List<Record> orderDetails = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                        .eq(OrderDetail::getOrderId, orderId), Record.class)
                .stream().filter(e -> !ExtOrderDetailStatusEnum.FINISHED.name().equals(e.get(ExtOrderDetail::getExtDetailStatus))
                        && OrderDetailStatus.CLOSED != e.get(ExtOrderDetail::getOrderDetailStatus)
                        && e.get(OrderDetail::getOrderNum).compareTo(e.get(OrderDetail::getReceiveSum)) > 0)
                .collect(Collectors.toList());
        Assert.notEmpty(orderDetails, "该订单不存在有效明细数据，不能创建送货单");

        // 送货单头
        Record deliveryNote = new Record();
        deliveryNote.set(DeliveryNote::getDeliveryNumber, baseClient.seqGen(SequenceCodeConstant.SEQ_SSC_DELIVERY_NOTE_NUM));
        deliveryNote.set(DeliveryNote::getDeliveryNoteStatus, DeliveryNoteStatus.CREATE.name());
        deliveryNote.set(DeliveryNote::getOrderSource, DeliveryNoteSource.PURCHASE_ORDER);
        deliveryNote.set(DeliveryNote::getOrgCode, order.get(ExtOrder::getCeeaOrgCode));
        deliveryNote.set(DeliveryNote::getOrgId, order.get(ExtOrder::getCeeaOrgId));
        deliveryNote.set(DeliveryNote::getOrgName, order.get(ExtOrder::getCeeaOrgName));
        deliveryNote.set(DeliveryNote::getOrganizationCode, "");
        deliveryNote.set(DeliveryNote::getOrganizationId, -1L);
        deliveryNote.set(DeliveryNote::getOrganizationName, "");
        deliveryNote.set(DeliveryNote::getVendorCode, order.get(ExtOrder::getVendorCode));
        deliveryNote.set(DeliveryNote::getVendorId, order.get(ExtOrder::getVendorId));
        deliveryNote.set(DeliveryNote::getVendorName, order.get(ExtOrder::getVendorName));
        deliveryNote.set(ExtDeliveryNote::getExtVendorContacts, order.get(ExtOrder::getExtVendorContacts));
        deliveryNote.set(ExtDeliveryNote::getExtVendorPhone, order.get(ExtOrder::getExtVendorPhone));
        deliveryNote.set(DeliveryNote::getCeeaDeliveryPlace, order.get(ExtOrder::getReceiveAddress));
        deliveryNote.set(ExtDeliveryNote::getExtDepartmentCode, order.get(ExtOrder::getCeeaDepartmentCode));
        deliveryNote.set(ExtDeliveryNote::getExtDepartmentName, order.get(ExtOrder::getCeeaDepartmentName));
        deliveryNote.set(ExtDeliveryNote::getExtDepartmentId, order.get(ExtOrder::getCeeaDepartmentId));
        deliveryNote.set(ExtDeliveryNote::getExtApplicantCode, order.get(ExtOrder::getExtApplicantCode));
        deliveryNote.set(ExtDeliveryNote::getExtApplicantName, order.get(ExtOrder::getExtApplicantName));
        deliveryNote.set(ExtDeliveryNote::getExtReceiveContact, order.get(ExtOrder::getReceiveContact));
        deliveryNote.set(ExtDeliveryNote::getExtReceiveTelephone, order.get(ExtOrder::getReceiveTelephone));
        deliveryNote.set(ExtDeliveryNote::getCeeaDeliveryPlace, order.get(ExtOrder::getReceiveAddress));
        deliveryNote.set(DeliveryNote::getDeliveryDate, new Date());
        deliveryNote.set(ExtDeliveryNote::getExtApplyDate, new Date());
        deliveryNote.set(ExtDeliveryNote::getExtCurrencyCode, order.get(ExtOrder::getRfqSettlementCurrency));
        deliveryNote.set(ExtDeliveryNote::getExtPurchaserId, order.get(ExtOrder::getCeeaEmpUserId));
        deliveryNote.set(ExtDeliveryNote::getExtPurchaserName, order.get(ExtOrder::getCeeaEmpUsername));
        deliveryNote.set(ExtDeliveryNote::getExtPurchaserNo, order.get(ExtOrder::getCeeaEmpNo));
        deliveryNote.set(ExtDeliveryNote::getExtPurchaserEmail, order.get(ExtOrder::getExtPurchaserEmail));
        deliveryNote.set(ExtDeliveryNote::getExtPurchaserPhone, order.get(ExtOrder::getExtPurchaserPhone));
        deliveryNote.set(ExtDeliveryNote::getExtPurchaserOrgName, order.get(ExtOrder::getExtPurchaserOrgName));

        // 送货单明细
        AtomicReference<BigDecimal> noTaxTotalAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> inTaxTotalAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicInteger lineNum = new AtomicInteger(1);
        List<Record> deliveryNoteDetails = orderDetails.stream().map(e -> {
            BigDecimal deliveryQty = BigDecimalUtil.sub(e.get(ExtOrderDetail::getOrderNum), e.get(ExtOrderDetail::getReceiveSum));
            BigDecimal noTaxAmount = BigDecimalUtil.mul(e.get(ExtOrderDetail::getCeeaUnitNoTaxPrice), deliveryQty);
            BigDecimal inTaxAmount = BigDecimalUtil.mul(e.get(ExtOrderDetail::getCeeaUnitTaxPrice), deliveryQty);

            Record deliveryNoteDetail = new Record();
            deliveryNoteDetail.set(DeliveryNoteDetail::getOrderDetailId, e.get(ExtOrderDetail::getOrderDetailId));
            deliveryNoteDetail.set(DeliveryNoteDetail::getOrderSource, DeliveryNoteSource.PURCHASE_ORDER);
            deliveryNoteDetail.set(DeliveryNoteDetail::getLineNum, lineNum.getAndIncrement());
            deliveryNoteDetail.set(DeliveryNoteDetail::getDeliveryQuantity, deliveryQty);
            deliveryNoteDetail.set(ExtDeliveryNoteDetail::getExtDetailStatus, ExtDeliveryNoteDetailStatusEnum.CREATE);
            noTaxTotalAmount.set(BigDecimalUtil.add(noTaxAmount, noTaxTotalAmount.get()));
            inTaxTotalAmount.set(BigDecimalUtil.add(inTaxAmount, inTaxTotalAmount.get()));
            return deliveryNoteDetail;
        }).collect(Collectors.toList());
        Assert.notEmpty(deliveryNoteDetails, "送货单明细不能为空");

        // 保存数据
        deliveryNote.set(ExtDeliveryNote::getExtInTaxAmount, inTaxTotalAmount);
        deliveryNote.set(ExtDeliveryNote::getExtUnTaxAmount, noTaxTotalAmount);
        List<Serializable> deliveryNoteIds = qlService.create(PurchaseSchemaEnum.DELIVERY_NOTE.getType(), Arrays.asList(deliveryNote));
        deliveryNote.set(DeliveryNote::getDeliveryNoteId, Long.parseLong(deliveryNoteIds.get(0).toString()));
        deliveryNoteDetails.stream().forEach(e -> e.set(DeliveryNoteDetail::getDeliveryNoteId, deliveryNoteIds.get(0)));
        qlService.create(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType(), deliveryNoteDetails);

        // 回写订单
        orderDetails.forEach(e -> {
            List<Serializable> orderDetailIds = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                    .set(ExtOrderDetail::getVersion, e.get(OrderDetail::getVersion) + 1)
                    .set(ExtOrderDetail::getExtDetailStatus, ExtOrderDetailStatusEnum.ONGOING)
                    .set(ExtOrderDetail::getReceiveSum, e.get(OrderDetail::getOrderNum))
                    .set(ExtOrderDetail::getDeliveryNoticeQuantity, e.get(OrderDetail::getOrderNum))
                    .eq(ExtOrderDetail::getOrderDetailId, e.get(OrderDetail::getOrderDetailId))
                    .eq(ExtOrderDetail::getVersion, e.get(OrderDetail::getVersion)));
            Assert.notEmpty(orderDetailIds, "数据已发生变化，请重试");
        });
        List<Serializable> orderIds = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER.getType())
                .set(ExtOrder::getVersion, order.get(ExtOrder::getVersion) + 1)
                .set(ExtOrder::getExtStatus, ExtOrderStatusEnum.ONGOING)
                .eq(ExtOrder::getOrderId, order.get(ExtOrder::getOrderId))
                .eq(ExtOrder::getVersion, order.get(ExtOrder::getVersion)));
        Assert.notEmpty(orderIds, "数据已发生变化，请重试");

        return ResultUtil.build(action, primaryKey, Arrays.asList(deliveryNote), false);
    }

    private QlResult extCancel(QlQueryAction action) {
        Record record = getRecord(action);
        Long deliveryNoteId = record.get(DeliveryNote::getDeliveryNoteId);
        String cancelReason = record.get(ExtDeliveryNoteDetail::getExtCancelReason);
        Assert.notNull(deliveryNoteId, "送货单id不能为空");
        List<Record> deliveryNoteDetails = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                .eq(DeliveryNoteDetail::getDeliveryNoteId, deliveryNoteId), Record.class);
        extDeliveryNoteService.cancelDeliveryDetails(deliveryNoteDetails, cancelReason);
        return QlResult.empty();
    }

    public QlResult extSaveOrUpdate(QlQueryAction action) {
        List<Record> records = getRecords(action);
        Record record = getRecord(records);
        ExtDeliveryNoteSaveDTO deliveryNoteSaveDTO = BeanCopyUtil.convertWithExtensions(record, ExtDeliveryNoteSaveDTO.class);
        if (deliveryNoteSaveDTO.getDeliveryNoteId() == null) {
            String deliveryNoteNumber = baseClient.seqGen(SequenceCodeConstant.SEQ_SSC_DELIVERY_NOTE_NUM);
            deliveryNoteSaveDTO.setDeliveryNumber(deliveryNoteNumber);
            deliveryNoteSaveDTO.setDeliveryNoteStatus(DeliveryNoteStatus.CREATE.name());
        } else {
            Record deliveryNoteDTO = this.readByKey(deliveryNoteSaveDTO.getDeliveryNoteId());
            deliveryNoteSaveDTO.setDeliveryNumber(deliveryNoteDTO.get(DeliveryNote::getDeliveryNumber));
        }

        AtomicLong i = new AtomicLong(1L);
        List<ExtDeliveryNoteDetail> detailList = PurchaseMqlUtils.trimDeleteFlag(deliveryNoteSaveDTO.getDetailList());
        detailList.forEach((e) -> {
            e.setLineNum(i.getAndIncrement());
            e.setExtDetailStatus(deliveryNoteSaveDTO.getDeliveryNoteStatus());
        });

        // 回写订单
        Map<Long, BigDecimal> orderDeliveryQtyMap = detailList.stream()
                .collect(Collectors.toMap(e -> e.getOrderDetailId(), e -> e.getDeliveryQuantity(), (v1, v2) -> BigDecimalUtil.add(v1, v2)));
        if (deliveryNoteSaveDTO.getDeliveryNoteId() != null) {
            List<Record> deliveryDetailRecords = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                    .eq(DeliveryNoteDetail::getDeliveryNoteId, deliveryNoteSaveDTO.getDeliveryNoteId()), Record.class);
            deliveryDetailRecords.forEach(e -> {
                Long orderDetailId = e.get(DeliveryNoteDetail::getOrderDetailId);
                BigDecimal newQty = orderDeliveryQtyMap.get(orderDetailId);
                BigDecimal oldQty = e.get(DeliveryNoteDetail::getDeliveryQuantity);
                if (newQty == null) {
                    orderDeliveryQtyMap.put(orderDetailId, BigDecimalUtil.sub(BigDecimal.ZERO, oldQty));
                } else {
                    orderDeliveryQtyMap.put(orderDetailId, BigDecimalUtil.sub(newQty, oldQty));
                }
            });
        }
        List<Record> orderDetails = qlService.readByKeys(PurchaseSchemaEnum.ORDER_DETAIL.getType(),
                new ArrayList(orderDeliveryQtyMap.keySet()), Record.class);
        orderDetails.forEach(e -> {
            BigDecimal changeQty = orderDeliveryQtyMap.get(e.get(OrderDetail::getOrderDetailId));
            e.set(OrderDetail::getReceiveSum, BigDecimalUtil.add(e.get(OrderDetail::getReceiveSum), changeQty));
            e.set(OrderDetail::getDeliveryNoticeQuantity, BigDecimalUtil.add( e.get(OrderDetail::getDeliveryNoticeQuantity), changeQty));
            ExtOrderDetailStatusEnum status = extOrderService.calcExtOrderDetailStatus(e);
            List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                    .set(ExtOrderDetail::getExtDetailStatus, status)
                    .set(ExtOrderDetail::getReceiveSum, e.get(OrderDetail::getReceiveSum))
                    .set(ExtOrderDetail::getDeliveryNoticeQuantity, e.get(OrderDetail::getDeliveryNoticeQuantity))
                    .set(OrderDetail::getVersion, e.get(OrderDetail::getVersion) + 1)
                    .eq(OrderDetail::getOrderDetailId, e.get(OrderDetail::getOrderDetailId))
                    .eq(OrderDetail::getVersion, e.get(OrderDetail::getVersion)));
            Assert.notEmpty(ids, "数据已发生变化，请重试");
        });

        // 修改订单头状态
        List<Long> orderIds = orderDetails.stream().map(e -> e.get(OrderDetail::getOrderId)).distinct().collect(Collectors.toList());
        extOrderService.updateExtOrderStatus(orderIds, null);

        // 保存送货单
        record.putAll(MeiQl.toValue(deliveryNoteSaveDTO, Record.class));
        QlResult result = super.doSave(action, records);

        // 同步到EAS
        if (DeliveryNoteStatus.DELIVERED.name().equals(deliveryNoteSaveDTO.getDeliveryNoteStatus())) {
            Long resultId = (Long)(result.getRef().get("DeliveryNoteVendor")).keySet().iterator().next();
            Record deliveryNote = qlService.readByKey(PurchaseSchemaEnum.DELIVERY_NOTE.getType(), resultId, Record.class);
            List<Record> deliveryNoteDetails = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                    .eq(DeliveryNoteDetail::getDeliveryNoteId, resultId), Record.class);
            extDeliveryNoteService.syncEas(deliveryNote, deliveryNoteDetails, true);
        }

        return result;
    }

    private QlResult getDetail(QlQueryAction qlQueryAction) {
        return deliveryNoteRepository.getDetail(qlQueryAction);
    }
}
