package com.midea.cloud.srm.supcooperate.ext.deliverynotes.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.order.DeliveryNoteStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.mall.result.jd.Order.DeliveryResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderDetailNoneChildResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderDetailResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.UpdatePoNoResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.excel.DeliveryNoteDetailExcel;
import com.midea.cloud.srm.model.supcooperate.excel.DeliveryNoteExcel;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNote;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNoteDetail;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.enums.DeliveryNoteSource;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.*;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.enums.ExtDeliveryNoteDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.enums.ExtDeliveryNoteStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.mapper.ExtDeliveryNoteMapper;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.service.ExtDeliveryNoteService;
import com.midea.cloud.srm.supcooperate.ext.order.PurchaseUtils;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.dto.JDOrderDetailRequestDTO;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderProperty;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import com.midea.cloud.srm.supcooperate.inq.ext.excelhandler.conf.ExtInqEnumsCellWriteHandler;
import com.midea.cloud.srm.supcooperate.inq.ext.excelhandler.conf.ExtInqSpinnerWriteHandler;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Service
public class ExtDeliveryNoteServiceImpl implements ExtDeliveryNoteService {

    @Autowired
    private QlService qlService;
    @Autowired
    private ExtOrderService extOrderService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private MallService mallService;
    @Autowired
    private ExternalMaterialService externalMaterialService;
    @Autowired
    protected BaseClient baseClient;
    @Autowired
    protected ExtDeliveryNoteMapper extDeliveryNoteMapper;

    @Override
    public void cancelDeliveryDetails(List<Record> deliveryNoteDetails, String cancelReason) {
        deliveryNoteDetails = deliveryNoteDetails.stream()
                .filter(e->!ExtDeliveryNoteDetailStatusEnum.FINISHED.name().equals(e.get(ExtDeliveryNoteDetail::getExtDetailStatus))
                    && !ExtDeliveryNoteDetailStatusEnum.CANCELLED.name().equals(e.get(ExtDeliveryNoteDetail::getExtDetailStatus)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deliveryNoteDetails)) {
            return;
        }

        // 回写订单
        releaseOrder(deliveryNoteDetails);

        // 更新送货单
        Map<Long, Record> detailMap = deliveryNoteDetails.stream().collect(Collectors.toMap(e -> e.get(ExtDeliveryNoteDetail::getDeliveryNoteDetailId), Function.identity(), (k1, k2) -> k2));
        List<Record> updateDeliveryNoteDetails = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                .in(ExtDeliveryNoteDetail::getDeliveryNoteDetailId, deliveryNoteDetails.stream().map(e -> e.get(ExtDeliveryNoteDetail::getDeliveryNoteDetailId)).distinct().collect(Collectors.toList())), Record.class);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(updateDeliveryNoteDetails)) {
            updateDeliveryNoteDetails.stream().forEach(e -> {
                Record deliverNoteDetail = detailMap.get(e.get(ExtDeliveryNoteDetail::getDeliveryNoteDetailId));

                if(Long.compare(ObjectUtils.defaultIfNull(deliverNoteDetail.get(ExtDeliveryNoteDetail::getVersion), SrmConstant.LONG_ZERO),
                ObjectUtils.defaultIfNull(e.get(ExtDeliveryNoteDetail::getVersion), SrmConstant.LONG_ZERO)) != 0) {
                    throw new BaseException("数据已发生变化，请重试");
                }

                BigDecimal cancelQty = BigDecimalUtil.sub(deliverNoteDetail.get(DeliveryNoteDetail::getDeliveryQuantity), deliverNoteDetail.get(ExtDeliveryNoteDetail::getWarehouseQuantity));
                deliverNoteDetail.set(ExtDeliveryNoteDetail::getExtCancelQty, cancelQty);// 推送用到
                deliverNoteDetail.set(ExtDeliveryNoteDetail::getExtCancelReason, cancelReason);// 推送用到
                if(ObjectUtils.defaultIfNull(e.get(ExtDeliveryNoteDetail::getWarehouseQuantity), BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 1) {
                    e.put(ExtDeliveryNoteDetail::getExtDetailStatus, ExtDeliveryNoteDetailStatusEnum.FINISHED);
                } else {
                    e.put(ExtDeliveryNoteDetail::getExtDetailStatus, ExtDeliveryNoteDetailStatusEnum.CANCELLED);
                }
                e.put(ExtDeliveryNoteDetail::getExtCancelQty, cancelQty);
                e.put(ExtDeliveryNoteDetail::getExtCancelReason, cancelReason);
                e.put(ExtDeliveryNoteDetail::getVersion, PurchaseUtils.increaseVersion(e.get(ExtDeliveryNoteDetail::getVersion)));
            });
            qlService.update(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType(), updateDeliveryNoteDetails);
        }

        // 更新头状态
        List<Long> deliveryNoteIds = deliveryNoteDetails.stream().map(e -> e.get(DeliveryNoteDetail::getDeliveryNoteId)).distinct().collect(Collectors.toList());
        List<Record> deliveryNotes = updateDeliveryNoteExtStatus(deliveryNoteIds);

        // 同步到EAS
        Map<Long, List<Record>> deliveryNoteDetailMap = deliveryNoteDetails.stream().collect(Collectors.groupingBy(e -> e.get(DeliveryNoteDetail::getDeliveryNoteId)));
        deliveryNotes.forEach(e -> {
            syncEas(e, deliveryNoteDetailMap.get(e.get(DeliveryNoteDetail::getDeliveryNoteId)), false);
        });
    }

    @Override
    public List<Record> updateDeliveryNoteExtStatus(List<Long> deliveryNoteIds) {
        List<Record> deliveryNotes = qlService.readByKeys(PurchaseSchemaEnum.DELIVERY_NOTE.getType(), deliveryNoteIds, Record.class);
        Map<Long, List<Record>> deliveryDetailMap = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                .in(DeliveryNote::getDeliveryNoteId, deliveryNoteIds), Record.class)
                .stream().collect(Collectors.groupingBy(e->e.get(ExtDeliveryNoteDetail::getDeliveryNoteId)));
        deliveryNotes.forEach(deliveryNote -> {
            Long deliveryNoteId = deliveryNote.get(DeliveryNote::getDeliveryNoteId);
            List<Record> deliveryNoteDetails = deliveryDetailMap.get(deliveryNoteId);
            long finishCount = deliveryNoteDetails.stream().filter(e -> ExtDeliveryNoteDetailStatusEnum.FINISHED.name().equals(e.get(ExtDeliveryNoteDetail::getExtDetailStatus))).count();
            long cancelCount = deliveryNoteDetails.stream().filter(e -> ExtDeliveryNoteDetailStatusEnum.CANCELLED.name().equals(e.get(ExtDeliveryNoteDetail::getExtDetailStatus))).count();
            if (cancelCount == deliveryNoteDetails.size()) {
                List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.DELIVERY_NOTE.getType())
                        .set(ExtDeliveryNote::getExtStatus, null)
                        .set(ExtDeliveryNote::getDeliveryNoteStatus, DeliveryNoteStatus.CANCELLED)
                        .set(ExtDeliveryNote::getVersion, PurchaseUtils.increaseVersion(deliveryNote.get(ExtDeliveryNote::getVersion)))
                        .eq(ExtDeliveryNote::getDeliveryNoteId, deliveryNote.get(ExtDeliveryNote::getDeliveryNoteId))
                        .eq(ExtDeliveryNote::getVersion, deliveryNote.get(ExtDeliveryNote::getVersion)));
                Assert.notEmpty(ids, "数据已发生变化，请重试");
            } else if (finishCount + cancelCount == deliveryNoteDetails.size()) {
                List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.DELIVERY_NOTE.getType())
                        .set(ExtDeliveryNote::getExtStatus, ExtDeliveryNoteStatusEnum.FINISHED)
                        .set(ExtDeliveryNote::getVersion, PurchaseUtils.increaseVersion(deliveryNote.get(ExtDeliveryNote::getVersion)))
                        .eq(ExtDeliveryNote::getDeliveryNoteId, deliveryNoteId)
                        .eq(ExtDeliveryNote::getVersion, deliveryNote.get(ExtDeliveryNote::getVersion)));
                Assert.notEmpty(ids, "数据已发生变化，请重试");
            }
        });
        return deliveryNotes;
    }

    @Override
    public void releaseOrder(List<Record> deliveryNoteDetails) {
        if (CollectionUtils.isEmpty(deliveryNoteDetails)) {
            return;
        }

        Map<Long, BigDecimal> orderReleaseQtyMap = new HashMap<>(50);
        deliveryNoteDetails.forEach(e -> {
            BigDecimal deliveryQty = e.get(DeliveryNoteDetail::getDeliveryQuantity);
            BigDecimal storageQty = e.get(ExtDeliveryNoteDetail::getWarehouseQuantity);
            BigDecimal releaseQty = BigDecimalUtil.sub(deliveryQty, storageQty);
            orderReleaseQtyMap.compute(e.get(DeliveryNoteDetail::getOrderDetailId), (k, v) -> BigDecimalUtil.add(v, releaseQty));
        });
        List<Record> orderDetails = qlService.readByKeys(PurchaseSchemaEnum.ORDER_DETAIL.getType(), new ArrayList(orderReleaseQtyMap.keySet()), Record.class);
        orderDetails.forEach(e -> {
            // 释放数量，修改订单行状态
            BigDecimal releaseQty = orderReleaseQtyMap.getOrDefault(e.get(OrderDetail::getOrderDetailId), BigDecimal.ZERO);
            BigDecimal receiveSum = BigDecimalUtil.sub(e.get(OrderDetail::getReceiveSum), releaseQty);
            BigDecimal deliveryNoticeQuantity = BigDecimalUtil.sub(e.get(OrderDetail::getDeliveryNoticeQuantity), releaseQty);
            if (receiveSum.compareTo(BigDecimal.ZERO) < 0) {
                throw new BaseException("取消送货单数据异常");
            }
            ExtOrderDetailStatusEnum status = extOrderService.calcExtOrderDetailStatus(e);
            List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                    .set(ExtOrderDetail::getExtDetailStatus, status)
                    .set(OrderDetail::getReceiveSum, receiveSum)
                    .set(OrderDetail::getDeliveryNoticeQuantity, deliveryNoticeQuantity)
                    .set(OrderDetail::getVersion, e.get(OrderDetail::getVersion) + 1)
                    .eq(OrderDetail::getOrderDetailId, e.get(OrderDetail::getOrderDetailId))
                    .eq(OrderDetail::getVersion, e.get(OrderDetail::getVersion)));
            Assert.notEmpty(ids, "数据已发生变化，请重试");
        });

        // 修改订单头状态
        List<Long> orderIds = orderDetails.stream().map(e -> e.get(OrderDetail::getOrderId)).distinct().collect(Collectors.toList());
        extOrderService.updateExtOrderStatus(orderIds, null);
    }

    @Override
    public void syncEas(Record deliveryNote, List<Record> deliveryNoteDetails, boolean delivery) {
        List<Long> orderDetailIds = deliveryNoteDetails.stream().map(e -> e.get(DeliveryNoteDetail::getOrderDetailId)).distinct().collect(Collectors.toList());
        Map<Long, Record> orderDetailMap = qlService.readByKeys(PurchaseSchemaEnum.ORDER_DETAIL.getType(), orderDetailIds, Record.class)
                .stream().collect(Collectors.toMap(e -> e.get(OrderDetail::getOrderDetailId), e -> e));
        List<Long> orderIds = orderDetailMap.values().stream().map(e -> e.get(OrderDetail::getOrderId)).distinct().collect(Collectors.toList());
        Map<Long, Record> orderMap = qlService.readByKeys(PurchaseSchemaEnum.ORDER.getType(), orderIds, Record.class)
                .stream().collect(Collectors.toMap(e -> e.get(Order::getOrderId), e -> e));
        List<Long> validOrderIds = orderMap.values().stream()
                .filter(e -> ExtOrderProperty.MATERIAL.name().equals(e.get(ExtOrder::getExtOrderProperty)))
                .map(e -> e.get(ExtOrder::getOrderId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validOrderIds)) {
            log.info("不存在实物类订单，无需推送EAS");
            return;
        }

        BigDecimal rate = BigDecimal.ONE;
        String currencyCode = deliveryNote.get(ExtDeliveryNote::getExtCurrencyCode);
//        if (!"CNY".equalsIgnoreCase(currencyCode) && !"RMB".equalsIgnoreCase(currencyCode)) {
//            List<PriceRate> rates = pjProjectExtClient.queryExchangeRate(currencyCode, "CNY", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
//            Assert.notEmpty(rates, "查询汇率失败");
//            rate = rates.get(0).getRate();
//        }

        AtomicReference<BigDecimal> noTaxTotal = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> inTaxTotal = new AtomicReference<>(BigDecimal.ZERO);
        List<Record> details = deliveryNoteDetails.stream().map(e -> {
            Record orderDetail = orderDetailMap.get(e.get(DeliveryNoteDetail::getOrderDetailId));
            Record order = orderMap.get(orderDetail.get(OrderDetail::getOrderId));
            BigDecimal qty = delivery ? e.get(DeliveryNoteDetail::getDeliveryQuantity) : e.get(ExtDeliveryNoteDetail::getExtCancelQty);
            BigDecimal noTaxAmount = BigDecimalUtil.mul(orderDetail.get(ExtOrderDetail::getCeeaUnitNoTaxPrice), qty);
            BigDecimal inTaxAmount = BigDecimalUtil.mul(orderDetail.get(ExtOrderDetail::getCeeaUnitTaxPrice), qty);
            noTaxTotal.set(BigDecimalUtil.add(noTaxTotal.get(), noTaxAmount));
            inTaxTotal.set(BigDecimalUtil.add(inTaxTotal.get(), inTaxAmount));

            Record record = new Record();
            record.put("FMATERIALID", orderDetail.get(OrderDetail::getMaterialCode));//物料编码
            record.put("FQTY", qty.doubleValue());//送货单行的本次送货数量
            record.put("FSTORAGEORGUNITID", orderDetail.get(ExtOrderDetail::getExtUseDepartmentCode) + "|" + orderDetail.get(ExtOrderDetail::getExtUseDepartmentName));//使用部门编码
            record.put("FPRICE", orderDetail.get(OrderDetail::getCeeaUnitNoTaxPrice).doubleValue());//送货单行的未税单价
            record.put("FTAXRATE", orderDetail.get(OrderDetail::getCeeaTaxRate).doubleValue());//送货单行的税率
            record.put("FTAXPRICE", orderDetail.get(OrderDetail::getCeeaUnitTaxPrice).doubleValue());//送货单行的含税单价
            record.put("FAMOUNT", noTaxAmount.doubleValue());//送货单行的总未税
            record.put("FTAX", BigDecimalUtil.sub(inTaxAmount, noTaxAmount).doubleValue());//送货单行的总税额
            record.put("FTAXAMOUNT", inTaxAmount.doubleValue());//送货单行的含税总额
            record.put("FDELIVERYDATE", orderDetail.get(OrderDetail::getDeliveryDate));//交货日期
            record.put("CFZINPUTCYC", orderDetail.get(ExtOrderDetail::getExtDeliveryCycle));//到货周期
            record.put("CFPROPOSERNUM", orderDetail.get(ExtOrderDetail::getExtUserCode) + "|" + orderDetail.get(ExtOrderDetail::getExtUserName));//送货单的使用人（工号）
            record.put("SEQ", e.get(DeliveryNoteDetail::getLineNum));//送货单行号
            record.put("ORDERBILL", order.get(Order::getOrderNumber));//订单号
            record.put("ORDERSEQ", orderDetail.get(OrderDetail::getLineNum));//订单行号
            record.put("WARRANTY", orderDetail.get(ExtOrderDetail::getExtWarrantyPeriod));//质保日期
            record.put("ENTRYSTATUS", delivery ? "01" : "02");//（01已发货/02已取消）
            record.put("REASON", e.get(ExtDeliveryNoteDetail::getExtCancelReason));//取消原因
            return record;
        }).collect(Collectors.toList());

        Record easHead = new Record();
        //业务实体
        easHead.put("FCONTROLUNITID", deliveryNote.get(DeliveryNote::getOrgCode));
        //创建人工号
        easHead.put("FCREATORID", orderMap.values().iterator().next().get(Order::getCeeaEmpNo));
        //发货单号
        easHead.put("FBILLNUMBER", deliveryNote.get(DeliveryNote::getDeliveryNumber));
        //送货单创建时间
        easHead.put("FBIZDATE", deliveryNote.get(DeliveryNote::getCreationDate));
        //送货单的业务实体
        easHead.put("FPURCHASEORGUNITID", deliveryNote.get(DeliveryNote::getOrgCode));
        //送货单的供应商编码
        easHead.put("FSUPPLIERID", deliveryNote.get(ExtDeliveryNote::getVendorCode));
        //送货单的币种编码
        easHead.put("FCURRENCYID", currencyCode);
        //送货单币种的月汇率
        easHead.put("FEXCHANGERATE", rate.toPlainString());
        //送货单的未税总额
        easHead.put("FTOTALAMOUNT", noTaxTotal.get().doubleValue());
        //送货单的税额
        easHead.put("FTOTALTAX", BigDecimalUtil.sub(inTaxTotal.get(), noTaxTotal.get()).doubleValue());
        //送货单的含税总额
        easHead.put("FTOTALTAXAMOUNT", inTaxTotal.get().doubleValue());
        //SRM送货单头的库存组织
        easHead.put("FSTORAGEORGUNITID", deliveryNote.get(DeliveryNote::getOrganizationCode));
        easHead.put("entries", details);
        log.info("pushDeliveryNote params: {}", JSONUtil.toJsonStr(easHead));
        pjProjectExtClient.pushDeliveryNote(easHead);
    }

    /**
     * 根据京东子订单号创建送货单
     * 给京东推送消息的定时任务调用
     *
     * 传递京东订单的子单号，查询物流信息，生产送货单
     * @param jdOrderIds 京东子单号
     */
    @Override
    public void createDeliveryNoteByJDOrderIds(List<String> jdOrderIds) {

        //记录查询的物流信息
        List<DeliveryResultDTO> deliveryResultDTOList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(jdOrderIds)) {
            //根据子单号获取物流信息
            deliveryResultDTOList = this.getDeliveryInfoByJdOrderIds(jdOrderIds);
        }

        //根据物流信息创建送货单
        if (!CollectionUtils.isEmpty(deliveryResultDTOList)) {
            /**
             * 根据物流信息创建送货单，参考逻辑 ExtDeliveryNoteRepository:extCreateByOrder
             */
            for (DeliveryResultDTO deliveryResultDTO : deliveryResultDTOList) {

                //根据京东订单id获取SRM订单信息
                //订单表只存放了父订单id，所以要用父订单id，若父订单id为0，则说明未拆单，任使用该订单号
                Long parentId = deliveryResultDTO.getResult().getWaybillCode().get(0).getParentId();
                List<ExtOrder> extOrderList = qlService.queryByWrapper(QlWrappers.query(ExtOrder.class)
                        .eq(ExtOrder::getExtJdOrderId, parentId == 0 ? deliveryResultDTO.getResult().getJdOrderId() : parentId), ExtOrder.class);

                ExtOrder order = new ExtOrder();
                //一个京东订单只会对应到一个SRM订单
                if (!CollectionUtils.isEmpty(extOrderList) && extOrderList.size() == 1) {
                    //获取SRM订单信息
                    order = extOrderList.get(0);
                    Assert.isTrue(PurchaseOrderEnum.APPROVED == order.getOrderStatus(), "订单未生效不能创建送货单");
                    Assert.isTrue(!ExtOrderStatusEnum.FINISHED.name().equals(order.getExtStatus()), "订单已完成不能创建送货单");

                    //查询京东订单详情，获取该京东订单中的物料明细
                    //组装入参对象
                    JDOrderDetailRequestDTO jdOrderDetailRequestDTO = new JDOrderDetailRequestDTO();
                    jdOrderDetailRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                    jdOrderDetailRequestDTO.setJdOrderId(order.getExtJdOrderId());
                    //调用查询京东订单详情接口，获取订单
                    OrderDetailResultDTO orderDetailResultDTO = mallService.queryOrderTetailInfo(jdOrderDetailRequestDTO);

                    //必定是子订单结构
                    OrderDetailNoneChildResultDTO orderDetailNoneChildResultDTO = new OrderDetailNoneChildResultDTO();
                    String type = orderDetailResultDTO.getResult().get("type").toString();
                    if (StringUtils.isNotEmpty(type) && "2".equals(type)) {
                        //type=2表示无子单的结构OrderDetailNoneChildResultDTO
                        orderDetailNoneChildResultDTO = JSON.parseObject(orderDetailResultDTO.getResult().toString(), OrderDetailNoneChildResultDTO.class);
                    } else {
                        throw new BaseException("京东订单:"+deliveryResultDTO.getResult().getJdOrderId()+"没有进行拆分");
                    }

                    //获取物料skuId
                    List<Long> skuIds = orderDetailNoneChildResultDTO.getSku().stream().map(OrderDetailNoneChildResultDTO.Sku::getSkuId).collect(Collectors.toList());

                    //根据skuId在 外部物料与系统物料映射表中 找到对应的SRM物料数据
                    LambdaQueryWrapper<ExternalMaterial> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.in(ExternalMaterial::getSkuId,skuIds);
                    queryWrapper.eq(ExternalMaterial::getMaterialType,"JD");
                    queryWrapper.eq(ExternalMaterial::getMappingFlag,"Y");
                    List<ExternalMaterial> externalMaterials = externalMaterialService.list(queryWrapper);
                    Assert.isTrue(CollectionUtils.isEmpty(externalMaterials),"京东订单:"+deliveryResultDTO.getResult().getJdOrderId()+"存在未映射物料");

                    //获取物料编码
                    List<String> materialCods = externalMaterials.stream().map(ExternalMaterial::getMaterialCode).collect(Collectors.toList());

                    //获取该京东订单物料对应的订单明细
                    List<ExtOrderDetail> extOrderDetails = qlService.queryByWrapper(QlWrappers.query(ExtOrderDetail.class)
                            .eq(ExtOrderDetail::getOrderId, order.getOrderId())
                            .in(ExtOrderDetail::getMaterialCode, materialCods), ExtOrderDetail.class);

                    //组装送货头
                    Record deliveryNote = new Record();
                    deliveryNote.set(DeliveryNote::getDeliveryNumber, baseClient.seqGen(SequenceCodeConstant.SEQ_SSC_DELIVERY_NOTE_NUM));
                    //状态设置为已发货
                    deliveryNote.set(DeliveryNote::getDeliveryNoteStatus, DeliveryNoteStatus.DELIVERED.name());
                    deliveryNote.set(DeliveryNote::getOrderSource, DeliveryNoteSource.PURCHASE_ORDER);
                    deliveryNote.set(DeliveryNote::getOrgCode, order.getCeeaOrgCode());
                    deliveryNote.set(DeliveryNote::getOrgId, order.getCeeaOrgId());
                    deliveryNote.set(DeliveryNote::getOrgName, order.getCeeaOrgName());
                    deliveryNote.set(DeliveryNote::getOrganizationCode, "");
                    deliveryNote.set(DeliveryNote::getOrganizationId, -1L);
                    deliveryNote.set(DeliveryNote::getOrganizationName, "");
                    deliveryNote.set(DeliveryNote::getVendorCode, order.getVendorCode());
                    deliveryNote.set(DeliveryNote::getVendorId, order.getVendorId());
                    deliveryNote.set(DeliveryNote::getVendorName, order.getVendorName());
                    //todo 京东单据带出的送货单，供应商联系人：京东；供应商联系电话：为空
                    //todo 京东的信息还没有确认，先确定位置，以后可能会改
                    deliveryNote.set(ExtDeliveryNote::getExtVendorContacts, "京东");
                    deliveryNote.set(ExtDeliveryNote::getExtVendorPhone, "");
                    //设置物流信息
                    /**
                     * 货运方式：第三方物流
                     * 快递单号：京东返回的运单号（若运单号为空则为京东子订单号），若是有多个子订单，返回了多个运单号，则运单号之间用“；”隔开；
                     */
                    deliveryNote.set(ExtDeliveryNote::getExtExpressType,"第三方物流");
                    //设置京东订单号
                    deliveryNote.set(ExtDeliveryNote::getExtJdOrderId,deliveryResultDTO.getResult().getJdOrderId());
                    //组装快递单号
                    StringBuffer extExpressNo = new StringBuffer();
                    for (DeliveryResultDTO.WaybillCode waybillCode : deliveryResultDTO.getResult().getWaybillCode()) {
                        extExpressNo.append(waybillCode.getDeliveryOrderId());
                        extExpressNo.append(";");
                    }
                    deliveryNote.set(ExtDeliveryNote::getExtExpressNo,extExpressNo);

                    deliveryNote.set(DeliveryNote::getCeeaDeliveryPlace, order.getReceiveAddress());
                    deliveryNote.set(ExtDeliveryNote::getExtDepartmentCode, order.getCeeaDepartmentCode());
                    deliveryNote.set(ExtDeliveryNote::getExtDepartmentName, order.getCeeaDepartmentName());
                    deliveryNote.set(ExtDeliveryNote::getExtDepartmentId, order.getCeeaDepartmentId());
                    deliveryNote.set(ExtDeliveryNote::getExtApplicantCode, order.getExtApplicantCode());
                    deliveryNote.set(ExtDeliveryNote::getExtApplicantName, order.getExtApplicantName());
                    deliveryNote.set(ExtDeliveryNote::getExtReceiveContact, order.getReceiveContact());
                    deliveryNote.set(ExtDeliveryNote::getExtReceiveTelephone, order.getReceiveTelephone());
                    deliveryNote.set(ExtDeliveryNote::getCeeaDeliveryPlace, order.getReceiveAddress());
                    deliveryNote.set(DeliveryNote::getDeliveryDate, new Date());
                    deliveryNote.set(ExtDeliveryNote::getExtApplyDate, new Date());
                    deliveryNote.set(ExtDeliveryNote::getExtCurrencyCode, order.getRfqSettlementCurrency());
                    deliveryNote.set(ExtDeliveryNote::getExtPurchaserId, order.getCeeaEmpUserId());
                    deliveryNote.set(ExtDeliveryNote::getExtPurchaserName, order.getCeeaEmpUsername());
                    deliveryNote.set(ExtDeliveryNote::getExtPurchaserNo, order.getCeeaEmpNo());
                    deliveryNote.set(ExtDeliveryNote::getExtPurchaserEmail, order.getExtPurchaserEmail());
                    deliveryNote.set(ExtDeliveryNote::getExtPurchaserPhone, order.getExtPurchaserPhone());
                    deliveryNote.set(ExtDeliveryNote::getExtPurchaserOrgName, order.getExtPurchaserOrgName());

                    // 送货单明细
                    AtomicReference<BigDecimal> noTaxTotalAmount = new AtomicReference<>(BigDecimal.ZERO);
                    AtomicReference<BigDecimal> inTaxTotalAmount = new AtomicReference<>(BigDecimal.ZERO);
                    AtomicInteger lineNum = new AtomicInteger(1);
                    List<Record> deliveryNoteDetails = extOrderDetails.stream().map(e -> {
                        BigDecimal deliveryQty = BigDecimalUtil.sub(e.getOrderNum(), e.getReceiveSum());
                        BigDecimal noTaxAmount = BigDecimalUtil.mul(e.getCeeaUnitNoTaxPrice(), deliveryQty);
                        BigDecimal inTaxAmount = BigDecimalUtil.mul(e.getCeeaUnitTaxPrice(), deliveryQty);

                        Record deliveryNoteDetail = new Record();
                        deliveryNoteDetail.set(DeliveryNoteDetail::getOrderDetailId, e.getOrderDetailId());
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
                    extOrderDetails.forEach(e -> {
                        List<Serializable> orderDetailIds = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                                .set(ExtOrderDetail::getVersion, e.getVersion() + 1)
                                .set(ExtOrderDetail::getExtDetailStatus, ExtOrderDetailStatusEnum.ONGOING)
                                .set(ExtOrderDetail::getReceiveSum, e.getOrderNum())
                                .set(ExtOrderDetail::getDeliveryNoticeQuantity, e.getOrderNum())
                                .eq(ExtOrderDetail::getOrderDetailId, e.getOrderDetailId())
                                .eq(ExtOrderDetail::getVersion, e.getVersion()));
                        Assert.notEmpty(orderDetailIds, "数据已发生变化，请重试");
                    });
                    List<Serializable> orderIds = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER.getType())
                            .set(ExtOrder::getVersion, order.getVersion() + 1)
                            .set(ExtOrder::getExtStatus, ExtOrderStatusEnum.ONGOING)
                            .eq(ExtOrder::getOrderId, order.getOrderId())
                            .eq(ExtOrder::getVersion, order.getVersion()));
                    Assert.notEmpty(orderIds, "数据已发生变化，请重试");

                    /**
                     * 创建完送货单调用订单-更新采购单号接口
                     */
                    //组装入参对象
                    JDSaveOrUpdatePoNoRequestDTO saveOrUpdatePoNoRequestDTO = new JDSaveOrUpdatePoNoRequestDTO();
                    saveOrUpdatePoNoRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                    saveOrUpdatePoNoRequestDTO.setJdOrderId(deliveryResultDTO.getResult().getJdOrderId());
                    saveOrUpdatePoNoRequestDTO.setPoNo(deliveryNote.get(DeliveryNote::getDeliveryNumber));

                    UpdatePoNoResultDTO updatePoNoResultDTO = mallService.saveOrUpdatePoNo(saveOrUpdatePoNoRequestDTO);

                    if (updatePoNoResultDTO.isSuccess()) {
                        log.info("更新京东订单:"+deliveryResultDTO.getResult().getJdOrderId()+"采购单号为:"+order.getOrderNumber()+"成功");
                    } else {
                        log.info("更新京东订单:"+deliveryResultDTO.getResult().getJdOrderId()+"采购单号为:"+order.getOrderNumber()+"失败");
                    }


                } else {
                    throw new BaseException("京东订单:"+deliveryResultDTO.getResult().getJdOrderId()+"与SRM订单对应关系异常");
                }
            }
        }
    }

    /**
     * 根据京东子单号获取物流信息
     */
    private List<DeliveryResultDTO> getDeliveryInfoByJdOrderIds(List<String> jdOrderIds){

        //记录查询的物流信息
        List<DeliveryResultDTO> deliveryResultDTOList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(jdOrderIds)){
            for (String jdOrderId : jdOrderIds) {
                //调用查询物流信息请求DTO
                JDDeliveryRequestDTO requestDTO = new JDDeliveryRequestDTO();
                //设置查询的京东子订单号
                requestDTO.setJdOrderId(jdOrderId);
                //设置查询配送信息
                requestDTO.setWaybillCode(1);
                //设置查询京东订单
                requestDTO.setMallType(MallTypeEnum.JD.getCode());

                //查询物流信息
                DeliveryResultDTO deliveryResultDTO = mallService.queryDeliveryInfo(requestDTO);
                if (deliveryResultDTO.isSuccess() && deliveryResultDTO.getResult() != null){
                    //查询成功且结果不为空，
                    //result：当查询不到配送信息时，此字段返回null。
                    deliveryResultDTOList.add(deliveryResultDTO);
                }
            }
        }
        return deliveryResultDTOList;
    }
    /**
     * 根查询条件导出送货单
     */
    @Override
    public void deliveryNotesUpload(HttpServletResponse response, QueryDeliveryNote queryDeliveryNote) throws IOException {
        //查询主表
        DeliveryNoteExcel deliveryNoteExcel=new DeliveryNoteExcel();
        BeanUtils.copyProperties(queryDeliveryNote,deliveryNoteExcel);
        List<DeliveryNoteExcel> excelList=getDeliveryNoteExcel(deliveryNoteExcel);
        if(excelList.size()==0){
            throw new BaseException("未查询到送检单");
        }
        if(excelList.size()>1000){
            throw new BaseException("导出数据过多，一次最多导出1000张送货单");
        }
        //查询子表
        List<DeliveryNoteDetailExcel> detailExcelList=getDeliveryNoteDetailExcel(excelList);
        //导出
        this.setExcelResponseProp(response, "送货单导出");
        ExcelWriter excelWriter=EasyExcel.write(response.getOutputStream()).build();
        WriteSheet writeOneSheet;
        WriteSheet writeTwoSheet;
        writeOneSheet=EasyExcel.writerSheet("送货单子表").head(DeliveryNoteDetailExcel.class)
                .registerWriteHandler(ExtInqEnumsCellWriteHandler.HANDLER)
                .registerWriteHandler(ExtInqSpinnerWriteHandler.HANDLER)
                .build();
        writeTwoSheet=EasyExcel.writerSheet("送货单主表").head(DeliveryNoteExcel.class)
                .registerWriteHandler(ExtInqEnumsCellWriteHandler.HANDLER)
                .registerWriteHandler(ExtInqSpinnerWriteHandler.HANDLER)
                .build();
        excelWriter.write(detailExcelList,writeOneSheet);
        excelWriter.write(excelList,writeTwoSheet);
        excelWriter.finish();
    }
    /**
     * 查询送货单主表
     */
    private List<DeliveryNoteExcel> getDeliveryNoteExcel(DeliveryNoteExcel queryDeliveryNote){
        List<DeliveryNoteExcel> orderDetailIds=new ArrayList<>();
        orderDetailIds = extDeliveryNoteMapper.deliveryList(queryDeliveryNote);
        return orderDetailIds;
    }
    /**
     * 查询送货单子表
     */
    private List<DeliveryNoteDetailExcel> getDeliveryNoteDetailExcel(List<DeliveryNoteExcel> extDeliveryNote){
        List<DeliveryNoteDetailExcel>extDeliveryNoteList=new ArrayList<>();

        List<Long> headIds = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(extDeliveryNote)) {
            headIds = extDeliveryNote.stream().map(DeliveryNoteExcel::getDeliveryNoteId).distinct().collect(Collectors.toList());
        }
        //1获取所有送货到子表
        List<Record> deliveryNotes = qlService.readByKeys(PurchaseSchemaEnum.DELIVERY_NOTE.getType(), headIds, Record.class);
        Map<Long, List<Record>> deliveryDetailMap = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE_DETAIL.getType())
                        .in(DeliveryNote::getDeliveryNoteId, headIds), Record.class)
                .stream().collect(Collectors.groupingBy(e->e.get(ExtDeliveryNoteDetail::getDeliveryNoteId)));
        //2循环子表
        deliveryNotes.forEach(deliveryNote -> {
            //3根据主表ID获取子表获取所有子表ID orderDetailCods
            Long deliveryNoteId = deliveryNote.get(DeliveryNote::getDeliveryNoteId);
            List<Record> deliveryNoteDetails = deliveryDetailMap.get(deliveryNoteId);
            List<ExtDeliveryNoteDetail> orderDetailIds=new ArrayList<>();
            orderDetailIds = OpenApiUtil.toListValue(deliveryNoteDetails, ExtDeliveryNoteDetail.class);
            List<Long> orderDetailCods =new ArrayList<>();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderDetailIds)) {
                orderDetailCods = orderDetailIds.stream().map(ExtDeliveryNoteDetail::getOrderDetailId).distinct().collect(Collectors.toList());
            }

            //4根据送货单子表明细ID 获取采购订单明细信息
            List<Record> details = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType()).in(OrderDetail::getOrderDetailId, orderDetailCods), Record.class);
            List<ExtOrderDetail> extOrderDetailList=new ArrayList<>();
            extOrderDetailList = OpenApiUtil.toListValue(details, ExtOrderDetail.class);
            List<Long> detailByMainIdCods =new ArrayList<>();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(details)) {
                detailByMainIdCods = extOrderDetailList.stream().map(ExtOrderDetail::getOrderId).distinct().collect(Collectors.toList());
            }
            //根据采购订单明细信息中主表ID，获取采购订单主表
            List<Record> orderIdsList=qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER.getType()).in(Order::getOrderId, detailByMainIdCods), Record.class);
            //拼装数据
            for(ExtDeliveryNoteDetail deliveryNoteDetailDTO:orderDetailIds){
                for(ExtOrderDetail orderDetail:extOrderDetailList){
                    if(orderDetail.getOrderDetailId().equals(deliveryNoteDetailDTO.getOrderDetailId())){
                        DeliveryNoteDetailExcel deliveryNoteDetailExcel=new DeliveryNoteDetailExcel();
                        deliveryNoteDetailExcel.setDeliveryNumber(deliveryNote.get(DeliveryNote::getDeliveryNumber));
                        deliveryNoteDetailExcel.setExtDetailStatus(deliveryNoteDetailDTO.getExtDetailStatus());
                        deliveryNoteDetailExcel.setExtPurchaserNo(orderIdsList.get(0).getString("orderNumber"));
                        deliveryNoteDetailExcel.setLineNum(deliveryNoteDetailDTO.getLineNum().longValue());
                        deliveryNoteDetailExcel.setCategoryName(orderDetail.getCategoryName());
                        deliveryNoteDetailExcel.setMaterialCode(orderDetail.getMaterialCode());
                        deliveryNoteDetailExcel.setMaterialName(orderDetail.getMaterialName());
                        deliveryNoteDetailExcel.setSpecification(orderDetail.getSpecification());
                        deliveryNoteDetailExcel.setExtBrand(orderDetail.getExtBrand());
                        deliveryNoteDetailExcel.setOrderNum(orderDetail.getOrderNum());
                        deliveryNoteDetailExcel.setNumberRemaining(orderDetail.getOrderNum().subtract(orderDetail.getReceiveSum()));
                        deliveryNoteDetailExcel.setDeliveryQuantity(deliveryNoteDetailDTO.getDeliveryQuantity());
                        deliveryNoteDetailExcel.setWarehouseQuantity(deliveryNoteDetailDTO.getWarehouseQuantity());
                        deliveryNoteDetailExcel.setExtCancelQty(deliveryNoteDetailDTO.getExtCancelQty());
                        deliveryNoteDetailExcel.setRequirementDate(orderDetail.getRequirementDate().toString());
                        deliveryNoteDetailExcel.setExtUserName(orderDetail.getExtUserName());
                        deliveryNoteDetailExcel.setExtUseDepartmentName(orderDetail.getExtUseDepartmentName());
                        deliveryNoteDetailExcel.setCeeaPromiseReceiveDate(orderDetail.getCeeaPromiseReceiveDate());
                        deliveryNoteDetailExcel.setExtWarrantyPeriod(orderDetail.getExtWarrantyPeriod());
                        deliveryNoteDetailExcel.setComments(orderDetail.getComments());
                        deliveryNoteDetailExcel.setExtFinishTime(deliveryNoteDetailDTO.getExtFinishTime());
                        extDeliveryNoteList.add(deliveryNoteDetailExcel);
                    }
                }
            }

        });
        return extDeliveryNoteList;
    }
    private void setExcelResponseProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(rawFileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
    }

}
